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


        int selectedColor = Color.parseColor("#424242"); // Light Gray
        int defaultColor = ContextCompat.getColor(this, R.color.white);

        // 2. Kiểm tra role để set màu nền (Background)
        if ("trainer".equals(role)) {
            // Chọn Trainer: Trainer xám, Client trắng
            cardTrainer.setCardBackgroundColor(selectedColor);
            cardClient.setCardBackgroundColor(defaultColor);

            // (Tuỳ chọn) Nếu muốn viền cũng mất đi hoặc hiện rõ hơn
            cardTrainer.setStrokeWidth(0);
            cardClient.setStrokeWidth(2); // Giữ viền cho cái chưa chọn nếu muốn

        } else if ("client".equals(role)) {
            // Chọn Client: Client xám, Trainer trắng
            cardClient.setCardBackgroundColor(selectedColor);
            cardTrainer.setCardBackgroundColor(defaultColor);

            // (Tuỳ chọn)
            cardClient.setStrokeWidth(0);
            cardTrainer.setStrokeWidth(2);
        }

        // 3. Kích hoạt nút Continue
        continueButton.setEnabled(true);
        // Đảm bảo nút hiện rõ (nếu XML đã set backgroundTint thì dòng này có thể không cần thiết, nhưng giữ lại cho chắc)
        continueButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
    }
    // --------------------------

    private void saveRoleAndProceed() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: No user logged in.", Toast.LENGTH_SHORT).show();
            // Nếu lỗi user, có thể đẩy về trang login thay vì MainView
            // startActivity(new Intent(this, LoginActivity.class));
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