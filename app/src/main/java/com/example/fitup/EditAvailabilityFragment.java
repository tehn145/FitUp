package com.example.fitup;

import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class EditAvailabilityFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_availability, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        MaterialButton btnMorning = view.findViewById(R.id.btnMorning);
        MaterialButton btnAfternoon = view.findViewById(R.id.btnAfternoon);
        MaterialButton btnEvening = view.findViewById(R.id.btnEvening);
        MaterialButton btnFlexible = view.findViewById(R.id.btnFlexible);

        btnMorning.setOnClickListener(v -> saveAvailability("Morning"));
        btnAfternoon.setOnClickListener(v -> saveAvailability("Afternoon"));
        btnEvening.setOnClickListener(v -> saveAvailability("Evening"));
        btnFlexible.setOnClickListener(v -> saveAvailability("Flexible"));
    }

    private void saveAvailability(String availability) {
        if (mAuth.getCurrentUser() == null) return;

        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .update("availability", availability)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Availability updated to " + availability, Toast.LENGTH_SHORT).show();
                    closeBottomSheet();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update availability.", Toast.LENGTH_SHORT).show();
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
