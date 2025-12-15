package com.example.fitup;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditNameFragment extends Fragment {

    private static final String TAG = "EditNameFragment";

    private EditText etEditName;
    private Button btnSaveName;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        etEditName = view.findViewById(R.id.etEditName);
        btnSaveName = view.findViewById(R.id.btnSaveName);

        // Set up the save button's OnClickListener
        btnSaveName.setOnClickListener(v -> saveName());
    }

    private void saveName() {
        String newName = etEditName.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            etEditName.setError("Name cannot be empty");
            etEditName.requestFocus();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "You must be logged in to change your name.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.update("name", newName)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User name updated successfully.");
                    Toast.makeText(getContext(), "Name updated successfully", Toast.LENGTH_SHORT).show();
                    dismissBottomSheet();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating name", e);
                    Toast.makeText(getContext(), "Failed to update name. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void dismissBottomSheet() {
        // Find the hosting BottomSheetBehavior and collapse it
        View parent = (View) requireView().getParent();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
        if (behavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}

