package com.example.fitup;import android.content.Intent; // Import Intent
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;import android.view.ViewGroup;
import android.widget.Button; // Import Button
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView ivProfileAvatar;
    private TextView tvProfileName, tvProfileId;
    private TextView tvGemsCount, tvConnectionsCount, tvFollowersCount, tvFollowingCount;
    private LinearLayout btnEditProfile; // Declare the Edit Profile button
    private ImageView btnSettings; // Declare the Settings button

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;

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

        // Find the Edit Profile button
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // Find the Settings button
        btnSettings = view.findViewById(R.id.btnSettings);

        // Set the click listener for Edit Profile
        btnEditProfile.setOnClickListener(v -> {
            // Create an Intent to start EditProfileActivity
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        // Set the click listener for Settings
        btnSettings.setOnClickListener(v -> {
            // Create an Intent to start SettingsActivity
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadAndListenForUserData();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userListener != null) {
            userListener.remove();
        }
    }

    private void loadAndListenForUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "No user is logged in.");
            if(tvProfileName != null) tvProfileName.setText("Not Logged In");
            if(tvProfileId != null) tvProfileId.setText("");
            return;
        }

        String userId = currentUser.getUid();
        if(tvProfileId != null) tvProfileId.setText(String.format("ID: %s", userId));

        userListener = db.collection("users").document(userId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "User data found. Populating UI.");
                        populateUiWithData(snapshot);
                    } else {
                        Log.d(TAG, "Current user data: null. Document might not be created yet.");
                        if(tvProfileName != null) tvProfileName.setText("Profile not found");
                    }
                });
    }

    private void populateUiWithData(DocumentSnapshot snapshot) {
        if (getContext() == null) return;

        String name = snapshot.getString("name");
        if(tvProfileName != null) tvProfileName.setText(name != null ? name : "No Name");

        String avatarUrl = snapshot.getString("avatarUrl");
        if(ivProfileAvatar != null) {
            Glide.with(getContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .circleCrop()
                    .into(ivProfileAvatar);
        }

        Long gems = snapshot.getLong("gem");
        if(tvGemsCount != null) tvGemsCount.setText(String.format(Locale.getDefault(), "%d", gems != null ? gems : 0L));

        int connectionsSize = 0;
        Object connectionsObj = snapshot.get("connections");
        if (connectionsObj instanceof List) {
            connectionsSize = ((List<?>) connectionsObj).size();
        }
        if(tvConnectionsCount != null) tvConnectionsCount.setText(String.valueOf(connectionsSize));

        int followersSize = 0;
        Object followersObj = snapshot.get("followers");
        if (followersObj instanceof List) {
            followersSize = ((List<?>) followersObj).size();
        }
        if(tvFollowersCount != null) tvFollowersCount.setText(String.valueOf(followersSize));

        int followingSize = 0;
        Object followingObj = snapshot.get("following");
        if (followingObj instanceof List) {
            followingSize = ((List<?>) followingObj).size();
        }
        if(tvFollowingCount != null) tvFollowingCount.setText(String.valueOf(followingSize));
    }
}
