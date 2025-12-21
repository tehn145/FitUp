package com.example.fitup;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.util.List;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.TrainerViewHolder> {

    private final List<Trainer> trainerList;
    private final Context context;
    private final OnTrainerItemClickListener listener;

    public interface OnTrainerItemClickListener {
        void onProfileClick(Trainer trainer);
        void onConnectClick(Trainer trainer);
    }

    public TrainerAdapter(Context context, List<Trainer> trainerList, OnTrainerItemClickListener listener) {
        this.context = context;
        this.trainerList = trainerList;
        this.listener = listener;
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

        Glide.with(context)
                .load(trainer.getAvatar())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .centerCrop()
                .into(holder.ivTrainerAvatar);

        if (trainer.isRequestSent()) {
            holder.btnConnect.setText("Requested");
            holder.btnConnect.setEnabled(false);
            holder.btnConnect.setAlpha(0.6f);
        } else {
            holder.btnConnect.setText("Connect");
            holder.btnConnect.setEnabled(true);
            holder.btnConnect.setAlpha(1.0f);
        }

        holder.itemView.setOnClickListener(v -> listener.onProfileClick(trainer));
        holder.btnConnect.setOnClickListener(v -> listener.onConnectClick(trainer));
    }

    @Override
    public int getItemCount() {
        return trainerList.size();
    }

    public static class TrainerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTrainerAvatar;
        TextView tvTrainerName, tvLocation, tvTrainerGem, tvSpecialty;
        AppCompatButton btnConnect;

        public TrainerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTrainerAvatar = itemView.findViewById(R.id.imgTrainer);
            tvTrainerName = itemView.findViewById(R.id.txtTrainerName);
            tvSpecialty = itemView.findViewById(R.id.txtSpecialty);
            tvLocation = itemView.findViewById(R.id.txtLocation);
            tvTrainerGem = itemView.findViewById(R.id.tvTrainerGem);
            btnConnect = itemView.findViewById(R.id.btnConnect);
        }
    }
}