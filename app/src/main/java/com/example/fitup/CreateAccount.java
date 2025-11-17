package com.example.fitup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class CreateAccount extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    TextInputEditText edtName2, edtPhone2, edtPassword2, edtPassword2Re;
    ImageButton edtPfp2, imgavt;
    Button continueButton2;
    Uri selectedImageUri;
    TextView tvEmail2;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);

//        edtName2 = findViewById(R.id.edtName2);
//        edtPhone2 = findViewById(R.id.edtPhone2);
//        edtPassword2 = findViewById(R.id.edtPassword2);
//        edtPasswordRe2 = findViewById(R.id.edtPassword2Re);
//        edtPfp2 = findViewById(R.id.edtPfp2);
//        continueButton2 = findViewById(R.id.continueButton2);
//        tvEmail2 = findViewById(R.id.tvEmail2);
        edtName2 = findViewById(R.id.edtName2);
        edtPhone2 = findViewById(R.id.edtPhone2);
        edtPassword2 = findViewById(R.id.edtPassword2);
        edtPassword2Re = findViewById(R.id.edtPassword2Re);
        edtPfp2 = findViewById(R.id.edtPfp2);
        continueButton2 = findViewById(R.id.continueButton2);
        tvEmail2 = findViewById(R.id.tvEmail2);
        imgavt = findViewById(R.id.imgavt);




        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        String email = getIntent().getStringExtra("email");
        tvEmail2.setText(email);

        edtPfp2.setOnClickListener(v -> openGallery());
        imgavt.setOnClickListener(v -> openGallery());

        continueButton2.setOnClickListener(v -> {
            String name = edtName2.getText() != null ? edtName2.getText().toString().trim() : "";
            String phone = edtPhone2.getText() != null ? edtPhone2.getText().toString().trim() : "";
            String password = edtPassword2.getText() != null ? edtPassword2.getText().toString() : "";
            String passwordRe = edtPassword2Re.getText() != null ? edtPassword2Re.getText().toString() : "";

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
                            saveUserData(uid, email, name, phone, "");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error creating user", e);
                        Toast.makeText(CreateAccount.this, "Failed to create account: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
        //        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    private void uploadImageAndSaveUser(String uid, String email, String name, String phone) {
        StorageReference avatarRef = storage.getReference().child("avatars/" + uid);

        avatarRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveUserData(uid, email, name, phone, uri.toString());  // ✔ LƯU URL
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to get download URL", e);
                        saveUserData(uid, email, name, phone, ""); // ✔ LƯU RỖNG
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload image", e);
                    saveUserData(uid, email, name, phone, ""); // ✔ LƯU RỖNG
                });
    }


    private void saveUserData(String uid, String email, String name, String phone, String avatarUrl) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("gem", 5);
        userData.put("avatar", avatarUrl);   // 👈 THÊM VÀO ĐÂY

        db.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User data saved!");
                    startActivity(new Intent(CreateAccount.this, SelectRole.class));
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
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgavt.setImageURI(uri);
                    Toast.makeText(this, "Profile image selected!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            });
}