package com.example.fitup;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    TextView LinkTermsOfService, LinkPrivacyPolicy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ánh xạ hai TextView
        LinkTermsOfService = findViewById(R.id.LinkTermsOfService);
        LinkPrivacyPolicy = findViewById(R.id.LinkPrivacyPolicy);


        // Gạch chân và gán sự kiện mở link cho Terms of Service
        LinkTermsOfService.setPaintFlags(LinkTermsOfService.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        LinkTermsOfService.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fitso.com/terms"));
            startActivity(intent);
        });

        // Gạch chân và gán sự kiện mở link cho Privacy Policy
        LinkPrivacyPolicy.setPaintFlags(LinkPrivacyPolicy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        LinkPrivacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fitso.com/privacy"));
            startActivity(intent);
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}