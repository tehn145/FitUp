package com.example.fitup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.BuildConfig;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.LocalCacheSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class IntroductionPage extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private OnboardingAdapter adapter;
    private Button btnJoinUs;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w("DebugMode", String.valueOf(BuildConfig.DEBUG));
        if (true) {
            try {
                String host = "10.101.25.210";

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                FirebaseFunctions functions = FirebaseFunctions.getInstance();
                FirebaseDatabase database = com.google.firebase.database.FirebaseDatabase.getInstance(); // Added

                auth.useEmulator(host, 9099);
                firestore.useEmulator(host, 8080);
                storage.useEmulator(host, 9199);
                functions.useEmulator(host, 5001);
                database.useEmulator(host, 9000);

                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder(firestore.getFirestoreSettings())
                        .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                        .setLocalCacheSettings(PersistentCacheSettings.newBuilder()
                                .build())
                        .build();

                FirebaseFirestore.getInstance().setFirestoreSettings(settings);

                Log.d("EmulatorConfig", "Successfully connected to Firebase Emulators.");

            } catch (IllegalStateException e) {
                Log.w("EmulatorConfig", "Failed to connect to emulators. They may have been initialized elsewhere.", e);
            }
        } else Log.w("EmulatorConfig", "Not running in debug mode. Skipping emulator configuration.");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(IntroductionPage.this, MainView.class);
            startActivity(intent);
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_introduction_page);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        btnJoinUs = findViewById(R.id.btnJoinUs);

        List<OnboardingItem> onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardingItem("Welcome to FitUp", "The ultimate app for booking your personal fitness trainer! Connect with expert trainers and achieve your goals.", R.drawable.image1));
        onboardingItems.add(new OnboardingItem("Track Your Progress", "Your journey to a healthier, fitter you starts here. Book sessions with top fitness trainers and tailor your workouts to fit your lifestyle and goals.", R.drawable.image2));
        onboardingItems.add(new OnboardingItem("Achieve Your Goals", "Connect with top fitness trainers, personalize your workout plans, and achieve your health and wellness goals with ease !", R.drawable.image3));

        adapter = new OnboardingAdapter(onboardingItems);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                View tabView = getLayoutInflater().inflate(R.layout.custom_tab_dot, null);
                tab.setCustomView(tabView);
            }
        }).attach();

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

    public static float dpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
