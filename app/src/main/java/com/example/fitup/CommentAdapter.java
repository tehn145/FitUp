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
import androidx.fragment.app.FragmentActivity; // Import needed
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;
    // We need the postId so we know which post's subcollection to delete from
    private String postId;

    // Update Constructor to accept postId (or handle it differently if postId isn't available here)
    // For now, assuming you might need to pass postId.
    // If your Comment object has postId inside it, you don't need this extra variable.
    // Based on your previous code, Comment doesn't seem to store postId, so we pass it in.
    public CommentAdapter(Context context, List<Comment> commentList, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.postId = postId;
    }

    // Fallback constructor if you haven't updated CommentsFragment yet
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

        // Set Timestamp
        if (comment.getTimestamp() != null) {
            long time = comment.getTimestamp().toDate().getTime();
            long now = System.currentTimeMillis();
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            holder.tvTime.setText(timeAgo);
        } else {
            holder.tvTime.setText("Just now");
        }

        // Load Avatar
        Glide.with(context)
                .load(comment.getAvatarUrl())
                .placeholder(R.drawable.defaultavt)
                .error(R.drawable.defaultavt)
                .into(holder.ivAvatar);

        // --- OPTIONS MENU LOGIC ---
        holder.btnOptions.setOnClickListener(v -> {
            // We need the context to be a FragmentActivity to show the BottomSheet
            if (context instanceof FragmentActivity) {
                // We need 3 things:
                // 1. postId (parent doc)
                // 2. commentId (specific doc to delete) - **See note below**
                // 3. userId (author of comment, for permission check)

                // NOTE: You need to ensure your Comment object has the Firestore Document ID.
                // Since your model might not have it, we often set it manually when fetching.
                // Assuming comment.getCommentId() exists or you handle it.
                // If not, see step 2 below.

                String commentId = comment.getCommentId(); // You might need to add this to your model

                CommentOptionsFragment optionsFragment = CommentOptionsFragment.newInstance(
                        postId,
                        commentId,
                        comment.getUserId()
                );

                optionsFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "CommentOptionsFragment");
            }
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
