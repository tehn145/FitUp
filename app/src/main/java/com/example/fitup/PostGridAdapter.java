package com.example.fitup;

import android.content.Context;
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

    public PostGridAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Liên kết với file xml vừa sửa ở trên
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Load ảnh từ URL
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(post.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.banner_fitness1) // Ảnh chờ
                    .into(holder.imgThumbnail);
        }
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