package com.example.fitup;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PostsActivity extends AppCompatActivity {

    private RecyclerView rvPosts;
    private PostGridAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        db = FirebaseFirestore.getInstance();

        if (getIntent().hasExtra("userId")) {
            userId = getIntent().getStringExtra("userId");
        } else {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            } else {
                Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        // 3. Setup Header Elements
        ImageView btnBack = findViewById(R.id.imgbtn_Back);
        TextView tvTitle = findViewById(R.id.tvTitle);

        // Optional: Update title based on context (e.g., "John's Posts")
        // tvTitle.setText("Posts");

        btnBack.setOnClickListener(v -> finish());

        // 4. Setup RecyclerView
        rvPosts = findViewById(R.id.rvPosts);

        // Use a GridLayout with 3 columns (standard for profile posts)
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rvPosts.setLayoutManager(layoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostGridAdapter(this, postList, R.layout.item_profile_post_in_posts);
        rvPosts.setAdapter(postAdapter);

        // 5. Load Data
        loadUserPosts();
    }

    private void loadUserPosts() {
        if (userId == null) return;

        // Query: Get posts where "userId" matches, ordered by time
        db.collection("posts")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
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

                    } else {
                        Toast.makeText(this, "No posts yet.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PostsActivity", "Error loading posts", e);
                    Toast.makeText(this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                });
    }
}
