package com.example.fitup;

import android.content.Intent; // Import Intent
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView imgCover;
    private TextView tvName, tvUsername, tvLocation, tvGoals, tvLevel, tvAvailability;
    private TextView tvSeeMorePosts; // 1. Add Variable
    private RecyclerView rvUserPosts;
    private AppCompatButton btnFollow;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private FirebaseFirestore db;
    private String targetUserId;
    private PostGridAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        imgCover = findViewById(R.id.imgCover);
        Toolbar toolbar = findViewById(R.id.toolbar);
        tvName = findViewById(R.id.tvProfileName);
        tvUsername = findViewById(R.id.tvProfileUsername);
        tvLocation = findViewById(R.id.tvProfileLocation);
        tvGoals = findViewById(R.id.tvFitnessGoals);
        tvLevel = findViewById(R.id.tvFitnessLevel);
        tvAvailability = findViewById(R.id.tvAvailability);
        tvSeeMorePosts = findViewById(R.id.tv_see_more); // 2. Initialize View
        rvUserPosts = findViewById(R.id.rvUserPosts);
        btnFollow = findViewById(R.id.btn_follow);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        targetUserId = getIntent().getStringExtra("targetUserId");

        // 3. Add Click Listener
        tvSeeMorePosts.setOnClickListener(v -> {
            if (targetUserId != null) {
                Intent intent = new Intent(UserProfileActivity.this, PostsActivity.class);
                intent.putExtra("userId", targetUserId);
                startActivity(intent);
            }
        });

        if (targetUserId != null) {
            loadUserProfile(targetUserId);
            loadUserPosts(targetUserId);
            setupFollowLogic();
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadUserProfile(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");
                        tvName.setText(name != null ? name : "Unknown");
                        tvUsername.setText(uid);

                        String avatar = document.getString("avatar");
                        if (avatar != null && !avatar.isEmpty()) {
                            Glide.with(this)
                                    .load(avatar)
                                    .centerCrop()
                                    .placeholder(R.drawable.defaultavt)
                                    .into(imgCover);
                        } else {
                            imgCover.setImageResource(R.drawable.defaultavt);
                        }

                        String locationName = document.getString("locationName");
                        if (locationName != null && !locationName.isEmpty()) {
                            tvLocation.setText(locationName);
                        } else {
                            tvLocation.setText("Location not specified");
                        }

                        String goals = document.getString("primaryGoal");
                        tvGoals.setText(goals != null ? capitalizeFirstLetter(goals) : "Not specified");

                        String level = document.getString("fitnessLevel");
                        tvLevel.setText(level != null ? capitalizeFirstLetter(level) : "Beginner");

                        String availability = document.getString("availability");
                        tvAvailability.setText(availability != null ? availability : "Flexible");

                    }
                })
                .addOnFailureListener(e -> Log.e("UserProfile", "Error loading profile", e));
    }

    private void setupFollowLogic() {
        if (currentUserId == null || currentUserId.equals(targetUserId)) {
            btnFollow.setVisibility(View.GONE);
            return;
        }

        FirestoreHelper.checkIsFollowing(targetUserId, isFollowing -> {
            updateFollowButtonUI(isFollowing);
            btnFollow.setTag(isFollowing); // Store state in the button tag
        });

        btnFollow.setOnClickListener(v -> {
            if (btnFollow.getTag() == null) return; // Wait for data to load

            boolean isCurrentlyFollowing = (boolean) btnFollow.getTag();

            updateFollowButtonUI(!isCurrentlyFollowing);
            btnFollow.setTag(!isCurrentlyFollowing);

            FirestoreHelper.toggleFollow(targetUserId, isCurrentlyFollowing, success -> {
                if (!success) {
                    updateFollowButtonUI(isCurrentlyFollowing);
                    btnFollow.setTag(isCurrentlyFollowing);
                    Toast.makeText(UserProfileActivity.this, "Action failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateFollowButtonUI(boolean isFollowing) {
        if (isFollowing) {
            btnFollow.setText("Following");
            btnFollow.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_button_orange_gradient));
        } else {
            btnFollow.setText("Follow Now");
            btnFollow.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_button_gray_gradient));
        }
        btnFollow.setVisibility(View.VISIBLE);
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        if (str.length() > 1) return str.substring(0, 1).toUpperCase() + str.substring(1);
        return str.toUpperCase();
    }

    private void loadUserPosts(String uid) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvUserPosts.setLayoutManager(layoutManager);
        rvUserPosts.setNestedScrollingEnabled(false);

        postList = new ArrayList<>();
        postAdapter = new PostGridAdapter(this, postList);
        rvUserPosts.setAdapter(postAdapter);

        db.collection("posts")
                .whereEqualTo("userId", uid)
                .limit(3)
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
                .addOnFailureListener(e -> Log.e("UserProfile", "Error loading posts", e));
    }
}
