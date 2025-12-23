package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExerciseCategoryActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerCategories;
    private ExerciseCategoryAdapter categoryAdapter;
    private List<ExerciseCategory> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_category);

        db = FirebaseFirestore.getInstance();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerCategories = findViewById(R.id.recyclerCategories);
        recyclerCategories.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        categoryAdapter = new ExerciseCategoryAdapter(this, categoryList, category -> {
            Intent intent = new Intent(ExerciseCategoryActivity.this, ExerciseListActivity.class);
            intent.putExtra("categoryId", category.getId());
            intent.putExtra("categoryName", category.getName());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        recyclerCategories.setAdapter(categoryAdapter);

        loadCategories();
    }

//    private void loadCategories() {
//        db.collection("exercise_categories")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    categoryList.clear();
//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        String id = document.getString("id");
//                        String name = document.getString("name");
//                        String description = document.getString("description");
//                        Long exerciseCount = document.getLong("exerciseCount");
//
//                        ExerciseCategory category = new ExerciseCategory(
//                                id,
//                                name,
//                                description,
//                                R.drawable.ic_dumbbell,
//                                exerciseCount != null ? exerciseCount.intValue() : 0
//                        );
//                        categoryList.add(category);
//                    }
//                    categoryAdapter.notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> {
//                    loadSampleCategories();
//                });
//    }

    private void loadCategories() {
        db.collection("exercise_categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getString("id");
                        if (id == null) id = document.getId();

                        String name = document.getString("name");
                        String description = document.getString("description");
                        String imageUrl = document.getString("imageUrl");

                        Long exerciseCount = document.getLong("exerciseCount");
                        ExerciseCategory category = new ExerciseCategory(
                                id,
                                name,
                                description,
                                imageUrl,
                                exerciseCount != null ? exerciseCount.intValue() : 0
                        );
                        categoryList.add(category);
                    }
                    categoryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    loadSampleCategories();
                });
    }

    private void loadSampleCategories() {
        categoryList.clear();
        categoryList.add(new ExerciseCategory("chest", "Chest", "Build powerful upper body", R.drawable.ic_dumbbell, 15));
        categoryList.add(new ExerciseCategory("back", "Back", "Strengthen posterior chain", R.drawable.ic_dumbbell, 18));
        categoryList.add(new ExerciseCategory("legs", "Legs", "Build strong foundation", R.drawable.ic_dumbbell, 20));
        categoryList.add(new ExerciseCategory("shoulders", "Shoulders", "Develop broad shoulders", R.drawable.ic_dumbbell, 12));
        categoryList.add(new ExerciseCategory("arms", "Arms", "Sculpt bigger arms", R.drawable.ic_dumbbell, 16));
        categoryList.add(new ExerciseCategory("core", "Core", "Strengthen your core", R.drawable.ic_dumbbell, 14));
        categoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}