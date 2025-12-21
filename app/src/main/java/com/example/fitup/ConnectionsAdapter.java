package com.example.fitup;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

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
        String role = connection.getSenderRole();
        holder.tvDescription.setText((role != null ? role : "Member"));

        if (connection.getSenderAvatar() != null) {
            Glide.with(context).load(connection.getSenderAvatar()).into(holder.imgAvatar);
        }

        if ("trainer".equalsIgnoreCase(role)) {
            holder.btnBook.setVisibility(View.VISIBLE);
        } else {
            holder.btnBook.setVisibility(View.GONE);
        }

        holder.btnMessage.setOnClickListener(v -> {
            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String partnerUid;

            if (connection.getFromUid().equals(myUid)) {
                partnerUid = connection.getToUid();
            } else {
                partnerUid = connection.getFromUid();
            }

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("RECEIVER_ID", partnerUid);
            intent.putExtra("RECEIVER_NAME", connection.getSenderName());
            context.startActivity(intent);
        });

        holder.btnBook.setOnClickListener(v -> {
            Toast.makeText(context, "Booking feature coming soon!", Toast.LENGTH_SHORT).show();
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.profile_image);
            tvName = itemView.findViewById(R.id.name_text);
            tvDescription = itemView.findViewById(R.id.description_text);
            btnBook = itemView.findViewById(R.id.book_session_button);
            btnMessage = itemView.findViewById(R.id.message_button);
        }
    }
}