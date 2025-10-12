package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;


public class IntroductionPage extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private OnboardingAdapter adapter;
    private Button btnJoinUs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_introduction_page);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        btnJoinUs = findViewById(R.id.btnJoinUs);
        //Data cho từng trang
        List<OnboardingItem> onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardingItem("Welcome to FitSo", "The ultimate app for booking your personal fitness trainer! Connect with expert trainers and achieve your goals.", R.drawable.image1));
        onboardingItems.add(new OnboardingItem("Track Your Progress", "Your journey to a healthier, fitter you starts here. Book sessions with top fitness trainers and tailor your workouts to fit your lifestyle and goals.", R.drawable.image2));
        onboardingItems.add(new OnboardingItem("Achieve Your Goals", "Connect with top fitness trainers, personalize your workout plans, and achieve your health and wellness goals with ease !", R.drawable.image3));

        //Khởi tạo và gán adapter cho ViewPager2
        adapter = new OnboardingAdapter(onboardingItems);
        viewPager.setAdapter(adapter);

        //Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
            }
        }).attach();

        //Chỉnh margin giữa các dots
        tabLayout.post(() -> {
            ViewGroup tabs = (ViewGroup) tabLayout.getChildAt(0);
            int marginInDp = 5;
            float scale = getResources().getDisplayMetrics().density;
            int marginInPx = (int) (marginInDp * scale + 0.5f);

            for (int i = 0; i < tabs.getChildCount(); i++) {
                View tab = tabs.getChildAt(i);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                params.setMargins(marginInPx, 0, marginInPx, 0);
                tab.requestLayout();
            }
        });



        btnJoinUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroductionPage.this, MainActivity.class));
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}