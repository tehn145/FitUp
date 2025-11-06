package com.example.fitup;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.List;

public class Primary_goal extends AppCompatActivity {
    MaterialCardView cardWeightLoss, cardMuscleBuilding, cardEndurance, cardKeepFit, cardOverallHealth;
    Button buttonContinue;
    List<MaterialCardView> cardList;

    private int defaultStrokeColor;
    private int selectedStrokeColor;
    private int defaultBackgroundColor;
    private int selectedBackgroundColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_primary_goal);

        buttonContinue = findViewById(R.id.button_continue);
        cardWeightLoss = findViewById(R.id.card_weight_loss);
        cardMuscleBuilding = findViewById(R.id.card_muscle_building);
        cardEndurance = findViewById(R.id.card_improving_endurance);
        cardKeepFit = findViewById(R.id.card_keep_fit);
        cardOverallHealth = findViewById(R.id.card_overall_health);

        cardList = Arrays.asList(
                cardWeightLoss,
                cardMuscleBuilding,
                cardEndurance,
                cardKeepFit,
                cardOverallHealth
        );

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

        for (MaterialCardView card : cardList) {
            card.setOnClickListener(cardClickListener);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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