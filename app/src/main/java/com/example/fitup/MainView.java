package com.example.fitup;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainView extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        bottomNavigationView = findViewById(R.id.bottomNav);

        if (savedInstanceState == null) {
            currentFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, currentFragment)
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;
            boolean isComingFromRight = false;

            if (itemId == R.id.nav_home) {
                if (!(currentFragment instanceof HomeFragment)) {
                    selectedFragment = new HomeFragment();
                    isComingFromRight = true;
                }
            } else if (itemId == R.id.nav_profile) {
                if (!(currentFragment instanceof ProfileFragment)) {
                    selectedFragment = new ProfileFragment();
                    isComingFromRight = false;
                }
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, isComingFromRight);
                currentFragment = selectedFragment;
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment, boolean isComingFromRight) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (isComingFromRight) {
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
