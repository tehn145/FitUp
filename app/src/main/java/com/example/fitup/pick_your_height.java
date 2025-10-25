package com.example.fitup;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class pick_your_height extends AppCompatActivity {
    private NumberPicker pickerHeight;
    private MaterialButton btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pick_your_height);

        pickerHeight = findViewById(R.id.pickerHeight);
        btnContinue = findViewById(R.id.btnContinue);

        initHeightPicker();

        btnContinue.setOnClickListener(v -> {
            int height = pickerHeight.getValue();
            Toast.makeText(pick_your_height.this,
                    "Your height: " + height + " cm", Toast.LENGTH_SHORT).show();
        });
    }

    private void initHeightPicker() {
        pickerHeight.setMinValue(100);
        pickerHeight.setMaxValue(250);
        pickerHeight.setValue(170);
        pickerHeight.setWrapSelectorWheel(false);
    }

}
