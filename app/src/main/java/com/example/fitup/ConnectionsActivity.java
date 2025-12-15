package com.example.fitup;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ConnectionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connections);

        // 1. Ánh xạ View
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        // 2. Thiết lập Adapter cho ViewPager2
        ConnectionsPagerAdapter pagerAdapter = new ConnectionsPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // 3. KẾT NỐI TAB LAYOUT VỚI VIEW PAGER (TabLayoutMediator)
        // Dùng TabLayoutMediator để đồng bộ hóa hành động vuốt và tên Tab.
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    // Đặt tên Tab
                    tab.setText(pagerAdapter.getTabTitle(position));
                }
        ).attach();


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }
}