package com.example.fitup;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OnePostActivity extends AppCompatActivity {

    private static final String TAG = "OnePostActivity";
    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;
    private String targetPostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        ImageView btnBack = findViewById(R.id.imgbtn_Back);
        TextView tvTitle = findViewById(R.id.tvTitle);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        targetPostId = getIntent().getStringExtra("postId");

        if (targetPostId == null || targetPostId.isEmpty()) {
            Toast.makeText(this, "Error: Post ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);

        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsRecyclerView.setAdapter(postAdapter);

        btnBack.setOnClickListener(v -> finish());

        loadSinglePost(targetPostId);
    }

    private void loadSinglePost(String postId) {
        db.collection("posts").document(postId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Post post = documentSnapshot.toObject(Post.class);
                        if (post != null) {
                            post.setPostId(documentSnapshot.getId());

                            postList.clear();
                            postList.add(post);
                            postAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Post no longer exists", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading post", e);
                    Toast.makeText(this, "Failed to load post", Toast.LENGTH_SHORT).show();
                });
    }
}
