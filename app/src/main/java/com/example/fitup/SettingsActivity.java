package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageView btnBack;
    private ConstraintLayout manageAccountRow;
    private ConstraintLayout logoutRow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        btnBack = findViewById(R.id.btn_back);
        manageAccountRow = findViewById(R.id.manage_account_row);
        logoutRow = findViewById(R.id.logout_row);

        setupClickListeners();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            finish();
        });

        manageAccountRow.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ManageAccountActivity.class);
            startActivity(intent);
        });

        logoutRow.setOnClickListener(v -> {
            mAuth.signOut();

            Toast.makeText(SettingsActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SettingsActivity.this, IntroductionPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
