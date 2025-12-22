package com.example.fitup;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainerProfileActivity extends AppCompatActivity {

    private ImageView imgCover, btnBack;
    private TextView tvName, tvTitle, tvUsername, tvLocation, tvAbout, tvSpecialty; // Thêm tvSpecialty
    private AppCompatButton btnConnect;
    private RecyclerView rvTrainerPosts;
    private PostGridAdapter postAdapter;
    private List<Post> postList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String targetUserId;
    private String currentUserId;

    private boolean isRequestSent = false;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_trainer_profile);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
        }

        imgCover = findViewById(R.id.imgTrainerCover);
        btnBack = findViewById(R.id.btnBack);
        tvName = findViewById(R.id.tvTrainerName);
        tvTitle = findViewById(R.id.tvTrainerTitle);
        tvSpecialty = findViewById(R.id.tvSpecialty);
        tvUsername = findViewById(R.id.tvTrainerUsername);
        tvLocation = findViewById(R.id.tvTrainerLocation);
        tvAbout = findViewById(R.id.tvAbout);
        btnConnect = findViewById(R.id.btnConnect);

        rvTrainerPosts = findViewById(R.id.rvTrainerPosts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTrainerPosts.setLayoutManager(layoutManager);
        rvTrainerPosts.setNestedScrollingEnabled(false);

        postList = new ArrayList<>();
        postAdapter = new PostGridAdapter(this, postList);
        rvTrainerPosts.setAdapter(postAdapter);

        targetUserId = getIntent().getStringExtra("targetUserId");

        if (currentUserId != null && targetUserId != null && currentUserId.equals(targetUserId)) {
            btnConnect.setVisibility(View.GONE);
        } else {
            btnConnect.setVisibility(View.VISIBLE);
        }

        btnBack.setOnClickListener(v -> finish());

        if (targetUserId != null) {
            loadTrainerData(targetUserId);
            loadTrainerPosts(targetUserId);

            if (currentUserId != null && !currentUserId.equals(targetUserId)) {
                checkRequestStatus();
            }
        }

        btnConnect.setOnClickListener(v -> {
            if (isConnected) {
                Intent intent = new Intent(TrainerProfileActivity.this, ChatActivity.class);
                intent.putExtra("RECEIVER_ID", targetUserId);
                intent.putExtra("RECEIVER_NAME", tvName.getText().toString());
                startActivity(intent);
            } else if (!isRequestSent) {
                sendConnectRequestToDatabase();
            }
        });
    }

    private void checkRequestStatus() {
        String requestId = currentUserId + "_" + targetUserId;
        db.collection("connect_requests").document(requestId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String status = documentSnapshot.getString("status");
                        if ("accepted".equals(status)) {
                            isConnected = true;
                            isRequestSent = false;
                        } else {
                            isConnected = false;
                            isRequestSent = true;
                        }
                    } else {
                        isConnected = false;
                        isRequestSent = false;
                    }
                    updateButtonUI();
                });
    }

    private void sendConnectRequestToDatabase() {
        if (currentUserId == null || targetUserId == null) return;
        btnConnect.setEnabled(false);

        String requestId = currentUserId + "_" + targetUserId;
        Map<String, Object> request = new HashMap<>();
        request.put("fromUid", currentUserId);
        request.put("toUid", targetUserId);
        request.put("status", "pending");
        request.put("timestamp", System.currentTimeMillis());

        db.collection("connect_requests").document(requestId)
                .set(request)
                .addOnSuccessListener(aVoid -> {
                    isRequestSent = true;
                    isConnected = false;
                    updateButtonUI();
                    Toast.makeText(this, "Request Sent!", Toast.LENGTH_SHORT).show();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("trainerId", targetUserId);
                    returnIntent.putExtra("isRequestSent", true);
                    setResult(RESULT_OK, returnIntent);
                })
                .addOnFailureListener(e -> {
                    btnConnect.setEnabled(true);
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateButtonUI() {
        if (isConnected) {
            btnConnect.setText("Message");
            btnConnect.setEnabled(true);
            btnConnect.setAlpha(1.0f);

            btnConnect.setTextColor(Color.WHITE);
            btnConnect.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            btnConnect.setAllCaps(false);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(Color.TRANSPARENT);
            float density = getResources().getDisplayMetrics().density;
            drawable.setStroke((int)(1 * density), Color.WHITE);
            drawable.setCornerRadius(20 * density);
            btnConnect.setBackground(drawable);

        } else {
            btnConnect.setBackgroundResource(R.drawable.bg_button_orange_gradient);
            btnConnect.setTextColor(Color.WHITE);
            btnConnect.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            btnConnect.setAllCaps(false);

            if (isRequestSent) {
                btnConnect.setText("Request Sent ✓");
                btnConnect.setEnabled(false);
                btnConnect.setAlpha(0.7f);
            } else {
                btnConnect.setText("Connect Now");
                btnConnect.setEnabled(true);
                btnConnect.setAlpha(1.0f);
            }
        }
    }

    private void loadTrainerData(String uid) {
        db.collection("users").document(uid).get().addOnSuccessListener(document -> {
            if (isFinishing()) return;
            if (document.exists()) {
                String name = document.getString("name");
                tvName.setText(name != null ? name : "Unknown Trainer");

                String avatar = document.getString("avatar");
                if (avatar != null && !avatar.isEmpty()) {
                    Glide.with(this)
                            .load(avatar)
                            .placeholder(R.drawable.defaultavt)
                            .error(R.drawable.defaultavt)
                            .centerCrop()
                            .into(imgCover);
                } else {
                    imgCover.setImageResource(R.drawable.defaultavt);
                }

                String username = document.getString("username");
                tvUsername.setText(username != null ? "@" + username : "@" + uid.substring(0, Math.min(uid.length(), 6)));

                String primaryGoal = document.getString("primaryGoal");
                if (primaryGoal != null && !primaryGoal.isEmpty()) {
                    String formattedGoal = primaryGoal.substring(0, 1).toUpperCase() + primaryGoal.substring(1);
                    tvTitle.setText(formattedGoal + " Coach");
                } else {
                    tvTitle.setText("Fitness Trainer");
                }

                String level = document.getString("fitnessLevel");
                String levelText = "Junior Trainer (< 5 years)";

                if (level != null) {
                    switch (level.toLowerCase().trim()) {
                        case "advanced":
                            levelText = "Master Trainer (10+ years)";
                            break;
                        case "intermediate":
                            levelText = "Senior Trainer (5-10 years)";
                            break;
                        case "beginner":
                            levelText = "Junior Trainer (< 5 years)";
                            break;
                    }
                }
                tvSpecialty.setText(levelText);

                String locationName = document.getString("locationName");
                tvLocation.setText(locationName != null ? locationName : "Location Unknown");

                String aboutMe = document.getString("aboutMe");
                tvAbout.setText(aboutMe != null ? aboutMe : "No bio available.");
            }
        }).addOnFailureListener(e -> {
            Log.e("TrainerProfile", "Error loading trainer data", e);
        });
    }

    private void loadTrainerPosts(String uid) {
        db.collection("posts")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Post post = doc.toObject(Post.class);
                            if (post != null) {
                                post.setPostId(doc.getId());
                                postList.add(post);
                            }
                        }
                        postAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Log.e("TrainerProfile", "Error loading posts", e));
    }
}