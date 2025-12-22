package com.example.fitup;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    private static final String TAG = "FirestoreHelper";

    public interface OnFollowCheckListener {
        void onCheck(boolean isFollowing);
    }

    public interface OnActionCompleteListener {
        void onComplete(boolean success);
    }

    public static void checkIsFollowing(String targetUserId, OnFollowCheckListener listener) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserId == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUserId)
                .collection("following")
                .document(targetUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    listener.onCheck(documentSnapshot.exists());
                })
                .addOnFailureListener(e -> listener.onCheck(false));
    }

    // Toggle Follow/Unfollow
    public static void toggleFollow(String targetUserId, boolean isCurrentlyFollowing, OnActionCompleteListener listener) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserId == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference currentUserRef = db.collection("users").document(currentUserId);
        DocumentReference targetUserRef = db.collection("users").document(targetUserId);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentReference followingRef = currentUserRef.collection("following").document(targetUserId);
            DocumentReference followersRef = targetUserRef.collection("followers").document(currentUserId);

            if (isCurrentlyFollowing) {
                transaction.delete(followingRef);
                transaction.delete(followersRef);

                transaction.update(currentUserRef, "followingCount", FieldValue.increment(-1));
                transaction.update(targetUserRef, "followerCount", FieldValue.increment(-1));
            } else {
                db.collection("users").document(currentUserId).get().addOnSuccessListener(currentUserDoc -> {
                    if (!currentUserDoc.exists()) {
                        listener.onComplete(false);
                        return;
                    }

                    db.collection("users").document(targetUserId).get().addOnSuccessListener(targetUserDoc -> {
                        if (!targetUserDoc.exists()) {
                            listener.onComplete(false);
                            return;
                        }

                        Map<String, Object> targetData = new HashMap<>();
                        targetData.put("name", targetUserDoc.getString("name"));
                        targetData.put("avatar", targetUserDoc.getString("avatar"));
                        targetData.put("role", targetUserDoc.getString("role"));
                        targetData.put("locationName", targetUserDoc.getString("locationName"));
                        targetData.put("followedAt", FieldValue.serverTimestamp()); // Keep the timestamp

                        Map<String, Object> currentUserData = new HashMap<>();
                        currentUserData.put("name", currentUserDoc.getString("name"));
                        currentUserData.put("avatar", currentUserDoc.getString("avatar"));
                        currentUserData.put("role", currentUserDoc.getString("role"));
                        currentUserData.put("locationName", currentUserDoc.getString("locationName"));
                        currentUserData.put("followedAt", FieldValue.serverTimestamp());

                        WriteBatch batch = db.batch();
                        batch.set(followingRef, targetData);
                        batch.set(followersRef, currentUserData);

                        // Increment counters
                        batch.update(db.collection("users").document(currentUserId), "followingCount", FieldValue.increment(1));
                        batch.update(db.collection("users").document(targetUserId), "followerCount", FieldValue.increment(1));

                        batch.commit().addOnSuccessListener(aVoid -> listener.onComplete(true))
                                .addOnFailureListener(e -> listener.onComplete(false));

                    }).addOnFailureListener(e -> listener.onComplete(false));
                }).addOnFailureListener(e -> listener.onComplete(false));
            }
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Follow toggle successful: " + !isCurrentlyFollowing);
            if (listener != null) listener.onComplete(true);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Follow toggle failed", e);
            if (listener != null) listener.onComplete(false);
        });
    }
}
