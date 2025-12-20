package com.example.fitup;

import android.graphics.Typeface; // Import Typeface for bold text
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;

public class DiscoveryFragment extends Fragment {

    private MaterialButton btnMyNetwork, btnForYou, btnNearbyTrainers;
    private FrameLayout fragmentContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_discovery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnMyNetwork = view.findViewById(R.id.btnMyNetwork);
        btnForYou = view.findViewById(R.id.btnForYou);
        btnNearbyTrainers = view.findViewById(R.id.btnNearbyTrainers);
        fragmentContainer = view.findViewById(R.id.fragment_container);

        btnMyNetwork.setOnClickListener(v -> {
            updateTabAppearance(btnMyNetwork);
            loadFragment(new MyNetworkFragment());
        });

        btnForYou.setOnClickListener(v -> {
            updateTabAppearance(btnForYou);
            loadFragment(new ForYouFragment());
        });

        btnNearbyTrainers.setOnClickListener(v -> {
            updateTabAppearance(btnNearbyTrainers);
            loadFragment(new NearbyTrainersFragment());
        });

        updateTabAppearance(btnMyNetwork);
        loadFragment(new MyNetworkFragment());
    }

    private void updateTabAppearance(MaterialButton selectedButton) {
        // Reset all buttons to unselected state
        resetButton(btnMyNetwork);
        resetButton(btnForYou);
        resetButton(btnNearbyTrainers);

        selectedButton.setBackgroundTintList(getResources().getColorStateList(R.color.orange_dark, null)); // Active Background #FF6A00
        selectedButton.setTextColor(getResources().getColor(android.R.color.white, null));
        selectedButton.setTypeface(null, Typeface.BOLD);
        selectedButton.setCornerRadius(dpToPx(20));
        selectedButton.setElevation(0);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void resetButton(MaterialButton button) {
        // Reset to "TextButton" style (Transparent background, Gray text)
        button.setBackgroundTintList(getResources().getColorStateList(android.R.color.transparent, null));
        button.setTextColor(0xFFCCCCCC); // Hex code for #CCCCCC (Gray) or use R.color.gray if defined
        button.setTypeface(null, Typeface.BOLD); // Keep them bold as per XML
        button.setElevation(0);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
