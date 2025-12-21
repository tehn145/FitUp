package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessagesAdapter adapter;
    private List<ChatSummary> chatList = new ArrayList<>();
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView = view.findViewById(R.id.rvMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MessagesAdapter(getContext(), chatList, currentUserId, chat -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("CHAT_ID", chat.chatId);
            intent.putExtra("RECEIVER_NAME", chat.getOtherUserName(currentUserId));
            intent.putExtra("RECEIVER_ID", chat.getOtherUserId(currentUserId));
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadChats();
    }

    private void loadChats() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        try {
            db.useEmulator("10.0.2.2", 9000);
        } catch (Exception e) {

        }
        DatabaseReference ref = db.getReference("chats");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        if (data.child("members").hasChild(currentUserId)) {
                            ChatSummary chat = data.getValue(ChatSummary.class);
                            if (chat != null) {
                                chat.chatId = data.getKey();
                                if (chat.lastMessage != null && !chat.lastMessage.isEmpty()) {
                                    chatList.add(chat);
                                }
                            }
                        }
                    } catch (Exception e) {}
                }
                Collections.sort(chatList, (a, b) -> Long.compare(b.lastTimestamp, a.lastTimestamp));
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}