package com.example.fitup;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class HomeFragment extends Fragment implements TrainerAdapter.OnTrainerItemClickListener {

    private static final String TAG = "HomeFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private FirebaseAuth.AuthStateListener authStateListener;
    private ListenerRegistration userListener;
    private ListenerRegistration trainersListener;

    private ImageView btnUser;
    private ImageView btnSearch;
    private ImageView btnAdd;
    private TextView tvUserName, tvUserGemCount;

    private RecyclerView recyclerTopTrainers;
    private TrainerAdapter trainerAdapter;
    private List<Trainer> trainerList;
    private Set<String> sentRequestIds = new HashSet<>();

    private LinearLayout textTodayChallenge;
    private TextView tvChallenge1, tvChallenge2, tvChallenge3;
    private List<Map<String, Object>> dailyTasks = new ArrayList<>();
    private String todayDateString;

    private ActivityResultLauncher<Intent> profileLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String id = result.getData().getStringExtra("trainerId");
                        boolean sent = result.getData().getBooleanExtra("isRequestSent", false);
                        if (sent && id != null) {
                            sentRequestIds.add(id);
                            updateSingleTrainerStatus(id);
                        }
                    }
                }
        );
    }

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
        todayDateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        btnUser = view.findViewById(R.id.btnUser);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnAdd = view.findViewById(R.id.btnAdd);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserGemCount = view.findViewById(R.id.tvUserGemCount);
        textTodayChallenge = view.findViewById(R.id.Today_challenge_box);
        tvChallenge1 = view.findViewById(R.id.tvChallenge1);
        tvChallenge2 = view.findViewById(R.id.tvChallenge2);
        tvChallenge3 = view.findViewById(R.id.tvChallenge3);
        recyclerTopTrainers = view.findViewById(R.id.recyclerTopTrainers);

        if (textTodayChallenge != null) textTodayChallenge.setVisibility(View.GONE);

        recyclerTopTrainers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        trainerList = new ArrayList<>();
        trainerAdapter = new TrainerAdapter(getContext(), trainerList, this);
        recyclerTopTrainers.setAdapter(trainerAdapter);

        btnUser.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra("targetUserId", currentUser.getUid());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();

            }
        });

        btnSearch.setOnClickListener(v -> startActivity(new Intent(getActivity(), FindUserActivity.class)));
        btnAdd.setOnClickListener(v -> startActivity(new Intent(getActivity(), PostActivity.class)));

        setupChallengeListeners();

        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                loadAndListenForUserData();
                loadSentRequestsAndThenTrainers();
                fetchDailyChallenge();
            } else {
                tvUserName.setText("Guest User");
                tvUserGemCount.setText("0 FitGem");
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth != null) {
            mAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuth != null && authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
        if (userListener != null) userListener.remove();
        if (trainersListener != null) trainersListener.remove();
    }


    private void loadAndListenForUserData() {
        if (mAuth.getCurrentUser() == null) return;

        userListener = db.collection("users").document(mAuth.getCurrentUser().getUid())
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        String name = snapshot.getString("name");
                        String avatarUrl = snapshot.getString("avatar");
                        Number gemCount = snapshot.getLong("gem");

                        tvUserName.setText((name != null && !name.isEmpty()) ? name : "User Name");
                        tvUserGemCount.setText(gemCount != null ? gemCount + " FitGem" : "0 FitGem");

                        if (avatarUrl != null && !avatarUrl.isEmpty() && getContext() != null) {
                            Glide.with(requireContext())
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.user)
                                    .error(R.drawable.user)
                                    .circleCrop()
                                    .into(btnUser);
                        }
                    }
                });
    }

    private void loadSentRequestsAndThenTrainers() {
        if (mAuth.getCurrentUser() == null) return;

        db.collection("connect_requests")
                .whereEqualTo("fromUid", mAuth.getCurrentUser().getUid())
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    if (snapshots != null) {
                        sentRequestIds.clear();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            String toUid = doc.getString("toUid");
                            if (toUid != null) sentRequestIds.add(toUid);
                        }
                        loadTopTrainers();
                    }
                });
    }

    private void loadTopTrainers() {
        trainersListener = db.collection("users")
                .whereEqualTo("role", "trainer")
                .orderBy("gem", Query.Direction.DESCENDING)
                .limit(5)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    if (snapshots != null) {
                        trainerList.clear();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Trainer trainer = doc.toObject(Trainer.class);
                            if (trainer != null) {
                                trainer.setUid(doc.getId());
                                if (sentRequestIds.contains(trainer.getUid())) {
                                    trainer.setRequestSent(true);
                                } else {
                                    trainer.setRequestSent(false);
                                }
                                if (doc.contains("avatar")) {
                                    trainer.setAvatarUrl(doc.getString("avatar"));
                                }
                                trainerList.add(trainer);
                            }
                        }
                        trainerAdapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public void onProfileClick(Trainer trainer) {
        Intent intent = new Intent(getActivity(), TrainerProfileActivity.class);
        intent.putExtra("targetUserId", trainer.getUid());
        profileLauncher.launch(intent);
    }

    @Override
    public void onConnectClick(Trainer trainer) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUid = mAuth.getCurrentUser().getUid();
        String requestId = currentUid + "_" + trainer.getUid();

        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("fromUid", currentUid);
        map.put("toUid", trainer.getUid());
        map.put("status", "pending");
        map.put("timestamp", System.currentTimeMillis());

        trainer.setRequestSent(true);
        trainerAdapter.notifyDataSetChanged();

        db.collection("connect_requests").document(requestId).set(map)
                .addOnFailureListener(e -> {
                    trainer.setRequestSent(false);
                    trainerAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateSingleTrainerStatus(String id) {
        for (int i = 0; i < trainerList.size(); i++) {
            if (trainerList.get(i).getUid().equals(id)) {
                trainerList.get(i).setRequestSent(true);
                trainerAdapter.notifyItemChanged(i);
                break;
            }
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
        if (!clickedChallenge.isEnabled()) return;

        clickedChallenge.setEnabled(false);
        DocumentReference progressDocRef = db.collection("users").document(userId)
                .collection("daily_progress").document(todayDateString);

        progressDocRef.update("task" + index + "_completed", true)
                .addOnSuccessListener(aVoid -> {
                    updateChallengeAppearance(index, true);
                    checkIfAllTasksAreComplete(progressDocRef);
                })
                .addOnFailureListener(e -> {
                    clickedChallenge.setEnabled(true);
                });
    }
    private TextView getChallengeTextViewByIndex(int index) { return (index==0)?tvChallenge1:(index==1)?tvChallenge2:tvChallenge3; }
    private void fetchDailyChallenge() {
        if (mAuth.getCurrentUser() == null) return;
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
                        dailyTasks = (List<Map<String, Object>>) documentSnapshot.get("workouts");
                        updateChallengeText();
                        fetchUserChallengeProgress();
                        if (textTodayChallenge != null) textTodayChallenge.setVisibility(View.VISIBLE);
                    }
                });
    }
    private void updateChallengeText() {
        if (dailyTasks == null || dailyTasks.size() < 3 || getContext() == null) return;
        setChallengeText(tvChallenge1, dailyTasks.get(0));
        setChallengeText(tvChallenge2, dailyTasks.get(1));
        setChallengeText(tvChallenge3, dailyTasks.get(2));
    }
    private void setChallengeText(TextView tv, Map<String, Object> task) {
        String name = (String) task.get("name");
        Long reps = (Long) task.get("reps");
        Long sets = (Long) task.get("sets");
        tv.setText(String.format(Locale.US, "%s: %d reps x %d sets", name, reps, sets));
    }
    private void fetchUserChallengeProgress() {
        if (mAuth.getCurrentUser() == null) return;
        DocumentReference progressDoc = db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("daily_progress").document(todayDateString);
        progressDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    updateAllChallengesAppearanceFromProgress(document);
                } else {
                    createUserProgressForToday(progressDoc);
                }
            }
        });
    }
    private void createUserProgressForToday(DocumentReference progressDoc) {
        progressDoc.set(Map.of("task0_completed", false, "task1_completed", false, "task2_completed", false, "all_tasks_completed", false, "gem_awarded_today", false), com.google.firebase.firestore.SetOptions.merge()).addOnSuccessListener(aVoid -> resetAllChallengesAppearance());
    }
    private void updateAllChallengesAppearanceFromProgress(DocumentSnapshot progress) {
        for (int i = 0; i < 3; i++) {
            boolean completed = Boolean.TRUE.equals(progress.getBoolean("task" + i + "_completed"));
            updateChallengeAppearance(i, completed);
        }
    }
    private void resetAllChallengesAppearance() {
        for (int i = 0; i < 3; i++) updateChallengeAppearance(i, false);
    }
    private void updateChallengeAppearance(int index, boolean completed) {
        if (getContext() == null) return;
        TextView challengeView = getChallengeTextViewByIndex(index);
        if (challengeView == null) return;
        int backgroundColor = ContextCompat.getColor(getContext(), completed ? R.color.orange : R.color.black);
        challengeView.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
        challengeView.setEnabled(!completed);
    }
    private void checkIfAllTasksAreComplete(DocumentReference progressDocRef) {
        progressDocRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) return;
            if (Boolean.TRUE.equals(snapshot.getBoolean("task0_completed")) && Boolean.TRUE.equals(snapshot.getBoolean("task1_completed")) && Boolean.TRUE.equals(snapshot.getBoolean("task2_completed"))) {
                progressDocRef.update("all_tasks_completed", true);
            }
        });
    }
}