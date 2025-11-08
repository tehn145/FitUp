package com.example.fitup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fitness_Level extends AppCompatActivity {
    private MaterialCardView cardBeginner, cardIntermediate, cardAdvanced;
    private Button buttonContinue;
    private List<MaterialCardView> cardList;
    private String selectedLevel = null;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "FitnessLevelActivity";


    private int defaultStrokeColor;
    private int selectedStrokeColor;
    private int defaultBackgroundColor;
    private int selectedBackgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fitness_level);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardBeginner = findViewById(R.id.card_beginner);
        cardIntermediate = findViewById(R.id.card_intermediate);
        cardAdvanced = findViewById(R.id.card_advanced);
        buttonContinue = findViewById(R.id.button_continue);

        cardList = Arrays.asList(cardBeginner, cardIntermediate, cardAdvanced);

        defaultStrokeColor = Color.parseColor("#333333");
        selectedStrokeColor = Color.WHITE;
        defaultBackgroundColor = ContextCompat.getColor(this, R.color.backgroungbtncontinue);
        selectedBackgroundColor = Color.DKGRAY;

        buttonContinue.setEnabled(false);

        View.OnClickListener cardClickListener = v -> selectCard((MaterialCardView) v);

        cardBeginner.setOnClickListener(cardClickListener);
        cardIntermediate.setOnClickListener(cardClickListener);
        cardAdvanced.setOnClickListener(cardClickListener);


        buttonContinue.setOnClickListener(v -> {
            if (selectedLevel != null) {
                saveLevelAndProceed();
            } else {
                Toast.makeText(this, "Please select a fitness level.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectCard(MaterialCardView selectedCard) {
        for (MaterialCardView card : cardList) {
            if (card == selectedCard) {
                card.setStrokeColor(selectedStrokeColor);
                card.setCardBackgroundColor(selectedBackgroundColor);

                if (card.getId() == R.id.card_beginner) {
                    selectedLevel = "beginner";
                } else if (card.getId() == R.id.card_intermediate) {
                    selectedLevel = "intermediate";
                } else if (card.getId() == R.id.card_advanced) {
                    selectedLevel = "advanced";
                }
            } else {
                card.setStrokeColor(defaultStrokeColor);
                card.setCardBackgroundColor(defaultBackgroundColor);
            }
        }

        buttonContinue.setEnabled(true);
    }

    private void saveLevelAndProceed() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: No user logged in. Redirecting.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        String userId = currentUser.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("fitnessLevel", selectedLevel);

        db.collection("users").document(userId).set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User fitness level saved successfully!");
                    Toast.makeText(Fitness_Level.this, "Profile setup complete!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Fitness_Level.this, Primary_goal.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(Fitness_Level.this, "Failed to save fitness level. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
