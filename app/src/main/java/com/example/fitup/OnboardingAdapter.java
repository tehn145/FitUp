package com.example.fitup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final List<OnboardingItem> onboardingItems;

    public OnboardingAdapter(List<OnboardingItem> onboardingItems) {
        this.onboardingItems = onboardingItems;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.bind(onboardingItems.get(position));
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgv_Background;
        private final TextView tv_Tittle;
        private final TextView tv_Description;

        OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgv_Background = itemView.findViewById(R.id.imgv_Background);
            tv_Tittle = itemView.findViewById(R.id.tv_Tittle);
            tv_Description = itemView.findViewById(R.id.tv_Description);
        }

        void bind(OnboardingItem onboardingItem) {
            imgv_Background.setImageResource(onboardingItem.getImageResId());
            tv_Tittle.setText(onboardingItem.getTitle());
            tv_Description.setText(onboardingItem.getDescription());
        }
    }
}