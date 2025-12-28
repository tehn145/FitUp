package com.example.fitup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private final List<Integer> bannerImages;

    public BannerAdapter(List<Integer> bannerImages) {
        this.bannerImages = bannerImages;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.bind(bannerImages.get(position));
    }

    @Override
    public int getItemCount() {
        return bannerImages != null ? bannerImages.size() : 0;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgBanner);
        }

        void bind(Integer imageResId) {
            imageView.setImageResource(imageResId);
        }
    }
}