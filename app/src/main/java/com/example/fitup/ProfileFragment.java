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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView ivSeeMore;
    private TextView tvSeeMoreBtn;
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

    private Set<String> outgoingConnectionIds = new HashSet<>();
    private Set<String> incomingConnectionIds = new HashSet<>();

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
        ivSeeMore = view.findViewById(R.id.ivSeeMore);
        tvSeeMoreBtn = view.findViewById(R.id.tvSeeMoreBtn);

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

        View.OnClickListener openFollowers = v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(getActivity(), FollowActivity.class);
                intent.putExtra("userId", user.getUid());
                intent.putExtra("initialTab", "followers");
                startActivity(intent);
            }
        };

        View.OnClickListener openFollowing = v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(getActivity(), FollowActivity.class);
                intent.putExtra("userId", user.getUid());
                intent.putExtra("initialTab", "following");
                startActivity(intent);
            }
        };

        if (tvFollowersCount != null) tvFollowersCount.setOnClickListener(openFollowers);
        if (tvFollowingCount != null) tvFollowingCount.setOnClickListener(openFollowing);

        View.OnClickListener openMyPosts = v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Intent intent = new Intent(getActivity(), PostsActivity.class);
                intent.putExtra("userId", currentUser.getUid());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "You need to be logged in.", Toast.LENGTH_SHORT).show();
            }
        };

        if (ivSeeMore != null) ivSeeMore.setOnClickListener(openMyPosts);
        if (tvSeeMoreBtn != null) tvSeeMoreBtn.setOnClickListener(openMyPosts);

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
                db.collection("users").document(currentUser.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String role = documentSnapshot.getString("role");
                                Intent intent;
                                if ("trainer".equalsIgnoreCase(role)) {
                                    intent = new Intent(getActivity(), TrainerProfileActivity.class);
                                } else {
                                    intent = new Intent(getActivity(), UserProfileActivity.class);
                                }
                                intent.putExtra("targetUserId", currentUser.getUid());
                                startActivity(intent);
                            }
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

        outgoingConnectionIds.clear();
        incomingConnectionIds.clear();

        if (connOutListener != null) connOutListener.remove();
        connOutListener = db.collection("connect_requests")
                .whereEqualTo("fromUid", uid)
                .whereEqualTo("status", "accepted")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    outgoingConnectionIds.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            String partnerId = doc.getString("toUid");
                            if (partnerId != null) outgoingConnectionIds.add(partnerId);
                        }
                    }
                    updateTotalConnectionCount();
                });

        if (connInListener != null) connInListener.remove();
        connInListener = db.collection("connect_requests")
                .whereEqualTo("toUid", uid)
                .whereEqualTo("status", "accepted")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    incomingConnectionIds.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            String partnerId = doc.getString("fromUid");
                            if (partnerId != null) incomingConnectionIds.add(partnerId);
                        }
                    }
                    updateTotalConnectionCount();
                });
    }

    private void updateTotalConnectionCount() {
        if (tvConnectionsCount == null) return;
        Set<String> uniqueConnections = new HashSet<>();
        uniqueConnections.addAll(outgoingConnectionIds);
        uniqueConnections.addAll(incomingConnectionIds);

        tvConnectionsCount.setText(String.valueOf(uniqueConnections.size()));
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
            if(tvProfileName != null) tvProfileName.setText("Not Logged In");
            return;
        }

        String userId = currentUser.getUid();

        userListener = db.collection("users").document(userId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) return;

                    if (snapshot != null && snapshot.exists()) {
                        populateUiWithData(snapshot);
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

        Long followerCount = snapshot.getLong("followerCount");
        if (tvFollowersCount != null) {
            tvFollowersCount.setText(String.valueOf(followerCount != null ? followerCount : 0));
        }

        Long followingCount = snapshot.getLong("followingCount");
        if (tvFollowingCount != null) {
            tvFollowingCount.setText(String.valueOf(followingCount != null ? followingCount : 0));
        }
    }

    private void loadMyPosts() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("posts")
                .whereEqualTo("userId", currentUser.getUid())
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
                });
    }
}