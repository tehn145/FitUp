package com.example.fitup;import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        // --- LIKE LOGIC ---
        if (currentUser != null && post.getPostId() != null) {
            String postId = post.getPostId();
            String userId = currentUser.getUid();

            // Reference to the subcollection
            DocumentReference postRef = db.collection("posts").document(postId);
            DocumentReference likeRef = postRef.collection("likes").document(userId);

            // 1. INITIAL STATE CHECK: Check if user already liked this post to set initial color
            // This runs when the list first loads
            likeRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    // SET ORANGE (Liked)
                    holder.tvLikes.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(context.getResources().getColor(android.R.color.holo_orange_dark)));
                    holder.tvLikes.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                    holder.tvLikes.setTag(true); // Tag as liked
                } else {
                    // SET WHITE (Unliked)
                    holder.tvLikes.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(context.getResources().getColor(android.R.color.white)));
                    holder.tvLikes.setTextColor(context.getResources().getColor(android.R.color.white));
                    holder.tvLikes.setTag(false); // Tag as unliked
                }
            });

            // 2. CLICK LISTENER
            holder.tvLikes.setOnClickListener(v -> {
                // Disable temporarily to prevent spamming
                holder.tvLikes.setEnabled(false);

                // Check current state from Tag (default to false if null)
                boolean currentlyLiked = holder.tvLikes.getTag() != null && (boolean) holder.tvLikes.getTag();

                db.runTransaction((Transaction.Function<Void>) transaction -> {
                    // We don't strictly need to read inside the transaction for the logic switch
                    // if we trust the UI state, but reading ensures data consistency.
                    DocumentSnapshot likeSnapshot = transaction.get(likeRef);

                    if (likeSnapshot.exists()) {
                        // UNLIKE operation
                        transaction.delete(likeRef);
                        transaction.update(postRef, "likeCount", FieldValue.increment(-1));
                    } else {
                        // LIKE operation
                        Map<String, Object> likeData = new HashMap<>();
                        likeData.put("timestamp", FieldValue.serverTimestamp());
                        transaction.set(likeRef, likeData);
                        transaction.update(postRef, "likeCount", FieldValue.increment(1));
                    }
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    // Transaction successful
                    holder.tvLikes.setEnabled(true);

                    // Toggle the state based on what we just did
                    boolean newLikedState = !currentlyLiked;
                    holder.tvLikes.setTag(newLikedState);

                    // Update Count visually based on the operation we just performed
                    int currentCount = post.getLikeCount();
                    if (newLikedState) {
                        // We just Liked: +1
                        post.setLikeCount(currentCount + 1);

                        // Change Color to ORANGE
                        holder.tvLikes.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(context.getResources().getColor(android.R.color.holo_orange_dark)));
                        holder.tvLikes.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                    } else {
                        // We just Unliked: -1
                        post.setLikeCount(Math.max(0, currentCount - 1));

                        // Change Color to WHITE
                        holder.tvLikes.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(context.getResources().getColor(android.R.color.white)));
                        holder.tvLikes.setTextColor(context.getResources().getColor(android.R.color.white));
                    }

                    // Set text from the model, not by parsing the previous text value
                    holder.tvLikes.setText(String.valueOf(post.getLikeCount()));

                }).addOnFailureListener(e -> {
                    holder.tvLikes.setEnabled(true);
                    Log.e("PostAdapter", "Like transaction failed", e);
                    Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show();
                });
            });
        }

        holder.tvComments.setOnClickListener(v -> {
            if (context instanceof FragmentActivity && post.getPostId() != null) {
                CommentsFragment commentsFragment = CommentsFragment.newInstance(post.getPostId());
                commentsFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "CommentsFragment");
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updatePosts(List<Post> newPosts) {
        Log.d("DEBUG_ADAPTER", "Updating adapter with " + newPosts.size() + " items");
        this.postList.clear();
        this.postList.addAll(newPosts);
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivPostImage;
        TextView tvUserName, tvContent, tvLikes, tvComments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ensure these IDs match your list_item_post.xml
            ivAvatar = itemView.findViewById(R.id.iv_user_avatar);
            ivPostImage = itemView.findViewById(R.id.iv_post_image);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvContent = itemView.findViewById(R.id.tv_post_description);

            // Reusing existing IDs from your provided code
            tvLikes = itemView.findViewById(R.id.btn_like);
            tvComments = itemView.findViewById(R.id.btn_comment);
        }
    }
}
