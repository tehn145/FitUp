package com.example.fitup;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Import Button
import android.widget.TextView;
import android.widget.Toast; // Import Toast

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth; // Import FirebaseAuth

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private String currentUid;
    private String listType;
    private String profileOwnerId;

    public UserListAdapter(Context context, List<User> userList, String listType, String profileOwnerId) {
        this.context = context;
        this.userList = userList;
        this.listType = listType;
        this.currentUid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        this.profileOwnerId = profileOwnerId;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follow_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvName.setText(user.getName() != null ? user.getName() : "Unknown");

        String role = user.getRole();
        if (role != null && !role.isEmpty()) {
            String formattedRole = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
            holder.tvRole.setText(formattedRole);
        } else {
            holder.tvRole.setText("User");
        }

        String locName = user.getLocationName();
        if (locName != null && !locName.isEmpty()) {
            holder.tvLocation.setText(locName);
        } else {
            holder.tvLocation.setText("Location not specified");
        }

        Glide.with(context)
                .load(user.getAvatar())
                .placeholder(R.drawable.defaultavt)
                .error(R.drawable.defaultavt)
                .into(holder.ivAvatar);

        boolean isMyProfile = currentUid != null && currentUid.equals(profileOwnerId);
        boolean isFollowingList = "following".equalsIgnoreCase(listType);

        if (isMyProfile && isFollowingList) {
            holder.btnUnfollow.setVisibility(View.VISIBLE);

            holder.btnUnfollow.setOnClickListener(v -> {
                holder.btnUnfollow.setEnabled(false);
                holder.btnUnfollow.setText("...");

                FirestoreHelper.toggleFollow(user.getUserId(), true, success -> {
                    if (success) {
                        int currentPosition = holder.getAdapterPosition();
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            userList.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                            notifyItemRangeChanged(currentPosition, userList.size());
                        }
                    } else {
                        holder.btnUnfollow.setEnabled(true);
                        holder.btnUnfollow.setText("Unfollow");
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            holder.btnUnfollow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            if ("trainer".equalsIgnoreCase(user.getRole())) {
                intent = new Intent(context, TrainerProfileActivity.class);
            } else {
                intent = new Intent(context, UserProfileActivity.class);
            }

            intent.putExtra("targetUserId", user.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAvatar;
        TextView tvName, tvRole, tvLocation;
        AppCompatButton btnUnfollow;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            btnUnfollow = itemView.findViewById(R.id.btnUnfollow);
        }
    }
}
