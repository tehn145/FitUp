package com.example.fitup;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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
    private ListenerRegistration requestsListener;
    private ListenerRegistration incomingRequestsListener;
    private ListenerRegistration incomingPendingListener;

    private ImageView btnUser;
    private ImageView btnSearch;
    private ImageView btnAdd;
    private TextView tvUserName, tvUserGemCount;

    private ViewPager2 bannerViewPager;
    private TabLayout bannerTabLayout;
    private BannerAdapter bannerAdapter;


    private RecyclerView recyclerTopTrainers;
    private TrainerAdapter trainerAdapter;
    private List<Trainer> trainerList;

    private Set<String> sentRequestIds = new HashSet<>();
    private Set<String> connectedIds = new HashSet<>();
    private Set<String> incomingPendingIds = new HashSet<>();

    private LinearLayout textTodayChallenge;
    private TextView tvChallenge1, tvChallenge2, tvChallenge3;
    private List<Map<String, Object>> dailyTasks = new ArrayList<>();
    private String todayDateString;

    private ActivityResultLauncher<Intent> profileLauncher;
    private LinearLayout btnExerciseLibrary;
    private ImageView btnAssistant;
    private TextView tvMascotBubble;
    private Handler mascotHandler = new Handler(Looper.getMainLooper());
    private Runnable mascotRunnable;
