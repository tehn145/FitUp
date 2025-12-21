package com.example.fitup;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
    private RecyclerView rvUserPosts;
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
        rvUserPosts = findViewById(R.id.rvUserPosts);

        db = FirebaseFirestore.getInstance();

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        targetUserId = getIntent().getStringExtra("targetUserId");

        if (targetUserId != null) {
            loadUserProfile(targetUserId);
            loadUserPosts(targetUserId);
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

                        String username = document.getString("username");
                        if (username != null) {
                            tvUsername.setText("@" + username);
                        } else {
                            tvUsername.setText("@" + uid.substring(0, 8));
                        }

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

                        GeoPoint location = document.getGeoPoint("location");
                        if (location != null) {
                            String locationString = String.format(Locale.US, "Lat: %.2f, Lon: %.2f",
                                    location.getLatitude(), location.getLongitude());
                            tvLocation.setText(locationString);
                        } else {
                            tvLocation.setText("Vietnam");
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