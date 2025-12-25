package com.example.fitup;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private EditText messageEditText;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList = new ArrayList<>();
    private String currentUserId, receiverId, receiverName;
    private String myName = "User";
    private String chatId = null;

    private DatabaseReference rtdbRef;
    private FirebaseFirestore firestore;
    private DatabaseReference chatInfoRef;
    private ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish(); return;
        }
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            try {
                database.useEmulator("10.0.2.2", 9000);
            } catch (Exception e) {}
            rtdbRef = database.getReference();

            firestore = FirebaseFirestore.getInstance();
            try {
                firestore.useEmulator("10.0.2.2", 8080);
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(false)
                        .build();
                firestore.setFirestoreSettings(settings);
            } catch (Exception e) {}

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi Emulator: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        receiverId = getIntent().getStringExtra("RECEIVER_ID");
        receiverName = getIntent().getStringExtra("RECEIVER_NAME");
        if (getIntent().hasExtra("CHAT_ID")) {
            chatId = getIntent().getStringExtra("CHAT_ID");
        }

        TextView tvTitle = findViewById(R.id.tvUserMess);
        tvTitle.setText(receiverName);
        messageEditText = findViewById(R.id.message_edit_text);

        recyclerView = findViewById(R.id.chat_recycler_view);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(false);
        recyclerView.setLayoutManager(lm);

        chatAdapter = new ChatAdapter(this, messageList, currentUserId);
        recyclerView.setAdapter(chatAdapter);

        findViewById(R.id.imgbtn_Back).setOnClickListener(v -> finish());
        findViewById(R.id.send_button).setOnClickListener(v -> {
            String txt = messageEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(txt)) sendMessage(txt);
        });

        fetchMyName();
        if (chatId != null) {
            listenForMessages();
            attachSeenListener();
        } else {
            findExistingChat();
        }
    }

    private void fetchMyName() {
        firestore.collection("users").document(currentUserId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.getString("name") != null) {
                        myName = doc.getString("name");
                    }
                });
    }

    private void findExistingChat() {
        rtdbRef.child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot chat : snapshot.getChildren()) {
                    if (chat.child("members").hasChild(currentUserId) &&
                            chat.child("members").hasChild(receiverId)) {
                        chatId = chat.getKey();
                        listenForMessages();
                        attachSeenListener();
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void sendMessage(String text) {
        if (chatId == null) {
            DatabaseReference newChat = rtdbRef.child("chats").push();
            chatId = newChat.getKey();

            Map<String, Object> chatInfo = new HashMap<>();
            Map<String, Boolean> members = new HashMap<>();
            members.put(currentUserId, true);
            members.put(receiverId, true);
            chatInfo.put("members", members);

            Map<String, String> names = new HashMap<>();
            names.put(currentUserId, myName);
            names.put(receiverId, receiverName);
            chatInfo.put("memberNames", names);

            newChat.updateChildren(chatInfo);
            listenForMessages();
            attachSeenListener();
        }

        long timestamp = System.currentTimeMillis();
        DatabaseReference msgRef = rtdbRef.child("messages").child(chatId).push();
        Message msg = new Message(currentUserId, receiverId, text, timestamp);

        msgRef.setValue(msg).addOnFailureListener(e ->
                Toast.makeText(ChatActivity.this, "Lỗi gửi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );

        DatabaseReference chatRef = rtdbRef.child("chats").child(chatId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastMessage", text);
        updates.put("lastSenderId", currentUserId);
        updates.put("lastTimestamp", timestamp);
        updates.put("isRead", false);
        chatRef.updateChildren(updates);

        messageEditText.setText("");
    }

    private void listenForMessages() {
        if (chatId == null) return;
        rtdbRef.child("messages").child(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                long lastDay = 0;
                for (DataSnapshot d : snapshot.getChildren()) {
                    Message m = d.getValue(Message.class);
                    if (m != null) {
                        long t = m.getTimestamp();
                        long day = t - (t % 86400000);
                        if (day > lastDay) { m.setShowDateHeader(true); lastDay = day; }
                        messageList.add(m);
                    }
                }
                chatAdapter.setMessages(messageList);
                if (!messageList.isEmpty()) recyclerView.scrollToPosition(messageList.size() - 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void attachSeenListener() {
        if (chatId == null) return;

        chatInfoRef = rtdbRef.child("chats").child(chatId);

        seenListener = chatInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String lastSenderId = snapshot.child("lastSenderId").getValue(String.class);
                    Boolean isRead = snapshot.child("isRead").getValue(Boolean.class);

                    if (lastSenderId != null && !lastSenderId.equals(currentUserId)) {
                        if (isRead == null || !isRead) {
                            chatInfoRef.child("isRead").setValue(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (chatInfoRef != null && seenListener != null) {
            chatInfoRef.removeEventListener(seenListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (chatId != null) {
            attachSeenListener();
        }
    }
}