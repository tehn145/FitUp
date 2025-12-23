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
import java.util.Collections;
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
    GeminiApiService apiService;

    String API_KEY = "AIzaSyD_uQaU73h-nt2bw_T1QI1l2BK2xkMy4NA";
    String BASE_URL = "https://generativelanguage.googleapis.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant_chat);

        recyclerView = findViewById(R.id.recyclerChat);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        chatList = new ArrayList<>();
        chatList.add(new GeminiModels.ChatMessage("Chào bạn! Mình là Fitty. Bạn cần mình tư vấn bài tập hay dinh dưỡng không? Mình rất sẵn lòng giải đáp thắc mắc của bạn!", false));

        adapter = new ChatAssistantAdapter(chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(GeminiApiService.class);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo API: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
//        callGemini(msg);
        callFakeAI(msg);
    }

    private void callFakeAI(String userMsg) {
        recyclerView.postDelayed(() -> {
            String reply = FakeAIService.getReply(userMsg);
            chatList.add(new GeminiModels.ChatMessage(reply, false));
            adapter.notifyItemInserted(chatList.size() - 1);
            recyclerView.scrollToPosition(chatList.size() - 1);
        }, 800);
    }

//    // Thay thế toàn bộ hàm callGemini cũ bằng hàm này
//    private void callGemini(String userMsg) {
//        Log.d("API_TEST", "Đang gửi tin nhắn: " + userMsg); // Log kiểm tra bắt đầu gửi
//
//        String prompt = "Bạn là Fitty - một HLV Gym cá nhân. Hãy trả lời ngắn gọn: " + userMsg;
//
//        GeminiModels.Part part = new GeminiModels.Part(prompt);
//        GeminiModels.Content content = new GeminiModels.Content("user", Collections.singletonList(part));
//        GeminiModels.Request request = new GeminiModels.Request(Collections.singletonList(content));
//
//        apiService.getResponse(API_KEY, request).enqueue(new Callback<GeminiModels.Response>() {
//            @Override
//            public void onResponse(Call<GeminiModels.Response> call, Response<GeminiModels.Response> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    try {
//                        // Log dữ liệu trả về thành công
//                        Log.d("API_TEST", "Thành công! Data: " + response.body().toString());
//
//                        if (response.body().candidates != null && !response.body().candidates.isEmpty()) {
//                            String botReply = response.body().candidates.get(0).content.parts.get(0).text;
//
//                            // Cập nhật UI
//                            chatList.add(new GeminiModels.ChatMessage(botReply, false));
//                            adapter.notifyItemInserted(chatList.size() - 1);
//                            recyclerView.scrollToPosition(chatList.size() - 1);
//                        } else {
//                            Log.e("API_TEST", "Danh sách candidates bị rỗng (Google chặn hoặc không trả lời)");
//                        }
//
//                    } catch (Exception e) {
//                        Log.e("API_TEST", "Lỗi phân tích JSON: " + e.getMessage());
//                    }
//                } else {
//                    // Log lỗi từ Google (quan trọng)
//                    try {
//                        String errorBody = response.errorBody().string();
//                        Log.e("API_TEST", "Lỗi API (" + response.code() + "): " + errorBody);
//                    } catch (Exception e) {
//                        Log.e("API_TEST", "Lỗi API (" + response.code() + ") - Không đọc được chi tiết lỗi");
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GeminiModels.Response> call, Throwable t) {
//                Log.e("API_TEST", "Lỗi kết nối mạng: " + t.getMessage());
//                t.printStackTrace();
//            }
//        });
    }