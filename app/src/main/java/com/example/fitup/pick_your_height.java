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

public class pick_your_height extends AppCompatActivity {
    private NumberPicker pickerHeight;
    private MaterialButton btnContinue;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "PickHeightActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_your_height);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        pickerHeight = findViewById(R.id.pickerHeight);
        btnContinue = findViewById(R.id.btnContinue);

        initHeightPicker();

        btnContinue.setOnClickListener(v -> saveHeightAndProceed());
    }

    private void initHeightPicker() {
        pickerHeight.setMinValue(100);
        pickerHeight.setMaxValue(250);
        pickerHeight.setValue(170);
        pickerHeight.setWrapSelectorWheel(false);
    }

    private void saveHeightAndProceed() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: No user logged in. Redirecting.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        int height = pickerHeight.getValue();

        String userId = currentUser.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("height", height); // Saving as a number

        db.collection("users").document(userId).set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User height saved successfully!");

                    Intent intent = new Intent(pick_your_height.this, scroll_weight.class);
                    startActivity(intent);

                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(pick_your_height.this, "Failed to save height. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
