package com.example.fitup;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class pick_your_birthday extends AppCompatActivity {
    private NumberPicker pickerMonth;
    private NumberPicker pickerDay;
    private NumberPicker pickerYear;
    private MaterialButton btnContinue;

    private final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_pick_your_birthday);

        pickerMonth = findViewById(R.id.pickerMonth);
        pickerDay = findViewById(R.id.pickerDay);
        pickerYear = findViewById(R.id.pickerYear);
        btnContinue = findViewById(R.id.buttonContinue);

        initPickers();
        setupListeners();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int monthIndex = pickerMonth.getValue();
                String monthName = MONTHS[monthIndex];
                int day = pickerDay.getValue();
                int year = pickerYear.getValue();

                String selectedDate = "Bạn chọn: " + monthName + " " + day + ", " + year;
                Toast.makeText(pick_your_birthday.this, selectedDate, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void initPickers() {
        Calendar today = Calendar.getInstance();
        int currentDay = today.get(Calendar.DAY_OF_MONTH);
        int currentMonth = today.get(Calendar.MONTH); // 0 - 11
        int currentYear = today.get(Calendar.YEAR);

        pickerMonth.setMinValue(0);
        pickerMonth.setMaxValue(11);
        pickerMonth.setDisplayedValues(MONTHS);
        pickerMonth.setWrapSelectorWheel(true);
        pickerMonth.setValue(currentMonth);

        pickerYear.setMinValue(1920);
        pickerYear.setMaxValue(currentYear);
        pickerYear.setWrapSelectorWheel(false);
        pickerYear.setValue(currentYear);

        updateDays();
        pickerDay.setValue(currentDay);
    }

    private void setupListeners() {
        //Cập nhật ngày trong tháng khi đổi tháng
        pickerMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateDays();
            }
        });

        //Cập nhật khi đổi năm gọi hàm updateDays
        pickerYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateDays();
            }
        });
    }

    private void updateDays() {
        int month = pickerMonth.getValue();
        int year = pickerYear.getValue();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currentDay = pickerDay.getValue();

        pickerDay.setMinValue(1);
        pickerDay.setMaxValue(maxDays);
        pickerDay.setWrapSelectorWheel(true);

        if (currentDay > maxDays) {
            pickerDay.setValue(maxDays);
        }
    }
}

