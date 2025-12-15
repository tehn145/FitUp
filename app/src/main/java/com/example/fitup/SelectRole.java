package com.example.fitup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SelectRole extends AppCompatActivity {

    private MaterialCardView cardTrainer, cardClient;
    private MaterialButton continueButton;
    private String selectedRole = null;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "SelectRoleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        cardTrainer = findViewById(R.id.card_trainer);
        cardClient = findViewById(R.id.card_client);
        continueButton = findViewById(R.id.continueButton2);

        cardTrainer.setOnClickListener(v -> selectRole("trainer"));
        cardClient.setOnClickListener(v -> selectRole("client"));

        continueButton.setOnClickListener(v -> {
            if (selectedRole != null) {
                saveRoleAndProceed();
            }
        });
    }

    private void selectRole(String role) {
        selectedRole = role;

        cardTrainer.setStrokeColor(Color.TRANSPARENT);
        cardTrainer.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));

        cardClient.setStrokeColor(Color.TRANSPARENT);
        cardClient.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));

        int highlightColor = ContextCompat.getColor(this, R.color.gray);
        if ("trainer".equals(role)) {
            cardTrainer.setStrokeColor(highlightColor);
        } else if ("client".equals(role)) {
            cardClient.setStrokeColor(highlightColor);
        }

        continueButton.setEnabled(true);
        continueButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
    }

    private void saveRoleAndProceed() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: No user logged in.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        String userId = currentUser.getUid();

        Map<String, Object> userData = new HashMap<>();
        userData.put("role", selectedRole);

        db.collection("users").document(userId).set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User role saved successfully!");

                    Intent intent = new Intent(SelectRole.this, SelectGender.class);
                    startActivity(intent);

                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                    Toast.makeText(SelectRole.this, "Failed to save role. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
