package com.example.fitup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditGenderFragment extends Fragment {
    private static final String TAG = "EditGenderFragment";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_gender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        MaterialButton btnMale = view.findViewById(R.id.btnMale);
        MaterialButton btnFemale = view.findViewById(R.id.btnFemale);
        MaterialButton btnOther = view.findViewById(R.id.btnOther);

        btnMale.setOnClickListener(v -> saveGender("male"));
        btnFemale.setOnClickListener(v -> saveGender("female"));
        btnOther.setOnClickListener(v -> saveGender("other"));
    }

    private void saveGender(String gender) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Error: No user logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("gender", gender);

        db.collection("users").document(userId).set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Gender saved successfully!");
                    Toast.makeText(getContext(), "Gender Updated!", Toast.LENGTH_SHORT).show();
                    dismissBottomSheet();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(getContext(), "Failed to save gender.", Toast.LENGTH_SHORT).show();
                });
    }

    private void dismissBottomSheet() {
        View parent = (View) requireView().getParent();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
        if (behavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}
