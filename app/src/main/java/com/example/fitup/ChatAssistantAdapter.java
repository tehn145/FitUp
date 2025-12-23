package com.example.fitup; // <--- ĐỔI TÊN PACKAGE

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAssistantAdapter extends RecyclerView.Adapter<ChatAssistantAdapter.ChatViewHolder> {

    private List<GeminiModels.ChatMessage> list;

    public ChatAssistantAdapter(List<GeminiModels.ChatMessage> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_assistant, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        GeminiModels.ChatMessage chat = list.get(position);
        if (chat.isUser) {
            holder.layoutUser.setVisibility(View.VISIBLE);
            holder.layoutBot.setVisibility(View.GONE);
            holder.tvUserMsg.setText(chat.message);
        } else {
            holder.layoutUser.setVisibility(View.GONE);
            holder.layoutBot.setVisibility(View.VISIBLE);
            holder.tvBotMsg.setText(chat.message);
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutBot, layoutUser;
        TextView tvBotMsg, tvUserMsg;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutBot = itemView.findViewById(R.id.layout_bot);
            layoutUser = itemView.findViewById(R.id.layout_user);
            tvBotMsg = itemView.findViewById(R.id.tv_bot_msg);
            tvUserMsg = itemView.findViewById(R.id.tv_user_msg);
        }
    }
}