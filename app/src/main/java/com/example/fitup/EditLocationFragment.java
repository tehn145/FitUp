package com.example.fitup;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

// CORRECT IMPORT
import com.google.android.gms.location.Priority;

import com.google.android.gms.tasks.CancellationTokenSource;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditLocationFragment extends Fragment {

    private static final String TAG = "EditLocationFragment";

    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private Button btnUseCurrentLocation;
    private Button btnCancel;

    // ActivityResultLauncher for handling permission requests
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action
                    getCurrentLocationAndSave();
                } else {
                    // Explain to the user that the feature is unavailable
                    Toast.makeText(getContext(), "Location access denied. Cannot get current location.", Toast.LENGTH_LONG).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        btnUseCurrentLocation = view.findViewById(R.id.btnUseCurrentLocation);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnUseCurrentLocation.setOnClickListener(v -> checkPermissionAndGetLocation());
        btnCancel.setOnClickListener(v -> dismissBottomSheet());
    }

    private void checkPermissionAndGetLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            getCurrentLocationAndSave();
        } else {
            // You can directly ask for the permission.
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void getCurrentLocationAndSave() {
        // Double-check permission before making the call
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // This check is good, keep it.
            return;
        }

        // 1. First, try the fast and efficient getLastLocation()
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), lastLocation -> {
            if (lastLocation != null) {
                // Success! We got a cached location.
                Log.d(TAG, "Got location from getLastLocation()");
                GeoPoint geoPoint = new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
                saveLocationToFirestore(geoPoint);
            } else {
                // 2. Fallback: The last location was null, so actively request the current location.
                Log.d(TAG, "getLastLocation() was null, falling back to getCurrentLocation()");
                CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                        .addOnSuccessListener(requireActivity(), currentLocation -> {
                            if (currentLocation != null) {
                                // Success on the fallback!
                                Log.d(TAG, "Got location from getCurrentLocation()");
                                GeoPoint geoPoint = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                                saveLocationToFirestore(geoPoint);
                            } else {
                                // Both methods failed. Location is likely disabled on the device.
                                Log.w(TAG, "Both getLastLocation() and getCurrentLocation() returned null.");
                                Toast.makeText(getContext(), "Could not get location. Make sure location is enabled on your device.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(requireActivity(), e -> {
                            // Handle failure of the active request
                            Log.e(TAG, "getCurrentLocation Exception: " + e.getMessage());
                            Toast.makeText(getContext(), "Failed to get location: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });
    }

    private void saveLocationToFirestore(GeoPoint geoPoint) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Error: No user logged in.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("location", geoPoint);

        db.collection("users").document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Location saved successfully!");
                    Toast.makeText(getContext(), "Location Updated!", Toast.LENGTH_SHORT).show();
                    dismissBottomSheet();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(getContext(), "Failed to save location.", Toast.LENGTH_SHORT).show();
                });
    }

    private void dismissBottomSheet() {
        View parent = (View) requireView().getParent();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
        if (behavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}
