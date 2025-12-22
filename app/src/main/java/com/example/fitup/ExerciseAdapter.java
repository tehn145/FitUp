package com.example.fitup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private Context context;
    private List<Exercise> exercises;
    private OnExerciseClickListener listener;

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }

    public ExerciseAdapter(Context context, List<Exercise> exercises, OnExerciseClickListener listener) {
        this.context = context;
        this.exercises = exercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);

        holder.tvName.setText(exercise.getName());
        holder.tvMuscle.setText(exercise.getTargetMuscle());

        String difficulty = exercise.getDifficulty();
        holder.tvDifficulty.setText(difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1));

        holder.tvEquipment.setText(exercise.getEquipment());
        holder.tvSetsReps.setText(String.format(Locale.US, "%d sets × %d reps",
                exercise.getDefaultSets(), exercise.getDefaultReps()));

        if ("beginner".equals(difficulty.toLowerCase())) {
            holder.tvDifficulty.setBackgroundResource(R.drawable.bg_badge_beginner);
        } else if ("intermediate".equals(difficulty.toLowerCase())) {
            holder.tvDifficulty.setBackgroundResource(R.drawable.bg_badge_intermediate);
        } else {
            holder.tvDifficulty.setBackgroundResource(R.drawable.bg_badge_advanced);
        }

        if (exercise.getGifUrl() != null && !exercise.getGifUrl().isEmpty()) {
            Glide.with(context)
                    .asGif()
                    .load(exercise.getGifUrl())
                    .placeholder(R.drawable.placeholder_exercise)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.placeholder_exercise);
        }

        holder.cardView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_in));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExerciseClick(exercise);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivImage;
        TextView tvName, tvMuscle, tvDifficulty, tvEquipment, tvSetsReps;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            ivImage = itemView.findViewById(R.id.ivExerciseImage);
            tvName = itemView.findViewById(R.id.tvExerciseName);
            tvMuscle = itemView.findViewById(R.id.tvTargetMuscle);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            tvEquipment = itemView.findViewById(R.id.tvEquipment);
            tvSetsReps = itemView.findViewById(R.id.tvDefaultSetsReps);
        }
    }
}