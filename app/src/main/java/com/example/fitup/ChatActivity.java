package com.example.fitup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private EditText messageEditText;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private boolean isTrainer;

    private List<Message> messageList = new ArrayList<>();
    private String currentUserId, receiverId, receiverName;
    private String myName = "User";
    private String chatId = null;

    private DatabaseReference rtdbRef;
    private FirebaseFirestore firestore;
    private StorageReference storageRef; // Added Storage
    private DatabaseReference chatInfoRef;
    private ValueEventListener seenListener;

    // Launcher for Image Picker
    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    uploadImage(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish(); return;
        }
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // --- FIREBASE INITIALIZATION WITH EMULATOR ---
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            try {
                // Keep your emulator settings!
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

            // Storage setup
            FirebaseStorage storage = FirebaseStorage.getInstance();
            try {
                // Uncomment if you use Storage emulator, otherwise it uses Live Storage
                // storage.useEmulator("10.0.2.2", 9199);
            } catch (Exception e) {}
            storageRef = storage.getReference();

        } catch (Exception e) {
            Toast.makeText(this, "Firebase Init Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        // ---------------------------------------------

        receiverId = getIntent().getStringExtra("RECEIVER_ID");
        receiverName = getIntent().getStringExtra("RECEIVER_NAME");

        if (getIntent().hasExtra("CHAT_ID")) {
            chatId = getIntent().getStringExtra("CHAT_ID");
        }

        boolean shouldTriggerBooking = getIntent().getBooleanExtra("TRIGGER_BOOKING", false);
        TextView tvTitle = findViewById(R.id.tvUserMess);
        tvTitle.setText(receiverName);
        messageEditText = findViewById(R.id.message_edit_text);

        recyclerView = findViewById(R.id.chat_recycler_view);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(false); // Pushes messages up when keyboard opens
        recyclerView.setLayoutManager(lm);

        chatAdapter = new ChatAdapter(this, messageList, currentUserId);
        recyclerView.setAdapter(chatAdapter);

        findViewById(R.id.imgbtn_Back).setOnClickListener(v -> finish());

        // Session Button
        isTrainer = false;

        firestore.collection("users").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        // If user is a trainer, SHOW the button
                        // If user is a client (or anything else), KEEP IT HIDDEN
                        if ("trainer".equalsIgnoreCase(role)) {
                            isTrainer = true;
                        }
                    }
                });

        findViewById(R.id.btn_book_session).setOnClickListener(v -> checkAndHandleSession());

        // Send Text Button
        findViewById(R.id.send_button).setOnClickListener(v -> {
            String txt = messageEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(txt)) sendMessage(txt, "text");
        });

        // Send Image Button (Make sure this ID exists in your XML)
        findViewById(R.id.btn_send_image).setOnClickListener(v -> {
            pickImage.launch("image/*");
        });

        fetchMyName();

        // Chat Loading Logic
        if (chatId != null) {
            if (shouldTriggerBooking) {
                checkAndHandleSession();
                return;
            }
            listenForMessages();
            attachSeenListener();
        } else {
            findExistingChat(shouldTriggerBooking);
        }
    }

    private void checkAndHandleSession() {
        firestore.collection("sessions")
                .where(Filter.or(
                        Filter.and(Filter.equalTo("trainerId", currentUserId), Filter.equalTo("clientId", receiverId)),
                        Filter.and(Filter.equalTo("trainerId", receiverId), Filter.equalTo("clientId", currentUserId))
                ))
                .whereIn("status", Arrays.asList("pending", "active"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot sessionDoc = queryDocumentSnapshots.getDocuments().get(0);
                        Log.i("Sudoku", sessionDoc.getId());
                        SessionDetailsFragment bottomSheet = SessionDetailsFragment.newInstance(sessionDoc.getId());
                        bottomSheet.show(getSupportFragmentManager(), "SessionDetails");
                    } else {
                        if (!isTrainer) {
                            Toast.makeText(this, "There's no session available!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent(ChatActivity.this, BookSessionActivity.class);
                        intent.putExtra("RECEIVER_ID", receiverId);
                        intent.putExtra("RECEIVER_NAME", receiverName);
                        intent.putExtra("CHAT_ID", chatId);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void uploadImage(Uri imageUri) {
        if (chatId == null) createChatStructure();

        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference fileRef = storageRef.child("chat_images").child(chatId).child(fileName);

        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        sendMessage(uri.toString(), "image");
                    });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ChatActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // --- CORE CHAT LOGIC ---

    private void fetchMyName() {
        firestore.collection("users").document(currentUserId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.getString("name") != null) {
                        myName = doc.getString("name");
                    }
                });
    }

    private void findExistingChat(boolean forceBooking) {
        rtdbRef.child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot chat : snapshot.getChildren()) {
                    if (chat.child("members").hasChild(currentUserId) &&
                            chat.child("members").hasChild(receiverId)) {
                        chatId = chat.getKey();

                        if (forceBooking) {
                            checkAndHandleSession();
                            return;
                        }

                        listenForMessages();
                        attachSeenListener();
                        found = true;
                        return;
                    }
                }

                if (!found) {

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void createChatStructure() {
        if (chatId != null) return;

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

    private void sendMessage(String content, String type) {
        if (chatId == null) {
            createChatStructure();
        }

        long timestamp = System.currentTimeMillis();
        DatabaseReference msgRef = rtdbRef.child("messages").child(chatId).push();

        // Pass type to Constructor
        Message msg = new Message(currentUserId, receiverId, content, timestamp, type);

        msgRef.setValue(msg).addOnFailureListener(e ->
                Toast.makeText(ChatActivity.this, "Error sending: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );

        DatabaseReference chatRef = rtdbRef.child("chats").child(chatId);
        Map<String, Object> updates = new HashMap<>();

        if ("image".equals(type)) {
            updates.put("lastMessage", "[Image]");
        } else {
            updates.put("lastMessage", content);
        }

        updates.put("lastSenderId", currentUserId);
        updates.put("lastTimestamp", timestamp);
        updates.put("isRead", false);
        chatRef.updateChildren(updates);

        if ("text".equals(type)) {
            messageEditText.setText("");
        }
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
                        else m.setShowDateHeader(false);

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
