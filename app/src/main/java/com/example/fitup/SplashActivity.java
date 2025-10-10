package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Sau 2 giây thì chuyển sang MainActivity
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, IntroductionPage.class));
            finish(); // đóng splash
        }, 2000); //hiển thị 2s
    }
}
