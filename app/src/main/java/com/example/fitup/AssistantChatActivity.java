package com.example.fitup;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AssistantChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText edtMessage;
    ImageButton btnSend;
    ChatAssistantAdapter adapter;
    List<GeminiModels.ChatMessage> chatList;

    GroqApiService apiService;
    FirebaseFirestore db; //RAG
    FirebaseAuth mAuth; //RAG

    String GROQ_API_KEY = BuildConfig.GROQ_API_KEY;
    String BASE_URL = "https://api.groq.com/openai/v1/";
    String MODEL_ID = "llama-3.3-70b-versatile";

    interface OnRagContextReady {
        void onContextReady(String contextData);
    }

    interface OnContextReady {
        void onResult(String context);
    }

//    interface OnDataReady {
//        void onDataReady(String userContext, String ragContext);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant_chat);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerChat);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        chatList = new ArrayList<>();
        chatList.add(new GeminiModels.ChatMessage("Chào bạn! Mình là Fitty. Bạn cần mình tư vấn bài tập hay dinh dưỡng không? Mình rất sẵn lòng để giúp đỡ", false));

        adapter = new ChatAssistantAdapter(chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(GroqApiService.class);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi Retrofit: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        btnSend.setOnClickListener(v -> {
            String msg = edtMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                sendMessage(msg);
            }
        });
    }

    private void fetchUserDailyStatus(OnContextReady callback) {
        if (mAuth.getCurrentUser() == null) {
            callback.onResult("");
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .collection("daily_checkins")
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        QueryDocumentSnapshot doc = querySnapshot.iterator().next();
                        StringBuilder sb = new StringBuilder();
                        sb.append("Tình trạng sức khỏe gần đây của người dùng (Daily Check-in):\n");

                        if(doc.contains("q_sleep_hours")) sb.append("- Giấc ngủ: ").append(doc.getString("q_sleep_hours")).append("\n");
                        if(doc.contains("q_sleep_quality")) sb.append("- Chất lượng ngủ: ").append(doc.getString("q_sleep_quality")).append("\n");
                        if(doc.contains("q_water")) sb.append("- Uống nước: ").append(doc.getString("q_water")).append("\n");
                        if(doc.contains("q_energy")) sb.append("- Mức năng lượng: ").append(doc.getString("q_energy")).append("\n");
                        if(doc.contains("q_training")) sb.append("- Tập luyện hôm qua: ").append(doc.getString("q_training")).append("\n");

                        callback.onResult(sb.toString());
                    } else {
                        callback.onResult("");
                    }
                })
                .addOnFailureListener(e -> callback.onResult(""));
    }

    private void sendMessage(String msg) {
        chatList.add(new GeminiModels.ChatMessage(msg, true));
        adapter.notifyItemInserted(chatList.size() - 1);
        recyclerView.scrollToPosition(chatList.size() - 1);
        edtMessage.setText("");

        fetchUserDailyStatus(new OnContextReady() {
            @Override
            public void onResult(String userPersonalContext) {
                searchKnowledgeBase(msg, new OnRagContextReady() {
                    @Override
                    public void onContextReady(String knowledgeBaseContext) {
                        callGroq(msg, userPersonalContext, knowledgeBaseContext);
                    }
                });
            }
        });
    }

    private void searchKnowledgeBase(String userMsg, OnRagContextReady callback) {
        String[] words = userMsg.toLowerCase().split("\\s+");

        Set<String> uniqueWords = new HashSet<>();
        for (String w : words) {
            if (w.length() > 2) uniqueWords.add(w);
        }

        List<String> searchKeywords = new ArrayList<>(uniqueWords);

        if (searchKeywords.size() > 10) {
            searchKeywords = searchKeywords.subList(0, 10);
        }

        if (searchKeywords.isEmpty()) {
            callback.onContextReady("");
            return;
        }

        Log.d("RAG_TEST", "Đang tìm keywords: " + searchKeywords.toString());

        db.collection("fitness_knowledge")
                .whereArrayContainsAny("keywords", searchKeywords)
                .limit(3)
                .get()
                .addOnCompleteListener(task -> {
                    StringBuilder contextBuilder = new StringBuilder();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FitnessKnowledge info = document.toObject(FitnessKnowledge.class);
                            if (info.getContent() != null) {
                                contextBuilder.append("- ").append(info.getContent()).append("\n");
                            }
                        }
                    } else {
                        Log.e("RAG_TEST", "Lỗi tìm kiếm Firestore: ", task.getException());
                    }

                    String resultContext = contextBuilder.toString();
                    Log.d("RAG_TEST", "Context tìm được: " + resultContext);
                    callback.onContextReady(resultContext);
                });
    }

    private void callGroq(String userMsg, String userPersonalContext, String knowledgeBaseContext) {
        List<GroqModels.Message> messages = new ArrayList<>();

        StringBuilder systemPrompt = new StringBuilder();
        systemPrompt.append("Bạn là Fitty - một HLV Gym cá nhân nhiệt tình. ");

        if (!userPersonalContext.isEmpty()) {
            systemPrompt.append("\n\n[THÔNG TIN SỨC KHỎE NGƯỜI DÙNG HÔM NAY]\n")
                    .append(userPersonalContext)
                    .append("=> LƯU Ý QUAN TRỌNG: Hãy điều chỉnh lời khuyên dựa trên tình trạng này (Ví dụ: ngủ ít/mệt -> khuyên tập nhẹ, khuyên ngủ sớm; uống ít nước -> nhắc uống nước).\n");
        }

        if (!knowledgeBaseContext.isEmpty()) {
            systemPrompt.append("\n\n[KIẾN THỨC CHUYÊN MÔN THAM KHẢO]\n")
                    .append("--- BẮT ĐẦU ---\n")
                    .append(knowledgeBaseContext)
                    .append("--- KẾT THÚC ---\n")
                    .append("Hãy ưu tiên dùng thông tin trên để trả lời chính xác.");
        }

        systemPrompt.append("\n\nHãy trả lời ngắn gọn, hữu ích bằng tiếng Việt.");

        messages.add(new GroqModels.Message("system", systemPrompt.toString()));
        messages.add(new GroqModels.Message("user", userMsg));

        GroqModels.Request requestBody = new GroqModels.Request(MODEL_ID, messages);
        String authHeader = "Bearer " + GROQ_API_KEY;

        apiService.getChatCompletion(authHeader, requestBody).enqueue(new Callback<GroqModels.Response>() {
            @Override
            public void onResponse(Call<GroqModels.Response> call, Response<GroqModels.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        if (!response.body().choices.isEmpty()) {
                            String botReply = response.body().choices.get(0).message.content;

                            chatList.add(new GeminiModels.ChatMessage(botReply, false));
                            adapter.notifyItemInserted(chatList.size() - 1);
                            recyclerView.scrollToPosition(chatList.size() - 1);
                        }
                    } catch (Exception e) {
                        Log.e("API_TEST", "Lỗi xử lý data: " + e.getMessage());
                    }
                } else {
                    try {
                        Log.e("API_TEST", "Lỗi API " + response.code() + ": " + response.errorBody().string());
                        Toast.makeText(AssistantChatActivity.this, "Lỗi Server: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {}
                }
            }

            @Override
            public void onFailure(Call<GroqModels.Response> call, Throwable t) {
                Log.e("API_TEST", "Lỗi mạng: " + t.getMessage());
                Toast.makeText(AssistantChatActivity.this, "Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}