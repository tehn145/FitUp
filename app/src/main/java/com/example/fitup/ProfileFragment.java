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
import androidx.constraintlayout.widget.ConstraintLayout;
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

    private ImageView ivProfileAvatar;
    private TextView tvProfileName, tvProfileId;
    private TextView tvGemsCount, tvConnectionsCount, tvFollowersCount, tvFollowingCount;
    private TextView labelConnections;
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
    private ListenerRegistration connOutListener;
    private ListenerRegistration connInListener;

    private ConstraintLayout cardConnection;

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
        labelConnections = view.findViewById(R.id.textView14);
        txtRequestCount = view.findViewById(R.id.txtRequestCount);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnSettings = view.findViewById(R.id.btnSettings);
        cardConnection = view.findViewById(R.id.cardConnection);

        cardConnection.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ConnectionsActivity.class);
            startActivity(intent);
        });

        View.OnClickListener openConnections = v -> {
            Intent intent = new Intent(getActivity(), ConnectionsActivity.class);
            startActivity(intent);
        };
        tvConnectionsCount.setOnClickListener(openConnections);
        if(labelConnections != null) labelConnections.setOnClickListener(openConnections);

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

        ivProfileAvatar.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // Fetch the user's role from Firestore before redirecting
                db.collection("users").document(currentUser.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String role = documentSnapshot.getString("role");
                                Intent intent;

                                // Check if the user is a trainer
                                if ("trainer".equalsIgnoreCase(role)) {
                                    intent = new Intent(getActivity(), TrainerProfileActivity.class);
                                } else {
                                    intent = new Intent(getActivity(), UserProfileActivity.class);
                                }

                                intent.putExtra("targetUserId", currentUser.getUid());
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error fetching profile", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadAndListenForUserData();
        loadMyPosts();
        listenForPendingRequests();
        loadCounters();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userListener != null) userListener.remove();
        if (requestCountListener != null) requestCountListener.remove();
        if (connOutListener != null) connOutListener.remove();
        if (connInListener != null) connInListener.remove();
    }

    private void loadCounters() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;
        String uid = currentUser.getUid();

        connOutListener = db.collection("connect_requests")
                .whereEqualTo("fromUid", uid)
                .whereEqualTo("status", "accepted")
                .addSnapshotListener((snapshots1, e1) -> {
                    if (e1 != null) return;
                    int count1 = (snapshots1 != null) ? snapshots1.size() : 0;

                    connInListener = db.collection("connect_requests")
                            .whereEqualTo("toUid", uid)
                            .whereEqualTo("status", "accepted")
                            .addSnapshotListener((snapshots2, e2) -> {
                                if (e2 != null) return;
                                int count2 = (snapshots2 != null) ? snapshots2.size() : 0;

                                int total = count1 + count2;
                                if (tvConnectionsCount != null) {
                                    tvConnectionsCount.setText(String.valueOf(total));
                                }
                            });
                });

        if (tvFollowersCount != null) tvFollowersCount.setText("0");
        if (tvFollowingCount != null) tvFollowingCount.setText("0");
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
//commit
    private void loadAndListenForUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "No user is logged in.");
            if(tvProfileName != null) tvProfileName.setText("Not Logged In");
            return;
        }

        String userId = currentUser.getUid();

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
        if(tvProfileId != null) {
            if (username != null && !username.isEmpty()) {
                tvProfileId.setText("@" + username);
            } else {
                tvProfileId.setText("ID: " + snapshot.getId().substring(0, 6));
            }
        }

        String avatarUrl = snapshot.getString("avatar");
        if(ivProfileAvatar != null) {
            Glide.with(getContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.defaultavt)
                    .error(R.drawable.defaultavt)
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