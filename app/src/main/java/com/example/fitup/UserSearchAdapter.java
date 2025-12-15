package com.example.fitup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // You have this dependency
import java.util.List;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;

    public UserSearchAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_user_search, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        String role = user.getRole() != null ? user.getRole() : "Member";
        role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();

        holder.tvUserName.setText(user.getName());
        holder.tvUserRole.setText(role);

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(context)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.user)
                    .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(R.drawable.user);
        }

        // You can set an OnClickListener here to navigate to the user's profile
        holder.itemView.setOnClickListener(v -> {
            // Intent to view user profile, passing user.getUid() for example
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUserName;
        TextView tvUserRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserRole = itemView.findViewById(R.id.tv_user_role);
        }
    }
}
