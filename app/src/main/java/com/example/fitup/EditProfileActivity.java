package com.example.fitup;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;

    // UI Views
    private ImageView ivAvatar;
    private View rowName, rowLocation, rowGender, rowBirthday;
    private View rowFitnessGoal, rowFitnessLevel, rowWeight, rowHeight;

    // Bottom Sheet
    private FrameLayout standardBottomSheet;
    private BottomSheetBehavior<FrameLayout> standardBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI
        initializeViews();
        setupBottomSheet();
        setupClickListeners();

        // Load data from Firestore
        loadAndListenForUserData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove the listener to prevent memory leaks when the activity is not visible
        if (userListener != null) {
            userListener.remove();
        }
    }

    private void initializeViews() {
        ivAvatar = findViewById(R.id.ivAvatar);

        // Personal Info Rows
        rowName = findViewById(R.id.rowName);
        //rowUsername = findViewById(R.id.rowUsername);
        rowLocation = findViewById(R.id.rowLocation);
        rowGender = findViewById(R.id.rowGender);
        rowBirthday = findViewById(R.id.rowBirthday);

        // Fitness Rows
        rowFitnessGoal = findViewById(R.id.rowFitnessGoal);
        rowFitnessLevel = findViewById(R.id.rowFitnessLevel);

        // Body Measurement Rows
        rowWeight = findViewById(R.id.rowWeight);
        rowHeight = findViewById(R.id.rowHeight);

        // Back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish()); // Go back to the previous activity
    }

    private void setupBottomSheet() {
        standardBottomSheet = findViewById(R.id.standard_bottom_sheet);
        standardBottomSheetBehavior = BottomSheetBehavior.from(standardBottomSheet);

        // Keep these settings for robustness
        standardBottomSheetBehavior.setSkipCollapsed(true);
        standardBottomSheetBehavior.setPeekHeight(0);

        // Set the default state to hidden
        standardBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // --- NEW AND IMPROVED FIX ---
        // This callback will fire whenever a fragment inside this activity's
        // FragmentManager goes through its lifecycle.
        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, @Nullable Bundle savedInstanceState) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState);
                // We only care about fragments inside our bottom sheet container
                if (f.getId() == R.id.standard_bottom_sheet) {
                    // Post the state change to the next frame after the view has been created and measured.
                    v.post(() -> {
                        // This ensures the sheet expands after the fragment's layout is complete.
                        standardBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    });
                }
            }
        }, false);
        // --- END OF NEW FIX ---

        standardBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    clearFragmentContainer();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Not needed
            }
        });
    }

    private void setupClickListeners() {
        // When a row is clicked, load the corresponding fragment and show the bottom sheet
        rowName.setOnClickListener(v -> showEditFragment(new EditNameFragment(), "Edit Name"));
        //rowUsername.setOnClickListener(v -> showEditFragment(new EditUsernameFragment(), "Edit Username"));
        // Add listeners for all other rows...
        // rowLocation.setOnClickListener(v -> showEditFragment(new EditLocationFragment(), "Edit Location"));
        // rowGender.setOnClickListener(v -> showEditFragment(new EditGenderFragment(), "Edit Gender"));
    }

    private void loadAndListenForUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "No user logged in.");
            Toast.makeText(this, "No user logged in.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user
            return;
        }

        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());

        // Use a snapshot listener to get real-time updates
        userListener = userDocRef.addSnapshotListener(this, (snapshot, error) -> {
            if (error != null) {
                Log.e(TAG, "Listen failed.", error);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "User data updated. Populating UI.");
                populateUiWithData(snapshot);
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
    }

    private void populateUiWithData(DocumentSnapshot snapshot) {
        // Set Avatar
        String avatarUrl = snapshot.getString("avatarUrl");
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this).load(avatarUrl).circleCrop().into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.user); // Default avatar
        }

        // Set values for each row
        updateRow(rowName, "Name", snapshot.getString("name"));
        //updateRow(rowUsername, "Username", snapshot.getString("username"));
        updateRow(rowGender, "Gender", snapshot.getString("gender"));
        updateRow(rowFitnessGoal, "Fitness Goal", snapshot.getString("fitnessGoal"));
        updateRow(rowFitnessLevel, "Fitness Level", snapshot.getString("fitnessLevel"));

        // **FIXED**: Handle GeoPoint for location
        GeoPoint location = snapshot.getGeoPoint("location");
        String locationString = "Not set";
        if (location != null) {
            // Format the GeoPoint into a readable string
            locationString = String.format(Locale.US, "Lat: %.4f, Lon: %.4f",
                    location.getLatitude(), location.getLongitude());
        }
        updateRow(rowLocation, "Location", locationString);


        // Handle numeric values for weight and height
        Number weight = snapshot.getLong("weight");
        Number height = snapshot.getLong("height");
        updateRow(rowWeight, "Weight", weight != null ? weight + " kg" : null);
        updateRow(rowHeight, "Height", height != null ? height + " cm" : null);

        // Format and display birthday
        com.google.firebase.Timestamp birthdayTimestamp = snapshot.getTimestamp("birthday");
        if (birthdayTimestamp != null) {
            Date birthdayDate = birthdayTimestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            updateRow(rowBirthday, "Birthday", sdf.format(birthdayDate));
        } else {
            updateRow(rowBirthday, "Birthday", null);
        }
    }

    /**
     * Helper method to update the text in a 'include_profile_row' layout.
     * @param rowView The view of the included layout (e.g., findViewById(R.id.rowName))
     * @param label The static label for the row (e.g., "Name")
     * @param value The dynamic value from Firestore. If null or empty, a placeholder is shown.
     */
    private void updateRow(View rowView, String label, String value) {
        TextView tvLabel = rowView.findViewById(R.id.tvLabel);
        TextView tvValue = rowView.findViewById(R.id.tvValue);

        tvLabel.setText(label);
        if (value != null && !value.isEmpty() && !value.equals("Not set")) {
            tvValue.setText(value);
            tvValue.setTextColor(getResources().getColor(android.R.color.white)); // Default text color
        } else {
            tvValue.setText("Not set"); // Placeholder text
            tvValue.setTextColor(getResources().getColor(R.color.gray)); // Optional: style placeholder
        }
    }


    /**
     * Replaces the content of the bottom sheet's FrameLayout with a new fragment.
     * The expansion is now handled by the FragmentLifecycleCallbacks.
     * @param fragment The fragment instance to display.
     * @param tag A tag for the fragment transaction.
     */
    private void showEditFragment(Fragment fragment, String tag) {
        // Only load a new fragment if the sheet is currently hidden.
        if (standardBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.standard_bottom_sheet, fragment, tag)
                    .commit();
        }
    }


    /**
     * Clears the fragment from the container.
     */
    private void clearFragmentContainer() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.standard_bottom_sheet);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }
}
