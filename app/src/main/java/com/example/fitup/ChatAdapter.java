package com.example.fitup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private static final int MSG_TYPE_SENT = 0;
    private static final int MSG_TYPE_RECEIVED = 1;
    private Context context;
    private List<Message> messages;
    private String currentUserId;

    public ChatAdapter(Context context, List<Message> messages, String currentUserId) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    public void setMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderId().equals(currentUserId)) return MSG_TYPE_SENT;
        return MSG_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                viewType == MSG_TYPE_SENT ? R.layout.item_message_sent : R.layout.item_message_received,
                parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.messageText.setText(message.getText());

        holder.timeText.setText(TimeUtil.format(message.getTimestamp()));

        if (holder.dateHeader != null) {
            if (message.isShowDateHeader()) {
                holder.dateHeader.setVisibility(View.VISIBLE);

                holder.dateHeader.setText(TimeUtil.formatDateHeader(message.getTimestamp()));
            } else {
                holder.dateHeader.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() { return messages.size(); }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, dateHeader;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            timeText = itemView.findViewById(R.id.time_text);
            dateHeader = itemView.findViewById(R.id.date_header);
        }
    }
}