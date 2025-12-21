package com.example.fitup;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton; // Import Added
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        holder.tvUserName.setText(post.getUserName());
        holder.tvContent.setText(post.getContent());

        // These TextViews likely have a drawable (icon) attached via XML (drawableStart/Left)
        holder.tvLikes.setText(String.valueOf(post.getLikeCount()));
        holder.tvComments.setText(String.valueOf(post.getCommentCount()));

        // Format Timestamp
        if (post.getTimestamp() != null) {
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    post.getTimestamp().toDate().getTime(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS);
            // holder.tvTime.setText(timeAgo);
        }

        // Load Avatar
        Glide.with(context)
                .load(post.getUserAvatar())
                .placeholder(R.drawable.defaultavt) // Ensure you have a placeholder
                .circleCrop()
                .into(holder.ivAvatar);

        // Load Post Image
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.ivPostImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(post.getImageUrl()).into(holder.ivPostImage);
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }

        // --- OPTIONS MENU LOGIC (New) ---
        holder.btnOptions.setOnClickListener(v -> {
            if (context instanceof FragmentActivity && post.getPostId() != null) {
                // Pass postId and the author's userId to check permission
                // Assuming your Post class has a getUserId() method.
                PostOptionsFragment optionsFragment = PostOptionsFragment.newInstance(post.getPostId(), post.getUserId());
                optionsFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "PostOptionsFragment");
            }
        });

        if (currentUser != null && post.getPostId() != null) {
            String postId = post.getPostId();
            String userId = currentUser.getUid();

            DocumentReference postRef = db.collection("posts").document(postId);
            DocumentReference likeRef = postRef.collection("likes").document(userId);

            // 1. INITIAL STATE CHECK
            likeRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    holder.tvLikes.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(context.getResources().getColor(android.R.color.holo_orange_dark)));
                    holder.tvLikes.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                    holder.tvLikes.setTag(true);
                } else {
                    holder.tvLikes.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(context.getResources().getColor(android.R.color.white)));
                    holder.tvLikes.setTextColor(context.getResources().getColor(android.R.color.white));
                    holder.tvLikes.setTag(false);
                }
            });

            // 2. LIKE CLICK LISTENER
            holder.tvLikes.setOnClickListener(v -> {
                holder.tvLikes.setEnabled(false);
                boolean currentlyLiked = holder.tvLikes.getTag() != null && (boolean) holder.tvLikes.getTag();

                db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot likeSnapshot = transaction.get(likeRef);

                    if (likeSnapshot.exists()) {
                        transaction.delete(likeRef);
                        transaction.update(postRef, "likeCount", FieldValue.increment(-1));
                    } else {
                        Map<String, Object> likeData = new HashMap<>();
                        likeData.put("timestamp", FieldValue.serverTimestamp());
                        transaction.set(likeRef, likeData);
                        transaction.update(postRef, "likeCount", FieldValue.increment(1));
                    }
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    holder.tvLikes.setEnabled(true);
                    boolean newLikedState = !currentlyLiked;
                    holder.tvLikes.setTag(newLikedState);

                    int currentCount = post.getLikeCount();
                    if (newLikedState) {
                        post.setLikeCount(currentCount + 1);
                        holder.tvLikes.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(context.getResources().getColor(android.R.color.holo_orange_dark)));
                        holder.tvLikes.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                    } else {
                        post.setLikeCount(Math.max(0, currentCount - 1));
                        holder.tvLikes.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(context.getResources().getColor(android.R.color.white)));
                        holder.tvLikes.setTextColor(context.getResources().getColor(android.R.color.white));
                    }
                    holder.tvLikes.setText(String.valueOf(post.getLikeCount()));

                }).addOnFailureListener(e -> {
                    holder.tvLikes.setEnabled(true);
                    Log.e("PostAdapter", "Like transaction failed", e);
                    Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show();
                });
            });
        }

        // COMMENT LOGIC
        holder.tvComments.setOnClickListener(v -> {
            if (context instanceof FragmentActivity && post.getPostId() != null) {
                CommentsFragment commentsFragment = CommentsFragment.newInstance(post.getPostId());
                // Optional: Apply style for rounded corners if defined
                // commentsFragment.setStyle(androidx.fragment.app.DialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme);
                commentsFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "CommentsFragment");
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updatePosts(List<Post> newPosts) {
        this.postList.clear();
        this.postList.addAll(newPosts);
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivPostImage;
        TextView tvUserName, tvContent, tvLikes, tvComments;
        ImageButton btnOptions; // Added

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_user_avatar);
            ivPostImage = itemView.findViewById(R.id.iv_post_image);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvContent = itemView.findViewById(R.id.tv_post_description);
            tvLikes = itemView.findViewById(R.id.btn_like);
            tvComments = itemView.findViewById(R.id.btn_comment);
            btnOptions = itemView.findViewById(R.id.btn_options); // Bind Added
        }
    }
}
