package com.example.fitup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    TextInputEditText edtName, edtPhone, edtPassword, edtPasswordRe;
    ImageButton edtPfp;
    Button continueButton;
    Uri selectedImageUri;
    TextView tvEmail;
    FirebaseAuth auth;
    FirebaseFirestore db;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    edtPfp.setImageURI(uri);
                    Toast.makeText(this, "Profile image selected!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtPasswordRe = findViewById(R.id.edtPasswordRe);
        edtPfp = findViewById(R.id.edtPfp);
        continueButton = findViewById(R.id.continueButton);
        tvEmail = findViewById(R.id.tvEmail);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String email = getIntent().getStringExtra("email");
        tvEmail.setText(email);

        edtPfp.setOnClickListener(v -> openGallery());

        continueButton.setOnClickListener(v -> {
            String name = edtName.getText() != null ? edtName.getText().toString().trim() : "";
            String phone = edtPhone.getText() != null ? edtPhone.getText().toString().trim() : "";
            String password = edtPassword.getText() != null ? edtPassword.getText().toString() : "";
            String passwordRe = edtPasswordRe.getText() != null ? edtPasswordRe.getText().toString() : "";

            boolean valid = validateFields(name, phone, password, passwordRe);

            if (!valid) {
                Toast.makeText(this, "Please check your input fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(result -> {
                        String uid = result.getUser().getUid();

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("email", email);
                        userData.put("name", name);
                        userData.put("phone", phone);

                        db.collection("users").document(uid)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User stored!"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Error storing user", e));

                        startActivity(new Intent(Register.this, IntroductionPage.class));
                        finish();
                    });
        });
    }

    private void openGallery() {
        pickImageLauncher.launch("image/*");
    }

    private boolean validateFields(String name, String phone, String pass, String passRe) {
        if (name.isEmpty() || phone.isEmpty() || pass.isEmpty() || passRe.isEmpty()) return false;
        if (!phone.matches("^[+]?[0-9]{9,12}$")) return false;
        if (pass.length() < 8 || !pass.equals(passRe)) return false;

        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$";
        return pass.matches(pattern);
    }
}
