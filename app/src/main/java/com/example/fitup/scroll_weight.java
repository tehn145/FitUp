package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class scroll_weight extends AppCompatActivity {
    private NumberPicker pickerWeight;
    private MaterialButton btnContinue;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "ScrollWeightActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_weight);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        pickerWeight = findViewById(R.id.pickerHeight);
        btnContinue = findViewById(R.id.btnContinue);

        initWeightPicker();

        btnContinue.setOnClickListener(v -> saveWeightAndProceed());
    }

    private void initWeightPicker() {
        pickerWeight.setMinValue(30);
        pickerWeight.setMaxValue(200);
        pickerWeight.setValue(70);
        pickerWeight.setWrapSelectorWheel(false);
    }

    private void saveWeightAndProceed() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: No user logged in. Redirecting.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
    //fix sau
        int weight = pickerWeight.getValue();

        String userId = currentUser.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("weight", weight); // Saving as a number

        db.collection("users").document(userId).set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User weight saved successfully!");

                    Intent intent = new Intent(scroll_weight.this, Fitness_Level.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(scroll_weight.this, "Failed to save weight. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
