package com.example.fitup;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class ManageAccountActivity extends AppCompatActivity {

    private static final String TAG = "ManageAccountActivity";

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ListenerRegistration userListener;

    // UI Views
    private ImageView btnBack;
    private TextView tvNameValue, tvEmailValue;
    private ConstraintLayout nameRow, passwordRow;

    // Bottom Sheet
    private FrameLayout standardBottomSheet;
    private BottomSheetBehavior<FrameLayout> standardBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI components
        initializeViews();
        setupBottomSheet();
        setupClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start listening for user data changes
        loadAndListenForUserData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop listening to prevent memory leaks
        if (userListener != null) {
            userListener.remove();
        }
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        tvNameValue = findViewById(R.id.tv_name_value);
        tvEmailValue = findViewById(R.id.tv_email_value);
        nameRow = findViewById(R.id.name_row);
        passwordRow = findViewById(R.id.password_row);
    }

    private void setupBottomSheet() {
        standardBottomSheet = findViewById(R.id.standard_bottom_sheet);
        standardBottomSheetBehavior = BottomSheetBehavior.from(standardBottomSheet);

        // Configure the bottom sheet to be fully hidden and skip the collapsed state
        standardBottomSheetBehavior.setSkipCollapsed(true);
        standardBottomSheetBehavior.setPeekHeight(0);
        standardBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // This callback handles expanding the sheet *after* the fragment's view is created.
        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, @Nullable Bundle savedInstanceState) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState);
                if (f.getId() == R.id.standard_bottom_sheet) {
                    v.post(() -> standardBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
                }
            }
        }, false);

        // This callback handles cleaning up the fragment when the sheet is hidden.
        standardBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    clearFragmentContainer();
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) { /* Not needed */ }
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        // When name row is clicked, show the EditNameFragment
        nameRow.setOnClickListener(v -> showEditFragment(new EditNameFragment(), "EditNameFragment"));

        // When password row is clicked, show the EditPasswordFragment
        passwordRow.setOnClickListener(v -> showEditFragment(new EditPasswordFragment(), "EditPasswordFragment"));
    }

    private void loadAndListenForUserData() {
        // Display the email immediately from Firebase Auth (it doesn't change often)
        tvEmailValue.setText(currentUser.getEmail());

        // Listen for real-time updates to the user's profile document (for the name)
        final DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
        userListener = userDocRef.addSnapshotListener(this, (snapshot, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                String name = snapshot.getString("name");
                tvNameValue.setText(name != null ? name : "Not Set");
            } else {
                Log.d(TAG, "Current data: null");
                tvNameValue.setText("Not Set");
            }
        });
    }

    /**
     * Replaces the content of the bottom sheet's FrameLayout with a new fragment.
     * The expansion logic is handled by the FragmentLifecycleCallbacks.
     * @param fragment The fragment instance to display.
     * @param tag A tag for the fragment transaction.
     */
    private void showEditFragment(Fragment fragment, String tag) {
        // Only load a new fragment if the sheet is currently hidden to prevent conflicts.
        if (standardBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.standard_bottom_sheet, fragment, tag)
                    .commit();
        }
    }

    /**
     * Clears the fragment from the bottom sheet container.
     * This is called when the sheet is hidden to clean up resources.
     */
    private void clearFragmentContainer() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.standard_bottom_sheet);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }
}
