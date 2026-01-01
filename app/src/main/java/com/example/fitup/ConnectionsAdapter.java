package com.example.fitup;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.Filter;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsAdapter.ViewHolder> {

    private Context context;
    private List<ConnectionRequest> connectionList;

    public ConnectionsAdapter(Context context, List<ConnectionRequest> connectionList) {
        this.context = context;
        this.connectionList = connectionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_connection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConnectionRequest connection = connectionList.get(position);

        holder.tvName.setText(connection.getSenderName());

        // Fix 1: Uppercase first letter of role
        String role = connection.getSenderRole();
        String displayRole = "Member";
        if (role != null && !role.isEmpty()) {
            displayRole = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
        }
        holder.tvDescription.setText(displayRole);

        if (connection.getSenderAvatar() != null) {
            Glide.with(context).load(connection.getSenderAvatar()).into(holder.imgAvatar);
        }

        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String partnerUid = connection.getFromUid().equals(myUid) ? connection.getToUid() : connection.getFromUid();

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(partnerUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isVerified = documentSnapshot.getBoolean("isVerified");
                        if (isVerified != null && isVerified) {
                            holder.ivVerification.setVisibility(View.VISIBLE);
                        } else {
                            holder.ivVerification.setVisibility(View.INVISIBLE);
                        }
                    }
                });

        checkSessionStatus(myUid, partnerUid, holder.btnBook, connection.getSenderName());

        holder.btnMessage.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("RECEIVER_ID", partnerUid);
            intent.putExtra("RECEIVER_NAME", connection.getSenderName());
            context.startActivity(intent);
        });
    }

    private void checkSessionStatus(String myUid, String partnerUid, Button btnBook, String partnerName) {
        btnBook.setVisibility(View.GONE);

        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("sessions")
                .where(Filter.or(
                        Filter.and(
                                Filter.equalTo("trainerId", myUid),
                                Filter.equalTo("clientId", partnerUid)
                        ),
                        Filter.and(
                                Filter.equalTo("trainerId", partnerUid),
                                Filter.equalTo("clientId", myUid)
                        )
                ))
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean hasActiveSession = false;
                    String activeSessionId = null;

                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot) {
                        String status = doc.getString("status");
                        if ("active".equals(status) || "pending".equals(status)) {
                            hasActiveSession = true;
                            activeSessionId = doc.getId();
                            break;
                        }
                    }

                    final boolean sessionExists = hasActiveSession;
                    final String sessionId = activeSessionId;

                    db.collection("users").document(myUid).get().addOnSuccessListener(userDoc -> {
                        String myRole = userDoc.getString("role");
                        boolean amITrainer = "trainer".equalsIgnoreCase(myRole);

                        if (sessionExists) {
                            btnBook.setVisibility(View.VISIBLE);
                            btnBook.setText("View Session");
                            btnBook.setOnClickListener(v -> {
                                if (context instanceof androidx.fragment.app.FragmentActivity) {
                                    SessionDetailsFragment fragment = SessionDetailsFragment.newInstance(sessionId);
                                    fragment.show(((androidx.fragment.app.FragmentActivity) context).getSupportFragmentManager(), "SessionDetails");
                                }
                            });
                        } else {
                            if (amITrainer) {
                                btnBook.setVisibility(View.VISIBLE);
                                btnBook.setText("Book A Session");
                                btnBook.setOnClickListener(v -> {
                                    Intent intent = new Intent(context, BookSessionActivity.class);
                                    intent.putExtra("RECEIVER_ID", partnerUid);
                                    intent.putExtra("RECEIVER_NAME", partnerName);
                                    intent.putExtra("TRIGGER_BOOKING", true); // Pass flag to trigger booking dialog in Chat
                                    context.startActivity(intent);


                                });
                            } else {
                                btnBook.setVisibility(View.GONE);
                            }
                        }
                    });
                });
    }

    @Override
    public int getItemCount() {
        return connectionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvatar;
        TextView tvName, tvDescription;
        Button btnBook, btnMessage;
        ImageView ivVerification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.profile_image);
            tvName = itemView.findViewById(R.id.name_text);
            tvDescription = itemView.findViewById(R.id.description_text);
            btnBook = itemView.findViewById(R.id.book_session_button);
            btnMessage = itemView.findViewById(R.id.message_button);
            ivVerification = itemView.findViewById(R.id.iv_verification);
        }
    }
}