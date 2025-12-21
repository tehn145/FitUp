package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView; // 1. Import ImageView
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Import Glide
import com.bumptech.glide.Glide; // 2. Import Glide

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class congrats extends AppCompatActivity {
    private static final int GOTO_HOME_DELAY = 3000;
    private static final String TAG = "CongratsActivity";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private TextView txtCongrats;
    private ImageView imgAvatar; // 3. Khai báo biến ImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_congrats);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        txtCongrats = findViewById(R.id.txtCongrats);
        imgAvatar = findViewById(R.id.imgAvatar); // 4. Ánh xạ ID từ layout (Đảm bảo trong XML có id này)

        // Gọi hàm load dữ liệu
        loadUserData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                goToHomeScreen();
            }
        }, GOTO_HOME_DELAY);
    }

    /**
     * Lấy dữ liệu user (Tên + Avatar) từ Firestore
     */
    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "No user is logged in.");
            txtCongrats.setText("Congratulations!");
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // --- XỬ LÝ TÊN ---
                    String userName = document.getString("name");
                    if (userName != null && !userName.isEmpty()) {
                        txtCongrats.setText("Congratulations, " + userName + "!");
                    } else {
                        txtCongrats.setText("Congratulations!");
                    }

                    // --- XỬ LÝ ẢNH AVATAR (Mới thêm vào) ---
                    String avatarUrl = document.getString("avatar");

                    // Kiểm tra nếu có link ảnh thì load, nếu không có thì bỏ qua (hoặc set ảnh mặc định)
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(congrats.this)
                                .load(avatarUrl)
                                .circleCrop() // Tuỳ chọn: Cắt ảnh thành hình tròn
                                .into(imgAvatar);
                    } else {
                        // (Tuỳ chọn) Set ảnh mặc định nếu user chưa có avatar
                        // imgAvatar.setImageResource(R.drawable.default_avatar);
                    }

                } else {
                    Log.d(TAG, "No such document");
                    txtCongrats.setText("Congratulations!");
                }
            } else {
                Log.e(TAG, "get failed with ", task.getException());
                txtCongrats.setText("Congratulations!");
            }
        });
    }

    private void goToHomeScreen() {
        Intent intent = new Intent(congrats.this, MainView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}