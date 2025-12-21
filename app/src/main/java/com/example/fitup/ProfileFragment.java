package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView ivProfileAvatar, btnBack;
    private TextView tvProfileName, tvProfileId;
    private TextView tvGemsCount, tvConnectionsCount, tvFollowersCount, tvFollowingCount;
    // --- THÊM BIẾN MỚI ---
    private TextView txtRequestCount;

    private LinearLayout btnEditProfile;
    private ImageView btnSettings;

    private RecyclerView rvMyPosts;
    private PostGridAdapter postAdapter;
    private List<Post> postList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;

    private ListenerRegistration requestCountListener;

    private androidx.constraintlayout.widget.ConstraintLayout cardConnection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ivProfileAvatar = view.findViewById(R.id.imgAvatar);
        tvProfileName = view.findViewById(R.id.txtName);
        tvProfileId = view.findViewById(R.id.txtUsername);
        tvGemsCount = view.findViewById(R.id.txtFitGemValue);
        tvConnectionsCount = view.findViewById(R.id.connectionCount);
        tvFollowersCount = view.findViewById(R.id.followerCount);
        tvFollowingCount = view.findViewById(R.id.followingCount);

        txtRequestCount = view.findViewById(R.id.txtRequestCount);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnSettings = view.findViewById(R.id.btnSettings);
        cardConnection = view.findViewById(R.id.cardConnection);

        cardConnection.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ConnectionsActivity.class);
            startActivity(intent);
        });

        rvMyPosts = view.findViewById(R.id.rvMyPosts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvMyPosts.setLayoutManager(layoutManager);
        rvMyPosts.setNestedScrollingEnabled(false);

        postList = new ArrayList<>();
        postAdapter = new PostGridAdapter(getContext(), postList);
        rvMyPosts.setAdapter(postAdapter);

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadAndListenForUserData();
        loadMyPosts();
        listenForPendingRequests();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userListener != null) {
            userListener.remove();
        }
        if (requestCountListener != null) {
            requestCountListener.remove();
        }
    }

    private void listenForPendingRequests() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        requestCountListener = db.collection("connect_requests")
                .whereEqualTo("toUid", currentUser.getUid())
                .whereEqualTo("status", "pending")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen request count failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        int count = snapshots.size();
                        if (txtRequestCount != null) {
                            txtRequestCount.setText(String.valueOf(count));
                        }
                    }
                });
    }

    private void loadAndListenForUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "No user is logged in.");
            if(tvProfileName != null) tvProfileName.setText("Not Logged In");
            return;
        }

        String userId = currentUser.getUid();
        if(tvProfileId != null) tvProfileId.setText(String.format("ID: %s", userId.substring(0, 6)));

        userListener = db.collection("users").document(userId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        populateUiWithData(snapshot);
                    } else {
                        if(tvProfileName != null) tvProfileName.setText("Profile not found");
                    }
                });
    }

    private void populateUiWithData(DocumentSnapshot snapshot) {
        if (getContext() == null) return;

        String name = snapshot.getString("name");
        if(tvProfileName != null) tvProfileName.setText(name != null ? name : "No Name");

        String username = snapshot.getString("username");
        if(tvProfileId != null && username != null) tvProfileId.setText("@" + username);

        String avatarUrl = snapshot.getString("avatar");
        if(ivProfileAvatar != null && avatarUrl != null) {
            Glide.with(getContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .circleCrop()
                    .into(ivProfileAvatar);
        }

        Long gems = snapshot.getLong("gem");
        if(tvGemsCount != null) tvGemsCount.setText(String.valueOf(gems != null ? gems : 0L));

    }

    private void loadMyPosts() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("posts")
                .whereEqualTo("userId", currentUser.getUid())
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
                .addOnFailureListener(e -> Log.e(TAG, "Error loading posts", e));
    }
}