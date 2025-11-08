package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class pick_your_birthday extends AppCompatActivity {

    private NumberPicker pickerMonth, pickerDay, pickerYear;
    private MaterialButton buttonContinue;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "PickBirthdayActivity";

    // Array of months
    private final String[] months = new String[]{
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_your_birthday);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        pickerMonth = findViewById(R.id.pickerMonth);
        pickerDay = findViewById(R.id.pickerDay);
        pickerYear = findViewById(R.id.pickerYear);
        buttonContinue = findViewById(R.id.buttonContinue);

        setupPickers();

        buttonContinue.setOnClickListener(v -> saveBirthdayAndProceed());
    }

    private void setupPickers() {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH); // 0-indexed
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        pickerMonth.setMinValue(0);
        pickerMonth.setMaxValue(months.length - 1);
        pickerMonth.setDisplayedValues(months);
        pickerMonth.setValue(currentMonth);

        pickerYear.setMinValue(1920);
        pickerYear.setMaxValue(currentYear);
        pickerYear.setValue(currentYear);

        pickerDay.setMinValue(1);
        updateDaysInMonth(currentYear, currentMonth);
        pickerDay.setValue(currentDay);

        pickerMonth.setOnValueChangedListener((picker, oldVal, newVal) -> {
            updateDaysInMonth(pickerYear.getValue(), newVal);
        });

        pickerYear.setOnValueChangedListener((picker, oldVal, newVal) -> {
            updateDaysInMonth(newVal, pickerMonth.getValue());
        });
    }

    private void updateDaysInMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(year, month, 1);

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        int currentDay = pickerDay.getValue();
        pickerDay.setMaxValue(maxDays);

        if (currentDay > maxDays) {
            pickerDay.setValue(maxDays);
        }
    }

    private void saveBirthdayAndProceed() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Error: No user logged in. Redirecting.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        int year = pickerYear.getValue();
        int month = pickerMonth.getValue();
        int day = pickerDay.getValue();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date birthdayDate = calendar.getTime();

        Timestamp birthdayTimestamp = new Timestamp(birthdayDate);

        String userId = currentUser.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("birthday", birthdayTimestamp);

        db.collection("users").document(userId).set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User birthday saved successfully!");
                    Toast.makeText(pick_your_birthday.this, "Birthday Saved!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, pick_your_height.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(pick_your_birthday.this, "Failed to save birthday. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
