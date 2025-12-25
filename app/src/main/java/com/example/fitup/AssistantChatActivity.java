package com.example.fitup;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
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

    String GROQ_API_KEY = BuildConfig.GROQ_API_KEY;
    String BASE_URL = "https://api.groq.com/openai/v1/";
    String MODEL_ID = "llama-3.3-70b-versatile";
    //hoac nhanh chong String MODEL_ID = "llama-3.1-8b-instant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant_chat);

        recyclerView = findViewById(R.id.recyclerChat);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        chatList = new ArrayList<>();
        chatList.add(new GeminiModels.ChatMessage("Chào bạn! Mình là Fitty. Bạn cần mình tư vấn bài tập hay dinh dưỡng không?", false));

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

    private void sendMessage(String msg) {
        chatList.add(new GeminiModels.ChatMessage(msg, true));
        adapter.notifyItemInserted(chatList.size() - 1);
        recyclerView.scrollToPosition(chatList.size() - 1);
        edtMessage.setText("");

        callGroq(msg);
    }

    private void callGroq(String userMsg) {
        Log.d("API_TEST", "Đang gửi tin đến Groq...");
        List<GroqModels.Message> messages = new ArrayList<>();
        //System Prompt
        messages.add(new GroqModels.Message("system", "Bạn là Fitty - một HLV Gym cá nhân nhiệt tình và chuyên nghiệp. Hãy trả lời ngắn gọn bằng tiếng Việt."));
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
                    // Logcat
                    try {
                        Log.e("API_TEST", "Lỗi API " + response.code() + ": " + response.errorBody().string());
                        Toast.makeText(AssistantChatActivity.this, "Lỗi API: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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