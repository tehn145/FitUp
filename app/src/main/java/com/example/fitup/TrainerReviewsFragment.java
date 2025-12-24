package com.example.fitup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class TrainerReviewsFragment extends Fragment {

    private static final String ARG_TRAINER_ID = "trainer_id";
    private String trainerId;

    private TextView tvAverageRating, tvTotalReviews, tvNoReviews;
    private RatingBar rbAverage;
    private RecyclerView rvReviews;
    private ReviewsAdapter adapter;
    private List<Review> reviewList;
    private ImageView btnBack;

    public TrainerReviewsFragment() {
        // Required empty public constructor
    }

    // Static factory method to pass arguments easily
    public static TrainerReviewsFragment newInstance(String trainerId) {
        TrainerReviewsFragment fragment = new TrainerReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRAINER_ID, trainerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trainerId = getArguments().getString(ARG_TRAINER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trainer_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        btnBack = view.findViewById(R.id.btnBack);
        tvAverageRating = view.findViewById(R.id.tvAverageRating);
        rbAverage = view.findViewById(R.id.rbAverage);
        tvTotalReviews = view.findViewById(R.id.tvTotalReviews);
        rvReviews = view.findViewById(R.id.rvReviews);
        tvNoReviews = view.findViewById(R.id.tvNoReviews);

        // Setup RecyclerView
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewList = new ArrayList<>();
        adapter = new ReviewsAdapter(reviewList);
        rvReviews.setAdapter(adapter);

        // Listeners
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                if (getActivity() != null) getActivity().finish();
            }
        });

        if (trainerId != null) {
            loadTrainerStats();
            loadReviews();
        } else {
            Toast.makeText(getContext(), "Error: Trainer ID missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTrainerStats() {
        FirebaseFirestore.getInstance().collection("users").document(trainerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        double rating = 0.0;
                        long count = 0;

                        // Check specific fields created in your transaction
                        if (documentSnapshot.contains("rating")) {
                            rating = documentSnapshot.getDouble("rating");
                        }
                        if (documentSnapshot.contains("reviewCount")) {
                            count = documentSnapshot.getLong("reviewCount");
                        }

                        tvAverageRating.setText(String.format("%.1f", rating));
                        rbAverage.setRating((float) rating);
                        tvTotalReviews.setText("(" + count + " reviews)");
                    }
                })
                .addOnFailureListener(e -> Log.e("TrainerReviews", "Failed to load stats", e));
    }

    private void loadReviews() {
        FirebaseFirestore.getInstance().collection("reviews")
                .whereEqualTo("trainerId", trainerId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    reviewList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Review review = doc.toObject(Review.class);
                            // Verify object mapping or manually map if Review.class isn't perfect
                            if (review != null) {
                                reviewList.add(review);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        tvNoReviews.setVisibility(View.GONE);
                        rvReviews.setVisibility(View.VISIBLE);
                    } else {
                        tvNoReviews.setVisibility(View.VISIBLE);
                        rvReviews.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TrainerReviews", "Error loading reviews", e);
                    Toast.makeText(getContext(), "Failed to load reviews.", Toast.LENGTH_SHORT).show();
                });
    }
}
