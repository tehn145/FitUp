package com.example.fitup;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random; // Import Random

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;
    private ListenerRegistration trainersListener; // Listener for top trainers

    private ImageButton btnUser;
    private TextView tvUserName, tvUserGemCount;

    private RecyclerView recyclerTopTrainers;
    private TrainerAdapter trainerAdapter;
    private List<Trainer> trainerList;

    private LinearLayout textTodayChallenge; // Variable for the title
    private TextView tvChallenge1, tvChallenge2, tvChallenge3;
    private List<Map<String, Object>> dailyTasks = new ArrayList<>();
    private String todayDateString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnUser = view.findViewById(R.id.btnUser);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserGemCount = view.findViewById(R.id.tvUserGemCount);

        textTodayChallenge = view.findViewById(R.id.Today_challenge_box);
        textTodayChallenge.setVisibility(View.GONE);

        tvChallenge1 = view.findViewById(R.id.tvChallenge1);
        tvChallenge2 = view.findViewById(R.id.tvChallenge2);
        tvChallenge3 = view.findViewById(R.id.tvChallenge3);

        recyclerTopTrainers = view.findViewById(R.id.recyclerTopTrainers);
        recyclerTopTrainers.setLayoutManager(new LinearLayoutManager(getContext()));
        trainerList = new ArrayList<>();
        trainerAdapter = new TrainerAdapter(getContext(), trainerList);
        recyclerTopTrainers.setAdapter(trainerAdapter);

        todayDateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        setupChallengeListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadAndListenForUserData();
        listenForTopTrainers(); // Changed from fetch to listen
        fetchDailyChallenge();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userListener != null) {
            userListener.remove();
        }
        // Detach the top trainers listener to prevent memory leaks
        if (trainersListener != null) {
            trainersListener.remove();
        }
    }

    private void setupChallengeListeners() {
        tvChallenge1.setOnClickListener(v -> handleChallengeClick(0));
        tvChallenge2.setOnClickListener(v -> handleChallengeClick(1));
        tvChallenge3.setOnClickListener(v -> handleChallengeClick(2));
    }

    private void handleChallengeClick(int index) {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        TextView clickedChallenge = getChallengeTextViewByIndex(index);
        if (!clickedChallenge.isEnabled()) {
            return;
        }
        clickedChallenge.setEnabled(false);

        DocumentReference progressDocRef = db.collection("users").document(userId)
                .collection("daily_progress").document(todayDateString);

        progressDocRef.update("task" + index + "_completed", true)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully marked task " + index + " as complete.");
                    updateChallengeAppearance(index, true);
                    checkIfAllTasksAreComplete(progressDocRef);
                })
                .addOnFailureListener(e -> {
                    clickedChallenge.setEnabled(true);
                    Log.w(TAG, "Failed to mark task as complete", e);
                    Toast.makeText(getContext(), "Failed to save progress. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkIfAllTasksAreComplete(DocumentReference progressDocRef) {
        progressDocRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) return;

            boolean task0 = Boolean.TRUE.equals(snapshot.getBoolean("task0_completed"));
            boolean task1 = Boolean.TRUE.equals(snapshot.getBoolean("task1_completed"));
            boolean task2 = Boolean.TRUE.equals(snapshot.getBoolean("task2_completed"));

            if (task0 && task1 && task2) {
                progressDocRef.update("all_tasks_completed", true)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "All tasks complete. Flag set for server verification."))
                        .addOnFailureListener(e -> Log.w(TAG, "Failed to set all_tasks_completed flag.", e));
            }
        });
    }

    private void fetchDailyChallenge() {
        DocumentReference progressDocRef = db.collection("users").document(mAuth.getUid())
                .collection("daily_progress").document(todayDateString);

        progressDocRef.get().addOnSuccessListener(progressSnapshot -> {
            if (progressSnapshot.exists() && progressSnapshot.contains("challenge_name")) {
                String challengeId = progressSnapshot.getString("challenge_name");
                fetchChallengeById(challengeId);
            } else {
                int randomChallengeNum = new Random().nextInt(3) + 1;
                String challengeId = String.format(Locale.US, "challenge%02d", randomChallengeNum);
                progressDocRef.set(Map.of("challenge_name", challengeId), com.google.firebase.firestore.SetOptions.merge());
                fetchChallengeById(challengeId);
            }
        });
    }

    private void fetchChallengeById(String challengeId) {
        db.collection("challenges").document(challengeId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Log.d(TAG, "Successfully fetched challenge: " + challengeId);

                        // Make title visible now that data has loaded successfully
                        dailyTasks = (List<Map<String, Object>>) documentSnapshot.get("workouts");
                        updateChallengeText();
                        fetchUserChallengeProgress();

                        if (textTodayChallenge != null) {
                            textTodayChallenge.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Log.w(TAG, "Could not find challenge document: " + challengeId);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching challenge: " + challengeId, e));
    }


    private void updateChallengeText() {
        if (dailyTasks == null || dailyTasks.size() < 3 || getContext() == null) {
            Log.w(TAG, "Not enough tasks in challenge to display.");
            return;
        }

        Map<String, Object> task1 = dailyTasks.get(0);
        String name1 = (String) task1.get("name");
        Long reps1 = (Long) task1.get("reps");
        Long sets1 = (Long) task1.get("sets");
        tvChallenge1.setText(String.format(Locale.US, "%s: %d reps x %d sets", name1, reps1, sets1));

        Map<String, Object> task2 = dailyTasks.get(1);
        String name2 = (String) task2.get("name");
        Long reps2 = (Long) task2.get("reps");
        Long sets2 = (Long) task2.get("sets");
        tvChallenge2.setText(String.format(Locale.US, "%s: %d reps x %d sets", name2, reps2, sets2));

        Map<String, Object> task3 = dailyTasks.get(2);
        String name3 = (String) task3.get("name");
        Long reps3 = (Long) task3.get("reps");
        Long sets3 = (Long) task3.get("sets");
        tvChallenge3.setText(String.format(Locale.US, "%s: %d reps x %d sets", name3, reps3, sets3));
    }

    private void fetchUserChallengeProgress() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        DocumentReference progressDoc = db.collection("users").document(userId)
                .collection("daily_progress").document(todayDateString);

        progressDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    updateAllChallengesAppearanceFromProgress(document);
                } else {
                    createUserProgressForToday(progressDoc);
                }
            } else {
                Log.d(TAG, "Failed to get daily progress: ", task.getException());
            }
        });
    }

    private void createUserProgressForToday(DocumentReference progressDoc) {
        progressDoc.set(Map.of(
                "task0_completed", false,
                "task1_completed", false,
                "task2_completed", false,
                "all_tasks_completed", false,
                "gem_awarded_today", false
        ), com.google.firebase.firestore.SetOptions.merge()).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Created daily progress doc for user.");
            resetAllChallengesAppearance();
        });
    }

    private void updateAllChallengesAppearanceFromProgress(DocumentSnapshot progress) {
        for (int i = 0; i < 3; i++) {
            boolean completed = Boolean.TRUE.equals(progress.getBoolean("task" + i + "_completed"));
            updateChallengeAppearance(i, completed);
        }
    }

    private void resetAllChallengesAppearance() {
        for (int i = 0; i < 3; i++) {
            updateChallengeAppearance(i, false);
        }
    }

    private void updateChallengeAppearance(int index, boolean completed) {
        if (getContext() == null) return;

        TextView challengeView = getChallengeTextViewByIndex(index);
        if (challengeView == null) return;

        int backgroundColor = ContextCompat.getColor(getContext(), completed ? R.color.orange : R.color.black);
        challengeView.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
        challengeView.setEnabled(!completed);
    }

    private TextView getChallengeTextViewByIndex(int index) {
        if (index == 0) return tvChallenge1;
        if (index == 1) return tvChallenge2;
        return tvChallenge3;
    }

    private void listenForTopTrainers() {
        Query query = db.collection("users")
                .whereEqualTo("role", "trainer")
                .orderBy("gem", Query.Direction.DESCENDING)
                .limit(3);

        trainersListener = query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed for top trainers.", e);
                Toast.makeText(getContext(), "Failed to load top trainers.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
                Log.d(TAG, "Got updated documents for trainers!");
                trainerList.clear();
                for (DocumentSnapshot document : snapshots.getDocuments()) {
                    Trainer trainer = document.toObject(Trainer.class);
                    if (trainer != null) {
                        trainerList.add(trainer);
                    }
                }
                trainerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadAndListenForUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "No user logged in.");
            return;
        }

        String userId = currentUser.getUid();
        userListener = db.collection("users").document(userId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        updateUI(snapshot);
                    }
                });
    }

    private void updateUI(DocumentSnapshot snapshot) {
        String name = snapshot.getString("name");
        String avatarUrl = snapshot.getString("avatarUrl");
        Number gemCount = snapshot.getLong("gem");

        if (name != null && !name.isEmpty()) {
            tvUserName.setText(name);
        } else {
            tvUserName.setText("User Name");
        }

        if (gemCount != null) {
            tvUserGemCount.setText(String.format(Locale.getDefault(), "%d FitGem", gemCount.longValue()));
        } else {
            tvUserGemCount.setText("0 FitGem");
        }

        if (avatarUrl != null && !avatarUrl.isEmpty() && getContext() != null) {
            Glide.with(getContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .circleCrop()
                    .into(btnUser);
        }
    }
}
