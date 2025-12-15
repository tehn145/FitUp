package com.example.fitup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;
import java.util.Locale;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.TrainerViewHolder> {

    private final List<Trainer> trainerList;
    private final Context context;

    public TrainerAdapter(Context context, List<Trainer> trainerList) {
        this.context = context;
        this.trainerList = trainerList;
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
        holder.tvTrainerGem.setText(String.format(Locale.getDefault(), "%d Gem", trainer.getGem()));

        if (trainer.getPrimaryGoal() != null && !trainer.getPrimaryGoal().isEmpty()) {
            String goal = trainer.getPrimaryGoal();
            String formattedGoal = goal.substring(0, 1).toUpperCase() + goal.substring(1).replace('_', ' ');
            holder.tvTrainerSpecialty.setText(formattedGoal);
        } else {
            holder.tvTrainerSpecialty.setText("General Fitness");
        }

        Glide.with(context)
                .load(trainer.getAvatar())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .circleCrop()
                .listener(new RequestListener<Drawable>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.ivTrainerAvatar.setImageTintList(null);
                        return false;
                    }
                })
                .into(holder.ivTrainerAvatar);
    }

    @Override
    public int getItemCount() {
        return trainerList.size();
    }

    public static class TrainerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTrainerAvatar;
        TextView tvTrainerName, tvTrainerSpecialty, tvTrainerGem;

        public TrainerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTrainerAvatar = itemView.findViewById(R.id.imgTrainer);
            tvTrainerName = itemView.findViewById(R.id.txtTrainerName);
            tvTrainerSpecialty = itemView.findViewById(R.id.txtRank);
            tvTrainerGem = itemView.findViewById(R.id.tvTrainerGem);
        }
    }
}
//fix sau
