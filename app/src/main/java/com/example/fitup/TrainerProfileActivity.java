package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager; // Horizontal layout
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
    private TextView tvName, tvTitle, tvUsername, tvLocation, tvAbout;
    private AppCompatButton btnConnect;
    private RecyclerView rvTrainerPosts;
    private PostGridAdapter postAdapter;
    private List<Post> postList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String targetUserId;
    private String currentUserId;
    private boolean isRequestSent = false;

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
        isRequestSent = getIntent().getBooleanExtra("isAlreadySent", false);

        if (currentUserId != null && targetUserId != null && currentUserId.equals(targetUserId)) {
            btnConnect.setVisibility(View.GONE);
        } else {
            btnConnect.setVisibility(View.VISIBLE);
            isRequestSent = getIntent().getBooleanExtra("isAlreadySent", false);
            updateButtonUI();
        }

        btnBack.setOnClickListener(v -> finish());

        if (targetUserId != null) {
            loadTrainerData(targetUserId);
            loadTrainerPosts(targetUserId);
            if (currentUserId != null) {
                checkRequestStatus();
            }
        }

        if (currentUserId != null && targetUserId != null && !currentUserId.equals(targetUserId)) {
            FirestoreHelper.checkIsFollowing(targetUserId, isFollowing -> {
                updateFollowButtonUI(isFollowing);
                btnFollow.setTag(isFollowing); // Store state in the view tag
            });

            btnFollow.setOnClickListener(v -> {
                if (btnFollow.getTag() == null) return; // Wait for initial load

                boolean isCurrentlyFollowing = (boolean) btnFollow.getTag();

                updateFollowButtonUI(!isCurrentlyFollowing);
                btnFollow.setTag(!isCurrentlyFollowing);

                FirestoreHelper.toggleFollow(targetUserId, isCurrentlyFollowing, success -> {
                    if (!success) {
                        updateFollowButtonUI(isCurrentlyFollowing);
                        btnFollow.setTag(isCurrentlyFollowing);
                        Toast.makeText(TrainerProfileActivity.this, "Action failed", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            btnFollow.setVisibility(View.GONE);
        }

        btnConnect.setOnClickListener(v -> {
            if (!isRequestSent) {
                sendConnectRequestToDatabase();
            }
        });
    }

    private void updateFollowButtonUI(boolean isFollowing) {
        if (isFollowing) {
            btnFollow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF9800")));
            btnFollow.setImageResource(R.drawable.ic_followed);
            btnFollow.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
        } else {
            btnFollow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#333333")));
            btnFollow.setImageResource(R.drawable.ic_addfriend2);
            btnFollow.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
        }
    }

    private void checkRequestStatus() {
        String requestId = currentUserId + "_" + targetUserId;
        db.collection("connect_requests").document(requestId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        isRequestSent = true;
                        updateButtonUI();
                    }
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
                    updateButtonUI();
                    Toast.makeText(this, "Request Sent!", Toast.LENGTH_SHORT).show();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("trainerId", targetUserId);
                    returnIntent.putExtra("isRequestSent", true);
                    setResult(RESULT_OK, returnIntent);
                });
    }

    private void updateButtonUI() {
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

    private void loadTrainerData(String uid) {
        db.collection("users").document(uid).get().addOnSuccessListener(document -> {
            if (isFinishing()) return;
            if (document.exists()) {
                // Name
                String name = document.getString("name");
                tvName.setText(name != null ? name : "Unknown Trainer");

                // Avatar
                String avatar = document.getString("avatar");
                if (avatar != null && !avatar.isEmpty()) {
                    Glide.with(this)
                            .load(avatar)
                            .placeholder(R.drawable.defaultavt) // Ensure you have a placeholder
                            .error(R.drawable.defaultavt)
                            .centerCrop()
                            .into(imgCover);
                } else {
                    imgCover.setImageResource(R.drawable.defaultavt);
                }

                String username = targetUserId;
                if (username != null && !username.isEmpty()) {
                    tvUsername.setText("@" + username);
                } else {
                    // Fallback if no username set
                    tvUsername.setText("@trainer");
                }

                String primaryGoal = document.getString("primaryGoal");
                if (primaryGoal != null && !primaryGoal.isEmpty()) {
                    String formattedGoal = primaryGoal.substring(0, 1).toUpperCase() + primaryGoal.substring(1);
                    tvTitle.setText(formattedGoal + " Coach");
                } else {
                    tvTitle.setText("Fitness Trainer");
                }

                String locationName = document.getString("locationName");
                if (locationName != null && !locationName.isEmpty()) {
                    tvLocation.setText(locationName);
                } else {
                    tvLocation.setText("Location not specified");
                }

                // About / Bio
                String aboutMe = document.getString("aboutMe");
                if (aboutMe != null && !aboutMe.isEmpty()) {
                    tvAbout.setText(aboutMe);
                } else {
                    tvAbout.setText("No bio available for this trainer.");
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("TrainerProfile", "Error loading trainer data", e);
            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
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