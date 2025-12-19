package com.example.fitup;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Set Username and Text
        holder.tvUsername.setText(comment.getUsername());
        holder.tvText.setText(comment.getText());

        // Set Timestamp (e.g., "2 hours ago")
        if (comment.getTimestamp() != null) {
            long time = comment.getTimestamp().toDate().getTime();
            long now = System.currentTimeMillis();
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            holder.tvTime.setText(timeAgo);
        } else {
            holder.tvTime.setText("Just now");
        }

        // Load Avatar using Glide
        Glide.with(context)
                .load(comment.getAvatarUrl())
                .placeholder(R.drawable.defaultavt) // Ensure this drawable exists
                .error(R.drawable.defaultavt)
                .into(holder.ivAvatar);

        // Handle Options Button Click (The "..." button)
        holder.btnOptions.setOnClickListener(v -> {
            // Placeholder logic for options (e.g., delete/report)
            Toast.makeText(context, "Options clicked for: " + comment.getUsername(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivAvatar;
        TextView tvUsername, tvText, tvTime;
        ImageView btnOptions;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.iv_comment_avatar);
            tvUsername = itemView.findViewById(R.id.tv_comment_username);
            tvText = itemView.findViewById(R.id.tv_comment_text);
            tvTime = itemView.findViewById(R.id.tv_comment_time);
            btnOptions = itemView.findViewById(R.id.btn_comment_options);
        }
    }
}
