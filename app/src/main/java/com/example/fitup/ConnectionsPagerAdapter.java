package com.example.fitup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;



public class ConnectionsPagerAdapter extends FragmentStateAdapter {
    private final int tabCount = 2;
    private final String[] tabTitles = new String[]{"Connections", "Pending Request"};

    public ConnectionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:

                return new ConnectionsFragment();
            case 1:
                return new PendingRequestFragment();
            default:
                return new ConnectionsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return tabCount;
    }

    public String getTabTitle(int position) {
        if (position >= 0 && position < tabTitles.length) {
            return tabTitles[position];
        }
        return "";
    }
}