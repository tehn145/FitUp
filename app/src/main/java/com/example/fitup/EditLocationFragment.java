package com.example.fitup;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address; // Added
import android.location.Geocoder; // Added
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

import java.io.IOException; // Added
import java.util.HashMap;
import java.util.List; // Added
import java.util.Locale; // Added
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
            return;
        }

        // 1. First, try the fast and efficient getLastLocation()
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), lastLocation -> {
            if (lastLocation != null) {
                Log.d(TAG, "Got location from getLastLocation()");
                processAndSaveLocation(lastLocation); // Updated to use helper method
            } else {
                // 2. Fallback: The last location was null, so actively request the current location.
                Log.d(TAG, "getLastLocation() was null, falling back to getCurrentLocation()");
                CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                        .addOnSuccessListener(requireActivity(), currentLocation -> {
                            if (currentLocation != null) {
                                Log.d(TAG, "Got location from getCurrentLocation()");
                                processAndSaveLocation(currentLocation); // Updated to use helper method
                            } else {
                                Log.w(TAG, "Both getLastLocation() and getCurrentLocation() returned null.");
                                Toast.makeText(getContext(), "Could not get location. Make sure location is enabled.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(requireActivity(), e -> {
                            Log.e(TAG, "getCurrentLocation Exception: " + e.getMessage());
                            Toast.makeText(getContext(), "Failed to get location: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });
    }

    // New helper method to handle Geocoding before saving
    private void processAndSaveLocation(Location location) {
        if (getContext() == null) return;

        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        Toast.makeText(getContext(), "Processing location...", Toast.LENGTH_SHORT).show();

        // Geocoder network operations must run on a background thread
        new Thread(() -> {
            String foundLocationName = null;
            try {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    // Logic to construct a readable string
                    String city = address.getLocality();
                    if (city == null) city = address.getSubAdminArea();
                    if (city == null) city = address.getAdminArea();

                    String country = address.getCountryName();

                    if (city != null && country != null) {
                        foundLocationName = city + ", " + country;
                    } else if (country != null) {
                        foundLocationName = country;
                    } else {
                        foundLocationName = city;
                    }

                    Log.d(TAG, "Geocoder found name: " + foundLocationName);
                }
            } catch (IOException e) {
                Log.e(TAG, "Geocoder failed: " + e.getMessage());
            }

            // Save to Firestore (must switch back to Main Thread for UI updates inside saveLocation,
            // but the Firestore call itself is thread-safe. We pass the data to the helper).
            String finalName = foundLocationName;
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> saveLocationToFirestore(geoPoint, finalName));
            }

        }).start();
    }

    private void saveLocationToFirestore(GeoPoint geoPoint, @Nullable String locationName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Error: No user logged in.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("location", geoPoint);

        // Add the location name if we found one
        if (locationName != null) {
            data.put("locationName", locationName);
        }

        db.collection("users").document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Location (GeoPoint) saved successfully!");
                    if (locationName != null) {
                        Log.d(TAG, "Location Name saved: " + locationName);
                    }
                    Toast.makeText(getContext(), "Location Updated!", Toast.LENGTH_SHORT).show();
                    dismissBottomSheet();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(getContext(), "Failed to save location.", Toast.LENGTH_SHORT).show();
                });
    }

    private void dismissBottomSheet() {
        if (getView() == null) return;
        View parent = (View) getView().getParent();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
        if (behavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}
