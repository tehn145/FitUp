package com.example.fitup;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<Review> reviewList;
    private Context context;

    public ReviewsAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.ratingBar.setRating((float) review.getRating());

        holder.tvContent.setText(review.getComment());

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(review.getTimestamp());
        String date = DateFormat.format("dd MMM yyyy", cal).toString();
        holder.tvDate.setText(date);

        String clientId = review.getClientId();

        holder.tvName.setText("Loading...");
        holder.imgAvatar.setImageResource(R.drawable.defaultavt);

        if (clientId != null && !clientId.isEmpty()) {
            FirebaseFirestore.getInstance().collection("users").document(clientId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            holder.tvName.setText(name != null ? name : "Anonymous");

                            String avatarUrl = documentSnapshot.getString("avatar");
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(context)
                                        .load(avatarUrl)
                                        .placeholder(R.drawable.defaultavt)
                                        .error(R.drawable.defaultavt)
                                        .into(holder.imgAvatar);
                            }
                        } else {
                            holder.tvName.setText("Unknown User");
                        }
                    })
                    .addOnFailureListener(e -> holder.tvName.setText("User"));
        } else {
            holder.tvName.setText("Anonymous");
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvatar;
        TextView tvName, tvDate, tvContent;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgReviewerAvatar);
            tvName = itemView.findViewById(R.id.tvReviewerName);
            tvDate = itemView.findViewById(R.id.tvReviewDate);
            ratingBar = itemView.findViewById(R.id.rbItemRating);
            tvContent = itemView.findViewById(R.id.tvReviewContent);
        }
    }
}
