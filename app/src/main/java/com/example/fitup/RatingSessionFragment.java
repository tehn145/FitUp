package com.example.fitup;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingSessionFragment extends BottomSheetDialogFragment {

    private static final String ARG_SESSION_ID = "session_id";
    private static final String ARG_TRAINER_ID = "trainer_id";

    private String sessionId;
    private String trainerId;

    private CircleImageView imgTrainerAvatar;
    private TextView tvTrainerName;
    private RatingBar ratingBar;
    private TextInputEditText etFeedback;
    private MaterialButton btnSubmit;
    private TextView tvSkip;

    public RatingSessionFragment() {
        // Required empty public constructor
    }

    public static RatingSessionFragment newInstance(String sessionId, String trainerId) {
        RatingSessionFragment fragment = new RatingSessionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SESSION_ID, sessionId);
        args.putString(ARG_TRAINER_ID, trainerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sessionId = getArguments().getString(ARG_SESSION_ID);
            trainerId = getArguments().getString(ARG_TRAINER_ID);
        }
        // Prevent user from cancelling by clicking outside (forces them to Skip or Submit)
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rating_session, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;

            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);            if (bottomSheet != null) {
                bottomSheet.setBackgroundResource(android.R.color.transparent);
            }

        });
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgTrainerAvatar = view.findViewById(R.id.imgTrainerAvatar);
        tvTrainerName = view.findViewById(R.id.tvTrainerName);
        ratingBar = view.findViewById(R.id.ratingBar);
        etFeedback = view.findViewById(R.id.etFeedback);
        btnSubmit = view.findViewById(R.id.btnSubmitRating);
        tvSkip = view.findViewById(R.id.tvSkipRating);

        loadTrainerInfo();

        btnSubmit.setOnClickListener(v -> submitRating());
        tvSkip.setOnClickListener(v -> skipRating());
    }

    private void loadTrainerInfo() {
        if (trainerId == null) return;

        FirebaseFirestore.getInstance().collection("users").document(trainerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String avatarUrl = documentSnapshot.getString("avatar");

                        tvTrainerName.setText(name != null ? name : "Trainer");

                        if (getContext() != null && avatarUrl != null) {
                            Glide.with(getContext())
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.defaultavt)
                                    .into(imgTrainerAvatar);
                        }
                    }
                });
    }

    private void submitRating() {
        float rating = ratingBar.getRating();
        String feedback = etFeedback.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(getContext(), "Please select a star rating", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("sessionId", sessionId);
        reviewData.put("trainerId", trainerId);
        reviewData.put("clientId", userId);
        reviewData.put("rating", rating);
        reviewData.put("comment", feedback);
        reviewData.put("timestamp", System.currentTimeMillis());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.runTransaction(transaction -> {
            com.google.firebase.firestore.DocumentReference trainerRef = db.collection("users").document(trainerId);
            com.google.firebase.firestore.DocumentSnapshot trainerSnapshot = transaction.get(trainerRef);

            com.google.firebase.firestore.DocumentReference newReviewRef = db.collection("reviews").document();

            double currentRatingSum = 0;
            long currentReviewCount = 0;

            if (trainerSnapshot.contains("ratingSum")) {
                currentRatingSum = trainerSnapshot.getDouble("ratingSum");
            }
            if (trainerSnapshot.contains("reviewCount")) {
                currentReviewCount = trainerSnapshot.getLong("reviewCount");
            }

            double newRatingSum = currentRatingSum + rating;
            long newReviewCount = currentReviewCount + 1;
            double newAverageRating = newRatingSum / newReviewCount;

            transaction.set(newReviewRef, reviewData);

            transaction.update(db.collection("sessions").document(sessionId), "isRated", true);

            // Update the Trainer's stats
            transaction.update(trainerRef, "ratingSum", newRatingSum);
            transaction.update(trainerRef, "reviewCount", newReviewCount);
            transaction.update(trainerRef, "rating", newAverageRating); // The displayable average

            return null;
        }).addOnSuccessListener(result -> {
            //Toast.makeText(getContext(), "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
            dismiss();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error submitting: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void skipRating() {
        // Even if they skip, we mark it as rated/handled so we don't annoy them
        markSessionAsRated();
    }

    private void markSessionAsRated() {
        FirebaseFirestore.getInstance().collection("sessions").document(sessionId)
                .update("isRated", true)
                .addOnCompleteListener(task -> dismiss());
    }
}
