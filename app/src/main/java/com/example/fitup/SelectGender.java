package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SelectGender extends AppCompatActivity {

    private MaterialButtonToggleGroup genderToggleGroup;
    private MaterialButton continueButton;
    private String selectedGender = null;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "SelectGenderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_gender);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        genderToggleGroup = findViewById(R.id.genderToggleGroup);
        continueButton = findViewById(R.id.continueButton2);

        genderToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_female) {
                    selectedGender = "female";
                } else if (checkedId == R.id.btn_male) {
                    selectedGender = "male";
                } else if (checkedId == R.id.btn_other) {
                    selectedGender = "other";
                }

                enableContinueButton();
            } else {

                selectedGender = null;
                disableContinueButton();
            }
        });

        continueButton.setOnClickListener(v -> {
            if (selectedGender != null) {
                saveGenderAndProceed();
            }
        });
    }

    private void enableContinueButton() {
        continueButton.setEnabled(true);
        continueButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        continueButton.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    private void disableContinueButton() {
        continueButton.setEnabled(false);
        continueButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        continueButton.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    private void saveGenderAndProceed() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: No user logged in. Redirecting.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        String userId = currentUser.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("gender", selectedGender);

        db.collection("users").document(userId).set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User gender saved successfully!");
                    Intent intent = new Intent(SelectGender.this, pick_your_birthday.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(SelectGender.this, "Failed to save gender. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
