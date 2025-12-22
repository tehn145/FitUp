package com.example.fitup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.TrainerViewHolder> {

    private final List<Trainer> trainerList;
    private final Context context;
    private final OnTrainerItemClickListener listener;
    private String currentUserId;

    public interface OnTrainerItemClickListener {
        void onProfileClick(Trainer trainer);
        void onConnectClick(Trainer trainer);
        void onMessageClick(Trainer trainer);
    }

    public TrainerAdapter(Context context, List<Trainer> trainerList, OnTrainerItemClickListener listener) {
        this.context = context;
        this.trainerList = trainerList;
        this.listener = listener;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    @NonNull
    @Override
    public TrainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trainer, parent, false);
        return new TrainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainerViewHolder holder, int position) {
        Trainer trainer = trainerList.get(position);

        holder.tvTrainerName.setText(trainer.getName());
        holder.tvTrainerGem.setText(String.valueOf(trainer.getGem()));
        holder.tvLocation.setText(trainer.getLocationName());

        String level = trainer.getFitnessLevel();
        String levelText = "Fitness Trainer";

        if (level != null) {
            switch (level.toLowerCase().trim()) {
                case "advanced":
                    levelText = "Master Trainer (10+ years)";
                    break;
                case "intermediate":
                    levelText = "Senior Trainer (5-10 years)";
                    break;
                case "beginner":
                    levelText = "Junior Trainer (< 5 years)";
                    break;
            }
        }
        holder.tvSpecialty.setText(levelText);

        Glide.with(context)
                .load(trainer.getAvatar())
                .placeholder(R.drawable.defaultavt)
                .error(R.drawable.defaultavt)
                .centerCrop()
                .into(holder.ivTrainerAvatar);

        boolean isMe = (currentUserId != null && trainer.getUid() != null && currentUserId.equals(trainer.getUid()));
        boolean isConnected = trainer.isConnected();

        if (isMe) {
            holder.btnAddFriend.setVisibility(View.GONE);
            holder.btnConnect.setVisibility(View.GONE);
            holder.layoutGem.setVisibility(View.GONE);
            holder.btnMessage.setVisibility(View.GONE);
            holder.tvYouLabel.setVisibility(View.VISIBLE);
        } else if (isConnected) {
            holder.btnAddFriend.setVisibility(View.GONE);
            holder.btnConnect.setVisibility(View.GONE);
            holder.layoutGem.setVisibility(View.GONE);
            holder.tvYouLabel.setVisibility(View.GONE);
            holder.btnMessage.setVisibility(View.VISIBLE);
        } else {
            holder.btnAddFriend.setVisibility(View.VISIBLE);
            holder.btnConnect.setVisibility(View.VISIBLE);
            holder.layoutGem.setVisibility(View.VISIBLE);
            holder.tvYouLabel.setVisibility(View.GONE);
            holder.btnMessage.setVisibility(View.GONE);

            if (trainer.isRequestSent()) {
                holder.btnConnect.setText("Requested");
                holder.btnConnect.setEnabled(false);
                holder.btnConnect.setAlpha(0.6f);
            } else {
                holder.btnConnect.setText("Connect");
                holder.btnConnect.setEnabled(true);
                holder.btnConnect.setAlpha(1.0f);
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onProfileClick(trainer));
        holder.btnConnect.setOnClickListener(v -> listener.onConnectClick(trainer));
        holder.btnMessage.setOnClickListener(v -> listener.onMessageClick(trainer));
    }

    @Override
    public int getItemCount() {
        return trainerList.size();
    }

    public static class TrainerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTrainerAvatar, btnAddFriend;
        TextView tvTrainerName, tvLocation, tvTrainerGem, tvSpecialty, tvYouLabel;
        AppCompatButton btnConnect, btnMessage;
        LinearLayout layoutGem;

        public TrainerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTrainerAvatar = itemView.findViewById(R.id.imgTrainer);
            tvTrainerName = itemView.findViewById(R.id.txtTrainerName);
            tvSpecialty = itemView.findViewById(R.id.txtSpecialty); // Đã có sẵn trong XML
            tvLocation = itemView.findViewById(R.id.txtLocation);

            btnAddFriend = itemView.findViewById(R.id.btnAddfriend);
            btnConnect = itemView.findViewById(R.id.btnConnect);
            btnMessage = itemView.findViewById(R.id.btnMessage);
            tvTrainerGem = itemView.findViewById(R.id.tvTrainerGem);
            layoutGem = itemView.findViewById(R.id.layoutGem);
            tvYouLabel = itemView.findViewById(R.id.tvYouLabel);
        }
    }
}