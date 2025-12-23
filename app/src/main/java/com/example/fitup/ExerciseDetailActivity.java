package com.example.fitup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class ExerciseDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ImageView ivGif;
    private TextView tvName, tvDifficulty, tvEquipment, tvMuscle;
    private TextView tvSets, tvReps, tvInstructions, tvTips;
    private CardView cardTips;
    private CollapsingToolbarLayout collapsingToolbar;
    private Button btnWatchVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        db = FirebaseFirestore.getInstance();

        String exerciseId = getIntent().getStringExtra("exerciseId");
        String exerciseName = getIntent().getStringExtra("exerciseName");

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ivGif = findViewById(R.id.ivExerciseGif);
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        tvName = findViewById(R.id.tvExerciseName);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvEquipment = findViewById(R.id.tvEquipment);
        tvMuscle = findViewById(R.id.tvTargetMuscle);
        tvSets = findViewById(R.id.tvSets);
        tvReps = findViewById(R.id.tvReps);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvTips = findViewById(R.id.tvTips);
        cardTips = findViewById(R.id.cardTips);
        btnWatchVideo = findViewById(R.id.btnWatchVideo);

        collapsingToolbar.setTitle(exerciseName);

        loadExerciseDetail(exerciseId);
    }

    private void loadExerciseDetail(String exerciseId) {
        db.collection("exercises")
                .document(exerciseId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Exercise exercise = document.toObject(Exercise.class);
                        if (exercise != null) {
                            displayExercise(exercise);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Exercise exercise = getSampleExercise(exerciseId);
                    displayExercise(exercise);
                });
    }

    private void displayExercise(Exercise exercise) {
        tvName.setText(exercise.getName());

        String difficulty = exercise.getDifficulty();
        if (difficulty != null && !difficulty.isEmpty()) {
            tvDifficulty.setText(difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1));
        }

        tvEquipment.setText(exercise.getEquipment());
        tvMuscle.setText(exercise.getTargetMuscle());
        tvSets.setText(String.valueOf(exercise.getDefaultSets()));
        tvReps.setText(String.valueOf(exercise.getDefaultReps()));

        // Load GIF animation
        if (exercise.getGifUrl() != null && !exercise.getGifUrl().isEmpty()) {
            Glide.with(this)
                    .asGif()
                    .load(exercise.getGifUrl())
                    .placeholder(R.drawable.placeholder_exercise)
                    .into(ivGif);
        } else {
            ivGif.setImageResource(R.drawable.placeholder_exercise);
        }

        // Instructions
        if (exercise.getInstructions() != null && !exercise.getInstructions().isEmpty()) {
            StringBuilder instructions = new StringBuilder();
            for (int i = 0; i < exercise.getInstructions().size(); i++) {
                instructions.append((i + 1))
                        .append(". ")
                        .append(exercise.getInstructions().get(i))
                        .append("\n");
            }
            tvInstructions.setText(instructions.toString().trim());
        }

        // Tips
        if (exercise.getTips() != null && !exercise.getTips().isEmpty()) {
            StringBuilder tips = new StringBuilder();
            for (String tip : exercise.getTips()) {
                tips.append("• ").append(tip).append("\n");
            }
            tvTips.setText(tips.toString().trim());
            cardTips.setVisibility(View.VISIBLE);
        } else {
            cardTips.setVisibility(View.GONE);
        }
    
        String videoUrl = exercise.getVideoUrl();
        if (videoUrl != null && !videoUrl.isEmpty()) {
            btnWatchVideo.setVisibility(View.VISIBLE);
            btnWatchVideo.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Cannot open video link.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            btnWatchVideo.setVisibility(View.GONE);
        }
    }

    private Exercise getSampleExercise(String exerciseId) {
        Exercise ex = new Exercise();
        ex.setId(exerciseId);

        if ("push_up".equals(exerciseId)) {
            ex.setName("Push Up");
            ex.setDifficulty("beginner");
            ex.setEquipment("bodyweight");
            ex.setTargetMuscle("Chest, Triceps, Shoulders");
            ex.setDefaultSets(3);
            ex.setDefaultReps(12);
            ex.setGifUrl("");
            // Thêm videoUrl mẫu cho push up
            ex.setVideoUrl("https://www.youtube.com/watch?v=IODxDxX7oi4");

            List<String> instructions = Arrays.asList(
                    "Start in a plank position with hands shoulder-width apart",
                    "Lower your body until chest nearly touches the floor",
                    "Keep your body in a straight line",
                    "Push back up to starting position",
                    "Repeat for desired reps"
            );
            ex.setInstructions(instructions);

            List<String> tips = Arrays.asList(
                    "Keep your core engaged throughout",
                    "Don't let your hips sag",
                    "Breathe in as you lower, out as you push up",
                    "Modify on knees if needed"
            );
            ex.setTips(tips);
        } else {
            ex.setName("Exercise Name");
            ex.setDifficulty("beginner");
            ex.setEquipment("bodyweight");
            ex.setTargetMuscle("Target Muscle Groups");
            ex.setDefaultSets(3);
            ex.setDefaultReps(10);
            ex.setInstructions(Arrays.asList("Perform the exercise correctly"));
            ex.setTips(Arrays.asList("Focus on form"));
        }

        return ex;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}