//    private ObjectAnimator mascotAnimator;
    private android.animation.Animator mascotAnimator;
    private View mascotContainer;
    private String[] mascotMessages = {
            "Hi! I'm Fitty. Need help?",
            "Don't forget to hydrate! 💧",
            "Ready for a workout? 💪",
            "Check today's challenge! 🔥",
            "Keep pushing! You got this!",
            "I'm here to assist you! 🤖"
    };
    private View cardInvite;

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

        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        bannerTabLayout = view.findViewById(R.id.bannerTabLayout);

        List<Integer> bannerList = new ArrayList<>();
        bannerList.add(R.drawable.banner_fitness1);
        bannerList.add(R.drawable.banner_fitness2);
        bannerList.add(R.drawable.banner_fitness3);

        bannerAdapter = new BannerAdapter(bannerList);
        bannerViewPager.setAdapter(bannerAdapter);

        new TabLayoutMediator(bannerTabLayout, bannerViewPager, (tab, position) -> {
            View tabView = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab_dot2, null);
            tab.setCustomView(tabView);
        }).attach();

        bannerTabLayout.post(() -> {
            ViewGroup tabs = (ViewGroup) bannerTabLayout.getChildAt(0);
            int marginInDp = 5;
            float scale = getResources().getDisplayMetrics().density;
            int marginInPx = (int) (marginInDp * scale + 0.5f);

            for (int i = 0; i < tabs.getChildCount(); i++) {
                View tab = tabs.getChildAt(i);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                params.setMargins(marginInPx, 0, marginInPx, 0);
                tab.requestLayout();
            }
        });

        recyclerTopTrainers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        trainerList = new ArrayList<>();
        trainerAdapter = new TrainerAdapter(getContext(), trainerList, this);
        recyclerTopTrainers.setAdapter(trainerAdapter);

        btnAssistant = view.findViewById(R.id.btn_virtual_assistant);
        btnAssistant = view.findViewById(R.id.btn_virtual_assistant);
        tvMascotBubble = view.findViewById(R.id.tv_mascot_bubble);
        mascotContainer = view.findViewById(R.id.layout_mascot_container);
        cardInvite = view.findViewById(R.id.card_invite);

        if (cardInvite != null) {
            cardInvite.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), InviteFriendsActivity.class);
                startActivity(intent);
            });
        }

        btnAssistant.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;
            float startX, startY;
            private static final int CLICK_ACTION_THRESHOLD = 10;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();

                        if (mascotContainer != null) {
                            dX = mascotContainer.getX() - startX;
                            dY = mascotContainer.getY() - startY;
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (mascotContainer != null) {
                            mascotContainer.animate()
                                    .x(event.getRawX() + dX)
                                    .y(event.getRawY() + dY)
                                    .setDuration(0)
                                    .start();
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        // Xử lý click (nếu ngón tay không di chuyển quá nhiều)
                        if (Math.abs(endX - startX) < CLICK_ACTION_THRESHOLD &&
                                Math.abs(endY - startY) < CLICK_ACTION_THRESHOLD) {
                            v.performClick();
                        }
                        return true;
                }
                return false;
            }
        });

        btnAssistant.setOnClickListener(v -> startActivity(new Intent(v.getContext(), AssistantChatActivity.class)));
        startMascotAnimation();
        startMascotTalkLoop();

        btnUser.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                db.collection("users").document(currentUser.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String role = documentSnapshot.getString("role");
                                Intent intent;
                                if ("trainer".equalsIgnoreCase(role)) {
                                    intent = new Intent(getActivity(), TrainerProfileActivity.class);
                                } else {
                                    intent = new Intent(getActivity(), UserProfileActivity.class);
                                }
                                intent.putExtra("targetUserId", currentUser.getUid());
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching profile", Toast.LENGTH_SHORT).show());
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
                loadIncomingConnections();
                loadIncomingPendingRequests();
                loadSentRequestsAndThenTrainers();
                fetchDailyChallenge();
            } else {
                tvUserName.setText("Guest User");
                tvUserGemCount.setText("0 FitGem");
            }
        };

        btnExerciseLibrary = view.findViewById(R.id.btn_exercise_library);
        btnExerciseLibrary.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ExerciseCategoryActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
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
        if (requestsListener != null) requestsListener.remove();
        if (incomingRequestsListener != null) incomingRequestsListener.remove();
        if (incomingPendingListener != null) incomingPendingListener.remove();
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

    private void loadIncomingConnections() {
        if (mAuth.getCurrentUser() == null) return;
        incomingRequestsListener = db.collection("connect_requests")
                .whereEqualTo("toUid", mAuth.getCurrentUser().getUid())
                .whereEqualTo("status", "accepted")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        String fromUid = doc.getString("fromUid");
                        if (fromUid != null) {
                            connectedIds.add(fromUid);
                        }
                    }
                    if (trainerList != null && !trainerList.isEmpty()) {
                        for (Trainer trainer : trainerList) {
                            if (connectedIds.contains(trainer.getUid())) {
                                trainer.setConnected(true);
                            }
                        }
                        trainerAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadIncomingPendingRequests() {
        if (mAuth.getCurrentUser() == null) return;
        incomingPendingListener = db.collection("connect_requests")
                .whereEqualTo("toUid", mAuth.getCurrentUser().getUid())
                .whereEqualTo("status", "pending")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    incomingPendingIds.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        String fromUid = doc.getString("fromUid");
                        if (fromUid != null) {
                            incomingPendingIds.add(fromUid);
                        }
                    }

                    if (trainerList != null && !trainerList.isEmpty()) {
                        for (Trainer trainer : trainerList) {
                            if (incomingPendingIds.contains(trainer.getUid())) {
                                trainer.setIncomingRequest(true);
                            } else {
                                trainer.setIncomingRequest(false);
                            }
                        }
                        trainerAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadSentRequestsAndThenTrainers() {
        if (mAuth.getCurrentUser() == null) return;

        requestsListener = db.collection("connect_requests")
                .whereEqualTo("fromUid", mAuth.getCurrentUser().getUid())
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    if (snapshots != null) {
                        sentRequestIds.clear();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            String toUid = doc.getString("toUid");
                            String status = doc.getString("status");

                            if (toUid != null) {
                                if ("accepted".equals(status)) {
                                    connectedIds.add(toUid);
                                } else {
                                    sentRequestIds.add(toUid);
                                }
                            }
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

                                if (connectedIds.contains(trainer.getUid())) {
                                    trainer.setConnected(true);
                                } else {
                                    trainer.setConnected(false);
                                }

                                if (sentRequestIds.contains(trainer.getUid())) {
                                    trainer.setRequestSent(true);
                                } else {
                                    trainer.setRequestSent(false);
                                }

                                if (incomingPendingIds.contains(trainer.getUid())) {
                                    trainer.setIncomingRequest(true);
                                } else {
                                    trainer.setIncomingRequest(false);
                                }

                                if (doc.contains("avatar")) {
                                    trainer.setAvatarUrl(doc.getString("avatar"));
                                }

                                String fitnessLevel = doc.getString("fitnessLevel");
                                trainer.setFitnessLevel(fitnessLevel);

                                String locationName = doc.getString("locationName");
                                trainer.setLocationName(locationName != null && !locationName.isEmpty() ? locationName : "Unspecified Location");

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

        if (trainer.isIncomingRequest()) {
            Intent intent = new Intent(getContext(), ConnectionsActivity.class);
            startActivity(intent);
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

    @Override
    public void onMessageClick(Trainer trainer) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("RECEIVER_ID", trainer.getUid());
        intent.putExtra("RECEIVER_NAME", trainer.getName());
        startActivity(intent);
    }

    private void startMascotAnimation() {
        ObjectAnimator floatAnim = ObjectAnimator.ofFloat(btnAssistant, "translationY", 0f, -15f, 0f);
        floatAnim.setDuration(2500);
        floatAnim.setRepeatCount(ValueAnimator.INFINITE);
        floatAnim.setRepeatMode(ValueAnimator.RESTART);
        floatAnim.setInterpolator(new android.view.animation.OvershootInterpolator(0.8f));

        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.05f, 1f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.05f, 1f);

        ObjectAnimator breathAnim = ObjectAnimator.ofPropertyValuesHolder(btnAssistant, pvhScaleX, pvhScaleY);
        breathAnim.setDuration(2500);
        breathAnim.setRepeatCount(ValueAnimator.INFINITE);
        breathAnim.setRepeatMode(ValueAnimator.RESTART);
        breathAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(floatAnim, breathAnim);

        mascotAnimator = animatorSet;
        mascotAnimator.start();
    }

    private void startMascotTalkLoop() {
        mascotRunnable = new Runnable() {
            @Override
            public void run() {
                int randomIndex = new Random().nextInt(mascotMessages.length);
                String randomMessage = mascotMessages[randomIndex];

                if (tvMascotBubble != null) {
                    tvMascotBubble.setText(randomMessage);

                    tvMascotBubble.setVisibility(View.VISIBLE);
                    tvMascotBubble.setAlpha(0f);
                    tvMascotBubble.animate().alpha(1f).setDuration(500).start();
                }

                mascotHandler.postDelayed(() -> {
                    if (tvMascotBubble != null) {
                        tvMascotBubble.animate().alpha(0f).setDuration(500).withEndAction(() ->
                                tvMascotBubble.setVisibility(View.GONE)
                        ).start();
                    }
                }, 4000);

                mascotHandler.postDelayed(this, 12000);
            }
        };
        mascotHandler.postDelayed(mascotRunnable, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mascotHandler != null && mascotRunnable != null) {
            mascotHandler.removeCallbacks(mascotRunnable);
        }
        if (mascotAnimator != null) {
            mascotAnimator.cancel();
        }
    }
}