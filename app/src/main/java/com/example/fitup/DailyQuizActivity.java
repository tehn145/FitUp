package com.example.fitup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DailyQuizActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvCategory, tvQuestion;
    private LinearLayout layoutOptions;
    private FrameLayout btnNext;
    private FrameLayout btnPrev;
    private AppCompatButton btnComplete;
    private ImageView btnClose;

    private List<QuizQuestion> questionList;
    private int currentQuestionIndex = 0;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_quiz);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupQuestions();
        loadQuestion(0);

        btnClose.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        });

        btnPrev.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                loadQuestion(currentQuestionIndex);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (questionList.get(currentQuestionIndex).getSelectedOptionIndex() == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                loadQuestion(currentQuestionIndex);
            }
        });

        btnComplete.setOnClickListener(v -> saveResultsToFirebase());
    }

    private void initViews() {
        progressBar = findViewById(R.id.quizProgressBar);
        tvCategory = findViewById(R.id.tvCategory);
        tvQuestion = findViewById(R.id.tvQuestion);
        layoutOptions = findViewById(R.id.layoutOptions);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnComplete = findViewById(R.id.btnComplete);
        btnClose = findViewById(R.id.btnClose);
    }

    private void setupQuestions() {
        questionList = new ArrayList<>();
        questionList.add(new QuizQuestion("q_water", "Recovery", "Did you drink enough water yesterday?",
                Arrays.asList("Almost forgot", "Not enough", "Enough")));
        questionList.add(new QuizQuestion("q_sleep_hours", "Sleep", "How many hours did you sleep last night?",
                Arrays.asList("Less than 5 hours", "Around 6-7 hours", "More than 8 hours")));
        questionList.add(new QuizQuestion("q_sleep_quality", "Sleep", "Did you feel that your sleep last night was good quality?",
                Arrays.asList("Poor", "Average", "Good")));
        questionList.add(new QuizQuestion("q_training", "Training", "How was your workout yesterday?",
                Arrays.asList("Did not train", "Off plan", "Good")));
        questionList.add(new QuizQuestion("q_energy", "Training", "How is your energy today?",
                Arrays.asList("Unmotivated", "Stable", "Excited")));
    }

    private void loadQuestion(int index) {
        QuizQuestion q = questionList.get(index);

        tvCategory.setText(q.getCategory());
        int iconResId = 0;
        switch (q.getCategory()) {
            case "Sleep":
                iconResId = R.drawable.ic_sleep;
                break;
            case "Recovery":
                iconResId = R.drawable.ic_water;
                break;
            case "Training":
                iconResId = R.drawable.ic_fitness;
                break;
            default:
                iconResId = R.drawable.ic_fitupgem;
                break;
        }
        tvCategory.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        tvQuestion.setText(q.getQuestionText());

        int progress = (int) (((float) (index + 1) / questionList.size()) * 100);
        progressBar.setProgress(progress);

        if (index == 0) {
            btnPrev.setVisibility(View.GONE);
        } else {
            btnPrev.setVisibility(View.VISIBLE);
        }

        if (index == questionList.size() - 1) {
            btnNext.setVisibility(View.GONE);
            btnComplete.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnComplete.setVisibility(View.GONE);
        }

        layoutOptions.removeAllViews();
        for (int i = 0; i < q.getOptions().size(); i++) {
            String optionText = q.getOptions().get(i);

            TextView optionView = new TextView(this);
            optionView.setText(optionText);
            optionView.setTextColor(Color.WHITE);
            optionView.setTextSize(18);
            optionView.setTypeface(null, Typeface.BOLD);
            optionView.setPadding(48, 60, 48, 60);
            optionView.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 32);
            optionView.setLayoutParams(params);

            if (i == q.getSelectedOptionIndex()) {
                optionView.setBackgroundResource(R.drawable.bg_quiz_option_selected);
            } else {
                optionView.setBackgroundResource(R.drawable.bg_quiz_option_default);
            }

            int finalIndex = i;
            optionView.setOnClickListener(v -> {
                q.setSelectedOptionIndex(finalIndex);
                loadQuestion(index);
            });

            layoutOptions.addView(optionView);
        }
    }

    private void saveResultsToFirebase() {
        if (mAuth.getCurrentUser() == null) return;

        if (questionList.get(currentQuestionIndex).getSelectedOptionIndex() == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Map<String, Object> quizData = new HashMap<>();
        for (QuizQuestion q : questionList) {
            quizData.put(q.getId(), q.getOptions().get(q.getSelectedOptionIndex()));
        }
        quizData.put("timestamp", System.currentTimeMillis());
        quizData.put("date", today);

        DocumentReference userRef = db.collection("users").document(uid);
        DocumentReference checkinRef = userRef.collection("daily_checkins").document(today);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot userSnapshot = transaction.get(userRef);

            long currentStreak = 0;
            String lastCheckin = "";

            if (userSnapshot.contains("current_streak")) {
                currentStreak = userSnapshot.getLong("current_streak");
            }
            if (userSnapshot.contains("last_checkin_date")) {
                lastCheckin = userSnapshot.getString("last_checkin_date");
            }

            String yesterday = getYesterdayDateString();

            if (!today.equals(lastCheckin)) {
                if (yesterday.equals(lastCheckin)) {
                    currentStreak++;
                } else {
                    currentStreak = 1;
                }
            }

            transaction.update(userRef, "current_streak", currentStreak);
            transaction.update(userRef, "last_checkin_date", today);

            transaction.set(checkinRef, quizData);

            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Daily Check-in Complete!", Toast.LENGTH_SHORT).show();

            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }).addOnFailureListener(e -> {
            userRef.update("current_streak", 1);
            Toast.makeText(this, "Saved (First time)", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private String getYesterdayDateString() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
    }
}