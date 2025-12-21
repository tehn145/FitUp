package com.example.fitup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FollowViewPagerAdapter extends FragmentStateAdapter{
    public FollowViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new FollowersFragment();
        } else {
            return new FollowingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 2 Tab
    }
}
