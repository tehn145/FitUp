package com.example.fitup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";

    // UI Components
    private ImageView btnBack;
    private FrameLayout mediaUploadArea;
    private LinearLayout placeholderLayout;
    private ImageView previewImageView;
    private EditText etCaption;
    private MaterialButton btnPost;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    // Data
    private Uri selectedImageUri = null;
    private String currentUserName = "User";
    private String currentUserAvatar = "";

    // Image Picker Launcher
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d(TAG, "Selected URI: " + uri);
                    selectedImageUri = uri;
                    updateUIWithImage(uri);
                } else {
                    Log.d(TAG, "No media selected");
                    // FIX 1: Reset UI if user cancels selection
                    if (selectedImageUri == null) {
                        resetUIToDefault();
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize Views
        btnBack = findViewById(R.id.btn_back);
        mediaUploadArea = findViewById(R.id.media_upload_area);
        etCaption = findViewById(R.id.etCaption);
        btnPost = findViewById(R.id.btnPost);

        mediaUploadArea.setClipToOutline(true);

        if (mediaUploadArea.getChildCount() > 0 && mediaUploadArea.getChildAt(0) instanceof LinearLayout) {
            placeholderLayout = (LinearLayout) mediaUploadArea.getChildAt(0);
        }

        // Add a programmatic ImageView
        previewImageView = new ImageView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        previewImageView.setLayoutParams(params);
        previewImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        previewImageView.setVisibility(View.GONE);
        mediaUploadArea.addView(previewImageView);

        fetchCurrentUserData();
        setupListeners();
    }

    private void fetchCurrentUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentUserName = documentSnapshot.getString("name");
                        currentUserAvatar = documentSnapshot.getString("avatar");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching user data", e));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        mediaUploadArea.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        btnPost.setOnClickListener(v -> handlePostUpload());
    }

    private void updateUIWithImage(Uri uri) {
        if (placeholderLayout != null) {
            placeholderLayout.setVisibility(View.GONE);
        }

        previewImageView.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(uri)
                .into(previewImageView);

        btnPost.setBackgroundTintList(getColorStateList(R.color.orange));
        btnPost.setTextColor(getColor(android.R.color.white));
    }

    // Helper method to reset UI
    private void resetUIToDefault() {
        if (placeholderLayout != null) {
            placeholderLayout.setVisibility(View.VISIBLE);
        }
        previewImageView.setVisibility(View.GONE);
        previewImageView.setImageDrawable(null); // Clear memory

        // Reset button style if needed (optional)
        // btnPost.setBackgroundTintList(...);
    }

    private void handlePostUpload() {
        String caption = etCaption.getText().toString().trim();

        if (TextUtils.isEmpty(caption) && selectedImageUri == null) {
            Toast.makeText(this, "Please write a caption or select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPost.setEnabled(false);
        btnPost.setText("Posting...");

        if (selectedImageUri != null) {
            uploadImageAndCreatePost(caption);
        } else {
            createPostInFirestore(caption, null);
        }
    }

    private void uploadImageAndCreatePost(String caption) {
        String filename = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child("post_images/" + filename);

        ref.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    createPostInFirestore(caption, imageUrl);
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(PostActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    btnPost.setEnabled(true);
                    btnPost.setText("Post");
                });
    }

    private void createPostInFirestore(String caption, @Nullable String imageUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        Post newPost = new Post();

        newPost.setUserId(user.getUid());
        newPost.setUserName(currentUserName);
        newPost.setUserAvatar(currentUserAvatar);
        newPost.setContent(caption);
        newPost.setImageUrl(imageUrl);
        newPost.setTimestamp(Timestamp.now());
        newPost.setLikeCount(0);
        newPost.setCommentCount(0);

        Map<String, Object> postData = new HashMap<>();
        postData.put("userId", newPost.getUserId());
        postData.put("userName", newPost.getUserName());
        postData.put("userAvatar", newPost.getUserAvatar());
        postData.put("content", newPost.getContent());
        postData.put("imageUrl", newPost.getImageUrl());
        postData.put("timestamp", newPost.getTimestamp());
        postData.put("likeCount", newPost.getLikeCount());
        postData.put("commentCount", newPost.getCommentCount());

        db.collection("posts")
                .add(postData)
                .addOnSuccessListener(documentReference -> {
                    String postId = documentReference.getId();

                    Map<String, Object> subCollectionData = new HashMap<>();
                    subCollectionData.put("timestamp", newPost.getTimestamp());

                    db.collection("users").document(user.getUid())
                            .collection("userPosts")
                            .document(postId)
                            .set(subCollectionData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(PostActivity.this, "Posted successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Post created but failed to link to user profile", e);
                                Toast.makeText(PostActivity.this, "Posted, but profile update failed.", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PostActivity.this, "Error posting: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error adding post", e);
                    btnPost.setEnabled(true);
                    btnPost.setText("Post");
                });
    }
}
