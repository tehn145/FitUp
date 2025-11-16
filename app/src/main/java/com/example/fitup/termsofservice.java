package com.example.fitup;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class termsofservice extends AppCompatActivity {
    private WebView webView;
    ImageView btn_back_terms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_termsofservice);

        webView = findViewById(R.id.WebTerm);
        btn_back_terms = findViewById(R.id.btn_back_policy);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://fitsohub.com/en/term-policy");

        btn_back_terms.setOnClickListener(v -> finish());
    }
}