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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    TextInputEditText edtName, edtPhone, edtPassword, edtPasswordRe;
    ImageButton edtPfp;
    Button continueButton;
    Uri selectedImageUri;
    TextView tvEmail;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseStorage storage;

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
        storage = FirebaseStorage.getInstance();

        String email = getIntent().getStringExtra("email");
        tvEmail.setText(email);

        edtPfp.setOnClickListener(v -> openGallery());

        continueButton.setOnClickListener(v -> {
            String name = edtName.getText() != null ? edtName.getText().toString().trim() : "";
            String phone = edtPhone.getText() != null ? edtPhone.getText().toString().trim() : "";
            String password = edtPassword.getText() != null ? edtPassword.getText().toString() : "";
            String passwordRe = edtPasswordRe.getText() != null ? edtPasswordRe.getText().toString() : "";

            boolean valid = validateFields(name, phone, password, passwordRe);

            if (!valid) return;


            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String uid = Objects.requireNonNull(authResult.getUser()).getUid();
                        // Now that we have the UID, we can upload the image or save the data.
                        if (selectedImageUri != null) {
                            uploadImageAndSaveUser(uid, email, name, phone);
                        } else {
                            // No image was selected, just save the user data without an avatar URL.
                            saveUserData(uid, email, name, phone);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error creating user", e);
                        Toast.makeText(Register.this, "Failed to create account: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }

    private void uploadImageAndSaveUser(String uid, String email, String name, String phone) {
        StorageReference avatarRef = storage.getReference().child("avatars/" + uid);

        avatarRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveUserData(uid, email, name, phone);
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to get download URL", e);
                        saveUserData(uid, email, name, phone);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload image", e);
                    saveUserData(uid, email, name, phone);
                });
    }

    private void saveUserData(String uid, String email, String name, String phone) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("gem", 5);

        db.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User data successfully stored in Firestore!");
                    startActivity(new Intent(Register.this, SelectRole.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error storing user data in Firestore", e);
                    Toast.makeText(this, "Failed to save profile details.", Toast.LENGTH_SHORT).show();
                });
    }


    private void openGallery() {
        pickImageLauncher.launch("image/*");
    }

    private boolean validateFields(String name, String phone, String pass, String passRe) {
        if (name.isEmpty() || phone.isEmpty() || pass.isEmpty() || passRe.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phone.matches("^[+]?[0-9]{9,15}$")) {
            Toast.makeText(this, "Invalid phone number format.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pass.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!pass.equals(passRe)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$";
        if (!pass.matches(pattern)) {
            Toast.makeText(this, "Password must contain at least one letter and one number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
