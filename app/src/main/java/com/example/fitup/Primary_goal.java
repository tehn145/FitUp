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

public class Primary_goal extends AppCompatActivity {
    MaterialCardView cardWeightLoss, cardMuscleBuilding, cardEndurance, cardKeepFit, cardOverallHealth;
    Button buttonContinue;
    List<MaterialCardView> cardList;

    private int defaultStrokeColor;
    private int selectedStrokeColor;
    private int defaultBackgroundColor;
    private int selectedBackgroundColor;
    private String selectedGoal = null;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "PrimaryGoalActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_primary_goal);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        buttonContinue = findViewById(R.id.button_continue);
        cardWeightLoss = findViewById(R.id.card_weight_loss);
        cardMuscleBuilding = findViewById(R.id.card_muscle_building);
        cardEndurance = findViewById(R.id.card_improving_endurance);
        cardKeepFit = findViewById(R.id.card_keep_fit);
        cardOverallHealth = findViewById(R.id.card_overall_health);

        cardList = Arrays.asList(
                cardWeightLoss,
                cardMuscleBuilding,
                cardEndurance,
                cardKeepFit,
                cardOverallHealth
        );

        defaultStrokeColor = Color.parseColor("#333333");
        selectedStrokeColor = Color.WHITE;
        defaultBackgroundColor = ContextCompat.getColor(this, R.color.backgroungbtncontinue);
        selectedBackgroundColor = Color.DKGRAY;
        buttonContinue.setEnabled(false);

        View.OnClickListener cardClickListener = v -> selectCard((MaterialCardView) v);

        for (MaterialCardView card : cardList) {
            card.setOnClickListener(cardClickListener);
        }


        buttonContinue.setOnClickListener(v -> {
            if (selectedGoal != null) {
                saveGoalAndProceed();
            } else {
                Toast.makeText(this, "Please select a primary goal.", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void selectCard(MaterialCardView selectedCard) {
        for (MaterialCardView card : cardList) {
            if (card == selectedCard) {
                card.setStrokeColor(selectedStrokeColor);
                card.setCardBackgroundColor(selectedBackgroundColor);
                if (card.getId() == R.id.card_weight_loss) {
                    selectedGoal = "weight_loss";
                } else if (card.getId() == R.id.card_muscle_building) {
                    selectedGoal = "muscle_building";
                } else if (card.getId() == R.id.card_improving_endurance) {
                    selectedGoal = "improving_endurance";
                } else if (card.getId() == R.id.card_keep_fit) {
                    selectedGoal = "keep_fit";
                } else if (card.getId() == R.id.card_overall_health) {
                    selectedGoal = "overall_health";
                }
            } else {
                card.setStrokeColor(defaultStrokeColor);
                card.setCardBackgroundColor(defaultBackgroundColor);
            }
        }
        buttonContinue.setEnabled(true);
    }

    private void saveGoalAndProceed() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: No user logged in. Redirecting.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        String userId = currentUser.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("primaryGoal", selectedGoal);

        db.collection("users").document(userId).set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User primary goal saved successfully!");
                    //Toast.makeText(Primary_goal.this, "Profile setup complete!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Primary_goal.this, Location_access.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(Primary_goal.this, "Failed to save goal. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
