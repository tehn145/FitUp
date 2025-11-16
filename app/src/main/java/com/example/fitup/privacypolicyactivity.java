package com.example.fitup;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class privacypolicyactivity extends AppCompatActivity {
    private WebView webView;
    private ImageView btn_back_policy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_privacypolicyactivity);

        webView = findViewById(R.id.WebPolicy);
        btn_back_policy = findViewById(R.id.btn_back_policy);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://fitsohub.com/en/privacy-policy");

        btn_back_policy.setOnClickListener(v -> finish());
    }
}