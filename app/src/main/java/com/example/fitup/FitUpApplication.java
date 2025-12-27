package com.example.fitup;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class FitUpApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private Activity currentActivity;
    private ListenerRegistration sessionListener;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    private void startGlobalSessionListener() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.e("RepeatTesrt", "GLOBAL_LISTENER: Abort - User is null");
            return;
        }

        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("RepeatTesrt", "GLOBAL_LISTENER: Starting real-time listener for Client ID: " + myId);

        // Listen for sessions where I am the client, status is completed, and I haven't rated it yet
        sessionListener = FirebaseFirestore.getInstance().collection("sessions")
                .whereEqualTo("clientId", myId)
                .whereEqualTo("status", "completed")
                .whereEqualTo("isRated", false)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("RepeatTesrt", "GLOBAL_LISTENER: Listen Failed/Crashed", error);
                        return;
                    }

                    if (snapshots == null) {
                        Log.w("RepeatTesrt", "GLOBAL_LISTENER: Snapshot is null");
                        return;
                    }

                    Log.d("RepeatTesrt", "GLOBAL_LISTENER: Snapshot received. Total Documents: " + snapshots.size());
                    Log.d("RepeatTesrt", "GLOBAL_LISTENER: Number of changes: " + snapshots.getDocumentChanges().size());

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        String sessionId = dc.getDocument().getId();
                        String type = dc.getType().toString();
                        Log.d("RepeatTesrt", "GLOBAL_LISTENER: Change Detected -> Type: " + type + " | SessionID: " + sessionId);

                        if (dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                            String trainerId = dc.getDocument().getString("trainerId");
                            Log.d("RepeatTesrt", "GLOBAL_LISTENER: Triggering Dialog for Session: " + sessionId);

                            showRatingDialog(sessionId, trainerId);
                        } else {
                            Log.d("RepeatTesrt", "GLOBAL_LISTENER: Change Type " + type + " ignored.");
                        }
                    }
                });
    }

    private void showRatingDialog(String sessionId, String trainerId) {
        // We need a valid FragmentActivity to show a BottomSheetDialogFragment
        if (currentActivity instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) currentActivity;

            // Check if it's already showing to prevent duplicates
            if (activity.getSupportFragmentManager().findFragmentByTag("RatingDialog") == null) {
                Log.d("RepeatTesrt", "DIALOG: Showing RatingDialog now.");
                RatingSessionFragment fragment = RatingSessionFragment.newInstance(sessionId, trainerId);

                // Extra safety: only show if activity is not finishing
                if (!activity.isFinishing() && !activity.isDestroyed()) {
                    fragment.show(activity.getSupportFragmentManager(), "RatingDialog");
                } else {
                    Log.w("RepeatTesrt", "DIALOG: Activity is finishing/destroyed, cannot show dialog.");
                }
            } else {
                Log.d("RepeatTesrt", "DIALOG: RatingDialog is already visible. Skipping.");
            }
        } else {
            Log.e("RepeatTesrt", "DIALOG: Current Activity is NOT a FragmentActivity. Cannot show dialog. Current: " + (currentActivity != null ? currentActivity.getClass().getSimpleName() : "null"));
        }
    }

    // --- Lifecycle Callbacks to track current Activity ---

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;

        // 1. FILTER: Ignore onboarding/auth screens
        // If we are on Splash or Introduction, we definitely don't want to listen or show dialogs yet.
        if (activity instanceof SplashActivity || activity instanceof IntroductionPage) {
            return;
        }

        // 2. CHECK AUTH STATE
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            // User is logged in AND passed the intro screens.
            Log.d("RepeatTesrt", "HAJIME GOT USER");

            // Ensure the global listener is running for real-time updates
            if (sessionListener == null) {
                Log.d("RepeatTesrt", "HAJIME GOT USER 2");
                startGlobalSessionListener();
            }

            // FORCE CHECK: If we are landing on MainView (the dashboard),
            // query ONCE to catch anything that happened while app was closed.
            if (activity instanceof MainView) {
                Log.d("RepeatTesrt", "HAJIME GOT USER 3");
                checkForUnratedSessions();
            }

        } else {
            Log.d("RepeatTesrt", "HAJIME DONT GOT USER");

            // User is NOT logged in.
            // If we have a lingering listener (e.g., user just logged out), kill it.
            if (sessionListener != null) {
                sessionListener.remove();
                sessionListener = null;
            }
        }
    }

    // Add this new helper method to query one-time existing completed sessions
    private void checkForUnratedSessions() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("RepeatTesrt", "HAJIME: Starting check for unrated sessions for ID: " + myId);

        FirebaseFirestore.getInstance().collection("sessions")
                .whereEqualTo("clientId", myId)
                .whereEqualTo("status", "completed")
                .whereEqualTo("isRated", false)
                .limit(1) // Just get one at a time to not spam popups
                .get()
                .addOnSuccessListener(snapshots -> {
                    Log.d("RepeatTesrt", "HAJIME: Query success. Documents count: " + snapshots.size());

                    if (!snapshots.isEmpty()) {
                        DocumentSnapshot doc = snapshots.getDocuments().get(0);
                        String sessionId = doc.getId();
                        String trainerId = doc.getString("trainerId");

                        Log.d("RepeatTesrt", "HAJIME: Found unrated session ID: " + sessionId);

                        // Show the dialog
                        showRatingDialog(sessionId, trainerId);
                    } else {
                        Log.d("RepeatTesrt", "HAJIME: No unrated sessions found in query.");
                    }
                })
                .addOnFailureListener(e -> Log.e("RepeatTesrt", "HAJIME: Query Failed Error: ", e));
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        // Clear reference to avoid memory leaks
        if (currentActivity == activity) {
            currentActivity = null;
        }
    }

    // Boilerplate for other methods
    @Override public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}
    @Override public void onActivityStarted(@NonNull Activity activity) {}
    @Override public void onActivityStopped(@NonNull Activity activity) {}
    @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
    @Override public void onActivityDestroyed(@NonNull Activity activity) {}
}
