package com.example.fitup;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    TextView LinkTermsOfService, LinkPrivacyPolicy;

    Button btnContinue;
    EditText email, pwd;
    LinearLayout pwdEdt;

    FirebaseAuth auth;
    FirebaseFirestore db;

    // ✅ Emulator config (Android Emulator host)
    private static final String EMU_HOST = "10.0.2.2";
    private static final int AUTH_PORT = 9099;
    private static final int FIRESTORE_PORT = 8080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        LinkTermsOfService = findViewById(R.id.LinkTermsOfService);
        LinkPrivacyPolicy = findViewById(R.id.LinkPrivacyPolicy);
        btnContinue = findViewById(R.id.btnContinue);
        email = findViewById(R.id.editEmail);
        pwd = findViewById(R.id.editPwd);
        pwdEdt = findViewById(R.id.edtPassword);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // ✅ MUST: trỏ emulator trước khi signIn / query Firestore
        setupEmulators();

        // ✅ (Khuyến nghị) nếu bạn vừa chuyển prod -> emulator, signOut để tránh token cũ
        // Chỉ cần bật 1 lần khi debug, sau đó có thể comment lại.
        // auth.signOut();

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

            // Nếu đã hiện ô password thì login
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
                                startActivity(new Intent(MainActivity.this, MainView.class));
                                finish();
                            } else {
                                String errorMsg = loginTask.getException() != null
                                        ? loginTask.getException().getMessage()
                                        : "Login failed";
                                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                return;
            }

            // Chưa hiện password -> check user trên Firestore emulator
            db.collection("users")
                    .whereEqualTo("email", emailText)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            if (result != null && !result.isEmpty()) {
                                pwdEdt.setVisibility(View.VISIBLE);
                            } else {
                                Intent intent = new Intent(MainActivity.this, CreateAccount.class);
                                intent.putExtra("email", emailText);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            String msg = task.getException() != null ? task.getException().getMessage() : "Error checking Firestore.";
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        LinkTermsOfService.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://fitsohub.com/en/term-policy"));
            startActivity(intent);
        });

        LinkPrivacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://fitsohub.com/en/privacy-policy"));
            startActivity(intent);
        });

        LinkTermsOfService.setPaintFlags(LinkTermsOfService.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        LinkPrivacyPolicy.setPaintFlags(LinkPrivacyPolicy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private void setupEmulators() {
        try {
            auth.useEmulator(EMU_HOST, AUTH_PORT);
        } catch (Exception ignored) {}

        try {
            db.useEmulator(EMU_HOST, FIRESTORE_PORT);

            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build();
            db.setFirestoreSettings(settings);
        } catch (Exception ignored) {}
    }
}
