package com.example.fitup;

import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class scroll_weight extends AppCompatActivity {
    NumberPicker weightPicker;
    MaterialButton continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scroll_weight);

        weightPicker = findViewById(R.id.pickerHeight);
        continueButton = findViewById(R.id.btnContinue);

        setupWeightPicker();

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedWeight = weightPicker.getValue();

                Toast.makeText(scroll_weight.this, "Cân nặng đã chọn: " + selectedWeight + " KG", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupWeightPicker() {
        weightPicker.setMinValue(20);
        weightPicker.setMaxValue(200);
        weightPicker.setValue(60);
        weightPicker.setWrapSelectorWheel(true);
    }
}