package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    // TextView LinkTermsOfService, LinkPrivacyPolicy;

    Button btnContinue;
    EditText email, pwd;
    LinearLayout pwdEdt;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnContinue = findViewById(R.id.btnContinue);
        email = findViewById(R.id.editEmail);
        pwd = findViewById(R.id.editPwd);
        pwdEdt = findViewById(R.id.edtPassword);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ hai TextView
//        LinkTermsOfService = findViewById(R.id.LinkTermsOfService);
//        LinkPrivacyPolicy = findViewById(R.id.LinkPrivacyPolicy);
//
//
//        // Gạch chân và gán sự kiện mở link cho Terms of Service
//        LinkTermsOfService.setPaintFlags(LinkTermsOfService.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        LinkTermsOfService.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fitso.com/terms"));
//            startActivity(intent);
//        });
//
//        // Gạch chân và gán sự kiện mở link cho Privacy Policy
//        LinkPrivacyPolicy.setPaintFlags(LinkPrivacyPolicy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        LinkPrivacyPolicy.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fitso.com/privacy"));
//            startActivity(intent);
//        });

        btnContinue.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();

            if (emailText.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(MainActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pwdEdt.getVisibility() == View.VISIBLE) {
                String passwordText = pwd.getText().toString().trim();

                if (passwordText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(loginTask -> {
                            if (loginTask.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(MainActivity.this, IntroductionPage.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String errorMsg = loginTask.getException() != null ?
                                        loginTask.getException().getMessage() : "Login failed";
                                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                return;
            }

            db.collection("users")
                    .whereEqualTo("email", emailText)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            if (result != null && !result.isEmpty()) {
                                pwdEdt.setVisibility(View.VISIBLE);
                            } else {
                                //g
                                Intent intent = new Intent(MainActivity.this, Register.class);
                                intent.putExtra("email", emailText);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error checking Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }
}