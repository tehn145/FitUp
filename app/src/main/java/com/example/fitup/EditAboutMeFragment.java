package com.example.fitup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditAboutMeFragment extends Fragment {

    private EditText etAboutMe;
    private Button btnSave;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_aboutme, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etAboutMe = view.findViewById(R.id.etEditAboutMe);
        btnSave = view.findViewById(R.id.btnSaveAboutMe);

        loadCurrentAboutMe();

        btnSave.setOnClickListener(v -> saveAboutMe());
    }

    private void loadCurrentAboutMe() {
        if (mAuth.getCurrentUser() != null) {
            db.collection("users").document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String currentAboutMe = documentSnapshot.getString("aboutMe");
                            if (currentAboutMe != null) {
                                etAboutMe.setText(currentAboutMe);
                            }
                        }
                    });
        }
    }

    private void saveAboutMe() {
        String aboutMeText = etAboutMe.getText().toString().trim();

        if (mAuth.getCurrentUser() == null) return;

        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .update("aboutMe", aboutMeText)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "About Me updated!", Toast.LENGTH_SHORT).show();
                    closeBottomSheet();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update About Me.", Toast.LENGTH_SHORT).show();
                });
    }

    private void closeBottomSheet() {
        if (getActivity() != null) {
            View bottomSheet = getActivity().findViewById(R.id.standard_bottom_sheet);
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}
