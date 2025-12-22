package com.example.fitup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder>{
    private List<User> mListUser;

    public FollowAdapter(List<User> mListUser) {
        this.mListUser = mListUser;
    }

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Liên kết với layout item_follow_user.xml bạn đã tạo
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow_user, parent, false);
        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        User user = mListUser.get(position);
        if (user == null) return;

        holder.tvName.setText(user.getName());
        holder.tvRole.setText(user.getRole());

        // Ẩn hiện địa điểm nếu không có
//        if (user.getLocation().isEmpty()) {
//            holder.tvLocation.setVisibility(View.GONE);
//        } else {
//            holder.tvLocation.setText(user.getLocation());
//            holder.tvLocation.setVisibility(View.VISIBLE);
//        }
//
//        // --- QUAN TRỌNG: XỬ LÝ MÀU NÚT ---
//        if (user.isFollowing()) {
//            // Đang follow -> Nút Xám "Unfollow"
//            holder.btnAction.setText("Unfollow");
//            holder.btnAction.setBackgroundResource(R.drawable.bg_button_gray_rounded);
//        } else {
//            // Chưa follow -> Nút Cam "Follow"
//            holder.btnAction.setText("Follow");
//            holder.btnAction.setBackgroundResource(R.drawable.bg_button_orange_rounded);
//        }
    }

    @Override
    public int getItemCount() {
        return mListUser != null ? mListUser.size() : 0;
    }

    public class FollowViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole, tvLocation;
        Button btnAction;

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ đúng ID trong item_follow_user.xml
            tvName = itemView.findViewById(R.id.tvName);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            btnAction = itemView.findViewById(R.id.btnUnfollow); // ID nút bấm
        }
    }
}
