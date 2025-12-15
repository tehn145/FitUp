package com.example.fitup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
// Dòng import sau đã được sửa chính tả từ Fragement -> Fragment
// Và có thể bỏ đi nếu không cần thiết, nhưng tôi giữ lại để đảm bảo không có lỗi
// import com.example.fitup.ConnectionsFragment;
// import com.example.fitup.PendingRequestFragment;


public class ConnectionsPagerAdapter extends FragmentStateAdapter {
    private final int tabCount = 2;
    private final String[] tabTitles = new String[]{"Connections", "Pending Request"};

    // Constructor nhận FragmentActivity (Activity chứa ViewPager)
    public ConnectionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    // 1. Tạo Fragment tương ứng với vị trí (position)
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // SỬA: ConnectionsFragement -> ConnectionsFragment
                return new ConnectionsFragment();
            case 1:
                return new PendingRequestFragment();
            default:
                // SỬA: ConnectionsFragement -> ConnectionsFragment
                return new ConnectionsFragment();
        }
    }

    // 2. Trả về tổng số mục (Fragment)
    @Override
    public int getItemCount() {
        return tabCount;
    }

    // Hàm tiện ích để lấy tên Tab
    public String getTabTitle(int position) {
        if (position >= 0 && position < tabTitles.length) {
            return tabTitles[position];
        }
        return "";
    }
}