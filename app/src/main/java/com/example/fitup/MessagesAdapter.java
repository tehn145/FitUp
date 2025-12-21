package com.example.fitup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ChatViewHolder> {

    private List<ChatSummary> chatList;
    private String currentUserId;
    private OnChatClickListener listener;
    private Context context;

    public interface OnChatClickListener {
        void onChatClick(ChatSummary chat);
    }

    public MessagesAdapter(Context context, List<ChatSummary> chatList, String currentUserId, OnChatClickListener listener) {
        this.context = context;
        this.chatList = chatList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatSummary chat = chatList.get(position);

        String otherName = chat.getOtherUserName(currentUserId);
        holder.tvName.setText(otherName);

        holder.tvTime.setText(TimeUtil.formatFullDate(chat.lastTimestamp));

        String previewText = chat.lastMessage;
        boolean isMyMessage = chat.lastSenderId != null && chat.lastSenderId.equals(currentUserId);

        if (isMyMessage) {
            holder.tvLastMessage.setText("You: " + previewText);
            holder.tvLastMessage.setTextColor(Color.GRAY);
            holder.tvLastMessage.setTypeface(null, Typeface.NORMAL);
        } else {
            holder.tvLastMessage.setText(previewText);
            if (!chat.isRead) {
                holder.tvLastMessage.setTextColor(Color.WHITE);
                holder.tvLastMessage.setTypeface(null, Typeface.BOLD);
            } else {
                holder.tvLastMessage.setTextColor(Color.GRAY);
                holder.tvLastMessage.setTypeface(null, Typeface.NORMAL);
            }
        }

        String otherUserId = chat.getOtherUserId(currentUserId);
        if (otherUserId != null) {
            FirebaseFirestore.getInstance().collection("users").document(otherUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String avatarUrl = documentSnapshot.getString("avatar");
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(context)
                                        .load(avatarUrl)
                                        .placeholder(R.drawable.defaultavt)
                                        .into(holder.imgAvatar);
                            } else {
                                holder.imgAvatar.setImageResource(R.drawable.defaultavt);
                            }
                        }
                    });
        }

        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastMessage, tvTime;
        ImageView imgAvatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}