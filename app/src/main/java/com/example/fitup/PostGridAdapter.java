package com.example.fitup;

import android.content.Context;
import android.content.Intent; // Import Intent
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostGridAdapter extends RecyclerView.Adapter<PostGridAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private int layoutId;

    public PostGridAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        this.layoutId = R.layout.item_profile_post;
    }

    public PostGridAdapter(Context context, List<Post> postList, int layoutId) {
        this.context = context;
        this.postList = postList;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(post.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.banner_fitness1)
                    .into(holder.imgThumbnail);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OnePostActivity.class);
            intent.putExtra("postId", post.getPostId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgPostThumbnail);
        }
    }
}
