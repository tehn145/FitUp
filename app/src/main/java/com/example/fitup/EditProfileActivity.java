package com.example.fitup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ListenerRegistration userListener;

    // UI Views
    private ImageView ivAvatar;
    private View rowName, rowLocation, rowGender, rowBirthday;
    private View rowFitnessGoal, rowFitnessLevel, rowWeight, rowHeight;
    private View rowAboutMe, rowAvailability;
    // Bottom Sheet
    private FrameLayout standardBottomSheet;
    private BottomSheetBehavior<FrameLayout> standardBottomSheetBehavior;

    // ActivityResultLauncher for picking an image
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    Uri imageUri = result.getData().getData();
                    uploadImageToFirebaseStorage(imageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance(); // Initialize Firebase Storage

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
        if (userListener != null) {
            userListener.remove();
        }
    }

    private void initializeViews() {
        ivAvatar = findViewById(R.id.ivAvatar);

        rowName = findViewById(R.id.rowName);
        rowLocation = findViewById(R.id.rowLocation);
        rowGender = findViewById(R.id.rowGender);
        rowBirthday = findViewById(R.id.rowBirthday);
        rowFitnessGoal = findViewById(R.id.rowFitnessGoal);
        rowFitnessLevel = findViewById(R.id.rowFitnessLevel);
        rowWeight = findViewById(R.id.rowWeight);
        rowHeight = findViewById(R.id.rowHeight);
        rowAboutMe = findViewById(R.id.rowAboutMe);
        rowAvailability = findViewById(R.id.rowAvailability);

        setRowIcon(rowName, R.drawable.ic_username2);
        setRowIcon(rowLocation, R.drawable.ic_location2);
        setRowIcon(rowGender, R.drawable.ic_gender);
        setRowIcon(rowBirthday, R.drawable.ic_calendar);
        setRowIcon(rowFitnessGoal, R.drawable.ic_goal);
        setRowIcon(rowFitnessLevel, R.drawable.ic_level);
        setRowIcon(rowWeight, R.drawable.ic_person);
        setRowIcon(rowHeight, R.drawable.ic_person);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupBottomSheet() {
        standardBottomSheet = findViewById(R.id.standard_bottom_sheet);
        standardBottomSheetBehavior = BottomSheetBehavior.from(standardBottomSheet);
        standardBottomSheetBehavior.setSkipCollapsed(true);
        standardBottomSheetBehavior.setPeekHeight(0);
        standardBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, @Nullable Bundle savedInstanceState) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState);
                if (f.getId() == R.id.standard_bottom_sheet) {
                    v.post(() -> standardBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
                }
            }
        }, false);

        standardBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    clearFragmentContainer();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private void setupClickListeners() {
        // Add click listener for the avatar
        ivAvatar.setOnClickListener(v -> openGallery());

        rowName.setOnClickListener(v -> showEditFragment(new EditNameFragment(), "Edit Name"));
        rowLocation.setOnClickListener(v -> showEditFragment(new EditLocationFragment(), "Edit Location"));
        rowBirthday.setOnClickListener(v -> showEditFragment(new EditBDayFragment(), "Edit Birthday"));
        rowHeight.setOnClickListener(v -> showEditFragment(new EditHeightFragment(), "Edit Height"));
        rowWeight.setOnClickListener(v -> showEditFragment(new EditWeightFragment(), "Edit Weight"));
        rowFitnessGoal.setOnClickListener(v -> showEditFragment(new EditFGoalFragment(), "Edit Fitness Goal"));
        rowFitnessLevel.setOnClickListener(v -> showEditFragment(new EditFLevelFragment(), "Edit Fitness Level"));
        rowGender.setOnClickListener(v -> showEditFragment(new EditGenderFragment(), "Edit Gender"));
        rowAboutMe.setOnClickListener(v -> showEditFragment(new EditAboutMeFragment(), "Edit About Me"));
        rowAvailability.setOnClickListener(v -> showEditFragment(new EditAvailabilityFragment(), "Edit Availability"));
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You must be logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();

        // Create a unique path for the image in Firebase Storage
        String filename = UUID.randomUUID().toString();
        StorageReference storageRef = storage.getReference().child("avatars/" + user.getUid() + "/" + filename);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(this::updateAvatarUrlInFirestore))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Image upload failed", e);
                });
    }

    private void updateAvatarUrlInFirestore(Uri downloadUri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        DocumentReference userDocRef = db.collection("users").document(user.getUid());
        userDocRef.update("avatar", downloadUri.toString())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Avatar URL updated in Firestore.");

                    Glide.with(this)
                            .load(downloadUri)
                            .placeholder(R.drawable.user)
                            .error(R.drawable.user)
                            .circleCrop()
                            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE) // Do not cache this specific image request on disk
                            .skipMemoryCache(true)
                            .into(ivAvatar);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update profile picture.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to update avatar URL in Firestore.", e);
                });
    }

    private void loadAndListenForUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "No user logged in.");
            finish();
            return;
        }

        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
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
        String avatarUrl = snapshot.getString("avatar");
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl).placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .circleCrop()
                    .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.user);
        }

        updateRow(rowName, "Name", snapshot.getString("name"));
        updateRow(rowGender, "Gender", capitalizeFirstLetter(snapshot.getString("gender")));
        updateRow(rowFitnessGoal, "Fitness Goal", capitalizeFirstLetter(snapshot.getString("primaryGoal")));
        updateRow(rowFitnessLevel, "Fitness Level", capitalizeFirstLetter(snapshot.getString("fitnessLevel")));
        updateRow(rowAboutMe, "About Me", snapshot.getString("aboutMe"));
        updateRow(rowAvailability, "Availability", snapshot.getString("availability"));

        GeoPoint location = snapshot.getGeoPoint("location");
        String storedLocationName = snapshot.getString("locationName"); // Check if we already saved the name

        if (storedLocationName != null && !storedLocationName.isEmpty()) {
            updateRow(rowLocation, "Location", storedLocationName);
        } else if (location != null) {

            // Set coordinates as temporary placeholder
            String coordFallback = String.format(Locale.US, "Lat: %.2f, Lon: %.2f",
                    location.getLatitude(), location.getLongitude());
            updateRow(rowLocation, "Location", coordFallback);

            new Thread(() -> {
                try {
                    android.location.Geocoder geocoder = new android.location.Geocoder(this, Locale.getDefault());
                    java.util.List<android.location.Address> addresses =
                            geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if (addresses != null && !addresses.isEmpty()) {
                        android.location.Address address = addresses.get(0);

                        // Try to get City, fallback to State/Region if City is null
                        String city = address.getLocality();
                        if (city == null) city = address.getSubAdminArea();
                        if (city == null) city = address.getAdminArea();

                        String country = address.getCountryName();

                        String finalLocationName;
                        if (city != null && country != null) {
                            finalLocationName = city + ", " + country;
                        } else if (country != null) {
                            finalLocationName = country;
                        } else {
                            finalLocationName = coordFallback;
                        }

                        // SAVE to Firestore so we don't need to geocode next time
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String nameToSave = finalLocationName; // effectively final for lambda
                            db.collection("users").document(user.getUid())
                                    .update("locationName", nameToSave)
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Location name saved to Firestore: " + nameToSave))
                                    .addOnFailureListener(e -> Log.e(TAG, "Failed to save location name", e));
                        }
                    }
                } catch (java.io.IOException e) {
                    Log.e(TAG, "Geocoder failed: " + e.getMessage());
                }
            }).start();
        } else {
            updateRow(rowLocation, "Location", "Not set");
        }
        // --- UPDATED LOCATION LOGIC END ---

        Number weight = snapshot.getLong("weight");
        Number height = snapshot.getLong("height");
        updateRow(rowWeight, "Weight", weight != null ? weight + " kg" : null);
        updateRow(rowHeight, "Height", height != null ? height + " cm" : null);

        com.google.firebase.Timestamp birthdayTimestamp = snapshot.getTimestamp("birthday");
        if (birthdayTimestamp != null) {
            Date birthdayDate = birthdayTimestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            updateRow(rowBirthday, "Birthday", sdf.format(birthdayDate));
        } else {
            updateRow(rowBirthday, "Birthday", null);
        }
    }


    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        //This is a more robust way to handle multi-word strings like "Build muscle"
        if (str.length() > 1) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str.toUpperCase();
    }


    private void updateRow(View rowView, String label, String value) {
        TextView tvLabel = rowView.findViewById(R.id.tvLabel);
        TextView tvValue = rowView.findViewById(R.id.tvValue);

        tvLabel.setText(label);
        if (value != null && !value.isEmpty() && !value.equals("Not set")) {
            tvValue.setText(value);
            tvValue.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        } else {
            tvValue.setText("Not set");
            tvValue.setTextColor(getResources().getColor(R.color.gray, getTheme()));
        }
    }

    private void showEditFragment(Fragment fragment, String tag) {
        if (standardBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.standard_bottom_sheet, fragment, tag)
                    .commit();
        }
    }

    private void clearFragmentContainer() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.standard_bottom_sheet);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private void setRowIcon(View row, int iconRes) {
        ImageView iv = row.findViewById(R.id.ivIcon);
        iv.setImageResource(iconRes);
    }
}
