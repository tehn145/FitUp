package com.example.fitup;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat; // Thêm import này
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.List;

public class Fitness_Level extends AppCompatActivity {
    MaterialCardView cardBeginner, cardIntermediate, cardAdvanced;
    Button buttonContinue;
    List<MaterialCardView> cardList;

    // Định nghĩa màu sắc
    private int defaultStrokeColor;
    private int selectedStrokeColor;
    private int defaultBackgroundColor;
    private int selectedBackgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fitness_level);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardBeginner = findViewById(R.id.card_beginner);
        cardIntermediate = findViewById(R.id.card_intermediate);
        cardAdvanced = findViewById(R.id.card_advanced);
        buttonContinue = findViewById(R.id.button_continue);

        cardList = Arrays.asList(cardBeginner, cardIntermediate, cardAdvanced);

        defaultStrokeColor = Color.parseColor("#333333");
        selectedStrokeColor = Color.WHITE;

        defaultBackgroundColor = ContextCompat.getColor(this, R.color.backgroungbtncontinue);
        selectedBackgroundColor = Color.LTGRAY;

        buttonContinue.setEnabled(false);

        View.OnClickListener cardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCard((MaterialCardView) v);
            }
        };

        cardBeginner.setOnClickListener(cardClickListener);
        cardIntermediate.setOnClickListener(cardClickListener);
        cardAdvanced.setOnClickListener(cardClickListener);
    }

    private void selectCard(MaterialCardView selectedCard) {
        for (MaterialCardView card : cardList) {
            if (card == selectedCard) {
                card.setStrokeColor(selectedStrokeColor);
                card.setCardBackgroundColor(selectedBackgroundColor);
            } else {
                card.setStrokeColor(defaultStrokeColor);
                card.setCardBackgroundColor(defaultBackgroundColor);
            }
        }

        buttonContinue.setEnabled(true);
    }
}