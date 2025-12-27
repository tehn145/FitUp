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

    private ImageView imgCover, btnBack, btnFollow;
    private TextView tvName, tvTitle, tvUsername, tvLocation, tvAbout, tvSpecialty, tvSeeMorePosts;
    private TextView tvAvgRating, tvSessionCount;
    private View btnSeeReviews;

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
    private boolean isIncomingRequest = false; // [NEW] Biến này để check request từ họ -> mình

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
        btnFollow = findViewById(R.id.btn_follow);

        tvName = findViewById(R.id.tvTrainerName);
        tvTitle = findViewById(R.id.tvTrainerTitle);
        tvSpecialty = findViewById(R.id.tvSpecialty);
        tvUsername = findViewById(R.id.tvTrainerUsername);
        tvLocation = findViewById(R.id.tvTrainerLocation);
        tvAbout = findViewById(R.id.tvAbout);
        tvSeeMorePosts = findViewById(R.id.tv_see_more);

        tvAvgRating = findViewById(R.id.tvAvgRating);
        tvSessionCount = findViewById(R.id.tvSessionCount);
        btnSeeReviews = findViewById(R.id.btnSeeReviews);

        btnConnect = findViewById(R.id.btnConnect);

        rvTrainerPosts = findViewById(R.id.rvTrainerPosts);
        rvTrainerPosts.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTrainerPosts.setNestedScrollingEnabled(false);

        postList = new ArrayList<>();
        postAdapter = new PostGridAdapter(this, postList);
        rvTrainerPosts.setAdapter(postAdapter);

        targetUserId = getIntent().getStringExtra("targetUserId");

        btnSeeReviews.setOnClickListener(v -> {
            if (targetUserId != null) {
                TrainerReviewsFragment fragment = TrainerReviewsFragment.newInstance(targetUserId);

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                android.R.anim.fade_in, android.R.anim.fade_out)
                        .add(android.R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        if (currentUserId != null && targetUserId != null && currentUserId.equals(targetUserId)) {
            btnConnect.setVisibility(View.GONE);
            btnFollow.setVisibility(View.GONE);
        } else {
            btnConnect.setVisibility(View.VISIBLE);
            // Mặc định set false, checkRequestStatus sẽ cập nhật lại
            isRequestSent = getIntent().getBooleanExtra("isAlreadySent", false);
            updateButtonUI();
        }

        btnBack.setOnClickListener(v -> finish());

        tvSeeMorePosts.setOnClickListener(v -> {
            Intent intent = new Intent(this, PostsActivity.class);
            intent.putExtra("userId", targetUserId);
            startActivity(intent);
        });

        if (targetUserId != null) {
            loadTrainerData(targetUserId);
            loadTrainerPosts(targetUserId);

            if (currentUserId != null && !currentUserId.equals(targetUserId)) {
                checkRequestStatus();
            }
        }

        if (currentUserId != null && targetUserId != null && !currentUserId.equals(targetUserId)) {
            FirestoreHelper.checkIsFollowing(targetUserId, isFollowing -> {
                updateFollowButtonUI(isFollowing);
                btnFollow.setTag(isFollowing);
            });

            btnFollow.setOnClickListener(v -> {
                if (btnFollow.getTag() == null) return;

                boolean isCurrentlyFollowing = (boolean) btnFollow.getTag();
                updateFollowButtonUI(!isCurrentlyFollowing);
                btnFollow.setTag(!isCurrentlyFollowing);

                FirestoreHelper.toggleFollow(targetUserId, isCurrentlyFollowing, success -> {
                    if (!success) {
                        updateFollowButtonUI(isCurrentlyFollowing);
                        btnFollow.setTag(isCurrentlyFollowing);
                        Toast.makeText(this, "Action failed", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        btnConnect.setOnClickListener(v -> {
            if (isConnected) {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("RECEIVER_ID", targetUserId);
                intent.putExtra("RECEIVER_NAME", tvName.getText().toString());
                startActivity(intent);
            } else if (isIncomingRequest) {
                // [UPDATE] Có incoming request -> Mở ConnectionsActivity
                Intent intent = new Intent(this, ConnectionsActivity.class);
                startActivity(intent);
            } else if (!isRequestSent) {
                sendConnectRequestToDatabase();
            }
        });
    }

    private void updateFollowButtonUI(boolean isFollowing) {
        btnFollow.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor(isFollowing ? "#FF9800" : "#333333")));
        btnFollow.setImageResource(
                isFollowing ? R.drawable.ic_followed : R.drawable.ic_addfriend2);
        btnFollow.setImageTintList(
                android.content.res.ColorStateList.valueOf(Color.WHITE));
    }

    // Đã chỉnh sửa logic để kiểm tra 2 chiều
    private void checkRequestStatus() {
        String requestId1 = currentUserId + "_" + targetUserId; // Mình gửi
        String requestId2 = targetUserId + "_" + currentUserId; // Họ gửi

        // Reset
        isConnected = false;
        isRequestSent = false;
        isIncomingRequest = false;

        // Check chiều mình gửi trước
        db.collection("connect_requests").document(requestId1)
                .get()
                .addOnSuccessListener(doc1 -> {
                    if (doc1.exists()) {
                        handleRequestDoc(doc1, true); // true = outgoing
                    } else {
                        // Nếu không thấy, check chiều họ gửi
                        db.collection("connect_requests").document(requestId2)
                                .get()
                                .addOnSuccessListener(doc2 -> {
                                    if (doc2.exists()) {
                                        handleRequestDoc(doc2, false); // false = incoming
                                    } else {
                                        // Không có quan hệ gì
                                        updateButtonUI();
                                    }
                                });
                    }
                });
    }

    private void handleRequestDoc(DocumentSnapshot doc, boolean isOutgoing) {
        String status = doc.getString("status");
        if ("accepted".equals(status)) {
            isConnected = true;
        } else if ("pending".equals(status)) {
            if (isOutgoing) {
                isRequestSent = true;
            } else {
                isIncomingRequest = true; // [UPDATE] Họ gửi mình -> incoming
            }
        }
        updateButtonUI();
    }

    private void sendConnectRequestToDatabase() {
        btnConnect.setEnabled(false);

        String requestId = currentUserId + "_" + targetUserId;
        Map<String, Object> request = new HashMap<>();
        request.put("fromUid", currentUserId);
        request.put("toUid", targetUserId);
        request.put("status", "pending");
        request.put("timestamp", System.currentTimeMillis());

        db.collection("connect_requests").document(requestId)
                .set(request)
                .addOnSuccessListener(v -> {
                    isRequestSent = true;
                    updateButtonUI();
                    Toast.makeText(this, "Request Sent!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    btnConnect.setEnabled(true);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateButtonUI() {
        if (isConnected) {
            btnConnect.setText("Message");
            btnConnect.setEnabled(true);
            btnConnect.setTextColor(Color.WHITE);
            btnConnect.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(Color.TRANSPARENT);
            drawable.setStroke(2, Color.WHITE);
            drawable.setCornerRadius(40);
            btnConnect.setBackground(drawable);
        } else if (isIncomingRequest) {
            btnConnect.setText("Respond");
            btnConnect.setEnabled(true);
            btnConnect.setTextColor(Color.WHITE);
            btnConnect.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            btnConnect.setBackgroundResource(R.drawable.bg_user_message);
        } else {
            btnConnect.setBackgroundResource(R.drawable.bg_button_orange_gradient);
            btnConnect.setTextColor(Color.WHITE);
            btnConnect.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            btnConnect.setText(isRequestSent ? "Request Sent ✓" : "Connect Now");
            btnConnect.setEnabled(!isRequestSent);
        }
    }

    private void loadTrainerData(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    tvName.setText(doc.getString("name"));
                    tvUsername.setText(uid);
                    tvAbout.setText(doc.getString("aboutMe"));

                    double rating = 0.0;
                    if (doc.contains("rating")) {
                        rating = doc.getDouble("rating");
                    }
                    tvAvgRating.setText(String.format("%.1f", rating));

                    long sessions = 0;
                    if (doc.contains("reviewCount")) {
                        sessions = doc.getLong("reviewCount");
                    }

                    tvSessionCount.setText(String.valueOf(sessions));

                    String goal = doc.getString("primaryGoal");
                    tvTitle.setText(goal != null ? goal + " Coach" : "Fitness Trainer");

                    String level = doc.getString("fitnessLevel");
                    String levelText = "Junior Trainer (< 5 years)";
                    if (level != null) {
                        switch (level.toLowerCase()) {
                            case "advanced": levelText = "Master Trainer (10+ years)"; break;
                            case "intermediate": levelText = "Senior Trainer (5-10 years)"; break;
                        }
                    }
                    tvSpecialty.setText(levelText);

                    String location = doc.getString("locationName");
                    tvLocation.setText(location != null ? location : "Location not specified");

                    Glide.with(this)
                            .load(doc.getString("avatar"))
                            .placeholder(R.drawable.defaultavt)
                            .into(imgCover);
                });
    }

    private void loadTrainerPosts(String uid) {
        db.collection("posts").whereEqualTo("userId", uid).limit(3).get()
                .addOnSuccessListener(qs -> {
                    postList.clear();
                    for (DocumentSnapshot d : qs) {
                        Post p = d.toObject(Post.class);
                        if (p != null) {
                            p.setPostId(d.getId());
                            postList.add(p);
                        }
                    }
                    postAdapter.notifyDataSetChanged();
                });
    }
}