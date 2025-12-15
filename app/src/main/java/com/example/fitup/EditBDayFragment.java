package com.example.fitup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditBDayFragment extends Fragment {
    private static final String TAG = "EditBirthdayFragment";

    private NumberPicker dayPicker, monthPicker, yearPicker;
    private Button btnSave, btnCancel;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Assuming layout is fragment_edit_birthday.xml or similar
        return inflater.inflate(R.layout.fragment_edit_birthday, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        dayPicker = view.findViewById(R.id.dayPicker);
        monthPicker = view.findViewById(R.id.monthPicker);
        yearPicker = view.findViewById(R.id.yearPicker);
        btnSave = view.findViewById(R.id.btnSaveBirthday);
        btnCancel = view.findViewById(R.id.btnCancel);

        setupPickers();

        btnSave.setOnClickListener(v -> saveBirthday());
        btnCancel.setOnClickListener(v -> dismissBottomSheet());
    }

    private void setupPickers() {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);

        // Month Picker
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setDisplayedValues(months);
        monthPicker.setValue(cal.get(Calendar.MONTH));

        // Year Picker
        yearPicker.setMinValue(currentYear - 100);
        yearPicker.setMaxValue(currentYear - 13); // Must be at least 13
        yearPicker.setValue(currentYear - 20); // Default to 20 years old

        // Day Picker
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        dayPicker.setValue(cal.get(Calendar.DAY_OF_MONTH));
    }

    private void saveBirthday() {
        int day = dayPicker.getValue();
        int month = monthPicker.getValue();
        int year = yearPicker.getValue();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date birthdayDate = calendar.getTime();

        Timestamp birthdayTimestamp = new Timestamp(birthdayDate);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Error: No user logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("birthday", birthdayTimestamp);

        db.collection("users").document(userId).set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Birthday saved successfully!");
                    Toast.makeText(getContext(), "Birthday Updated!", Toast.LENGTH_SHORT).show();
                    dismissBottomSheet();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(getContext(), "Failed to save birthday.", Toast.LENGTH_SHORT).show();
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
