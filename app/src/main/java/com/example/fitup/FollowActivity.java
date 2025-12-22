package com.example.fitup;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FollowActivity extends AppCompatActivity {

    private String targetUserId;
    private String initialTab; // "following" or "followers"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_follow);

        // 1. Get Intent Data
        targetUserId = getIntent().getStringExtra("userId");
        initialTab = getIntent().getStringExtra("initialTab"); // Pass "followers" or "following"

        if (targetUserId == null) {
            finish();
            return;
        }

        ImageView btnBack = findViewById(R.id.imgbtn_Back);
        btnBack.setOnClickListener(v -> finish());

        TextView tvTitle = findViewById(R.id.tvTitle);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        FollowPagerAdapter pagerAdapter = new FollowPagerAdapter(this, targetUserId);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Following");
            } else {
                tab.setText("Followers");
            }
        }).attach();

        if ("followers".equalsIgnoreCase(initialTab)) {
            viewPager.setCurrentItem(1, false);
        } else {
            viewPager.setCurrentItem(0, false);
        }
    }

    private static class FollowPagerAdapter extends FragmentStateAdapter {
        private final String userId;

        public FollowPagerAdapter(@NonNull AppCompatActivity fragmentActivity, String userId) {
            super(fragmentActivity);
            this.userId = userId;
        }

        @NonNull
        @Override
        public androidx.fragment.app.Fragment createFragment(int position) {
            if (position == 0) {
                return FollowListFragment.newInstance("following", userId);
            } else {
                return FollowListFragment.newInstance("followers", userId);
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
