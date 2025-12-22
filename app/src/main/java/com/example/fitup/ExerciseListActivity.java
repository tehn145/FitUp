package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ExerciseListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private List<Exercise> fullExerciseList;
    private List<Exercise> displayList;
    private TextView tvTitle;
    private ChipGroup chipGroup;
    private LinearLayout emptyState;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        db = FirebaseFirestore.getInstance();

        String categoryId = getIntent().getStringExtra("categoryId");
        String categoryName = getIntent().getStringExtra("categoryName");

        tvTitle = findViewById(R.id.tvToolbarTitle);
        ImageButton btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerExercises);
        chipGroup = findViewById(R.id.chipGroup);
        emptyState = findViewById(R.id.emptyState);

        if(categoryName != null) tvTitle.setText(categoryName + " Exercises");

        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fullExerciseList = new ArrayList<>();
        displayList = new ArrayList<>();

        adapter = new ExerciseAdapter(this, displayList, exercise -> {
            Intent intent = new Intent(ExerciseListActivity.this, ExerciseDetailActivity.class);
            intent.putExtra("exerciseId", exercise.getId());
            intent.putExtra("exerciseName", exercise.getName());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        recyclerView.setAdapter(adapter);

        if (categoryId != null) {
            loadExercisesFromFirebase(categoryId);
        }

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            filterExercises(checkedId);
        });
    }

    private void loadExercisesFromFirebase(String categoryId) {

        db.collection("exercises")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    fullExerciseList.clear();
                    displayList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Exercise exercise = doc.toObject(Exercise.class);
                        if (exercise != null) {
                            exercise.setId(doc.getId());
                            fullExerciseList.add(exercise);
                        }
                    }

                    displayList.addAll(fullExerciseList);
                    adapter.notifyDataSetChanged();

                    if (displayList.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ExerciseListActivity.this, "Error loading exercises: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DEBUG_FIREBASE", "Lỗi nghiêm trọng: " + e.getMessage());
                    Log.e("ExerciseList", "Error loading", e);
                });
    }

    private void filterExercises(int checkedId) {
        displayList.clear();
        String filterType = "all";

        if (checkedId == R.id.chipBeginner) filterType = "beginner";
        else if (checkedId == R.id.chipIntermediate) filterType = "intermediate";
        else if (checkedId == R.id.chipAdvanced) filterType = "advanced";
        else if (checkedId == R.id.chipBodyweight) filterType = "bodyweight";
        else if (checkedId == R.id.chipEquipment) filterType = "equipment";

        if (filterType.equals("all") || checkedId == -1 || checkedId == R.id.chipAll) {
            displayList.addAll(fullExerciseList);
        } else {
            for (Exercise ex : fullExerciseList) {
                String diff = ex.getDifficulty() != null ? ex.getDifficulty() : "";
                String equip = ex.getEquipment() != null ? ex.getEquipment() : "";

                boolean matchDifficulty = diff.equalsIgnoreCase(filterType);
                boolean matchEquipment = false;

                if (filterType.equals("bodyweight")) matchEquipment = equip.equalsIgnoreCase("bodyweight");
                if (filterType.equals("equipment")) matchEquipment = !equip.equalsIgnoreCase("bodyweight") && !equip.isEmpty();

                if (matchDifficulty || matchEquipment) {
                    displayList.add(ex);
                }
            }
        }

        adapter.notifyDataSetChanged();

        if (displayList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}