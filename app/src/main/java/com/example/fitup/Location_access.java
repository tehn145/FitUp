package com.example.fitup;

import android.Manifest;import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address; // Added
import android.location.Geocoder; // Added
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions; // Added

import java.io.IOException; // Added
import java.util.HashMap;   // Added
import java.util.List;      // Added
import java.util.Locale;    // Added
import java.util.Map;       // Added

public class Location_access extends AppCompatActivity {
    private static final String TAG = "LocationAccess";
    private Button buttonAllowAccess;
    private final String permission = Manifest.permission.ACCESS_FINE_LOCATION;

    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Permission Granted! Fetching location...", Toast.LENGTH_SHORT).show();
                    fetchAndSaveLocation();
                } else {
                    Toast.makeText(this, "Permission Denied. Skipping location setup.", Toast.LENGTH_SHORT).show();
                    goToNextScreen();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location_access);

        buttonAllowAccess = findViewById(R.id.button_allow_access);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        buttonAllowAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        Location_access.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Location_access.this, "Permission already granted. Fetching location...", Toast.LENGTH_SHORT).show();
                    fetchAndSaveLocation();
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchAndSaveLocation() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: No user logged in.", Toast.LENGTH_SHORT).show();
            goToNextScreen();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Error: Location permission missing.", Toast.LENGTH_SHORT).show();
            goToNextScreen();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Instead of saving directly, we process it to find the name first
                        processAndSaveLocation(location, currentUser.getUid());
                    } else {
                        Log.w(TAG, "FusedLocationProvider returned null location.");
                        Toast.makeText(this, "Could not retrieve location. Please ensure location is enabled.", Toast.LENGTH_LONG).show();
                        goToNextScreen();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Error getting location", e);
                    Toast.makeText(this, "Failed to get location.", Toast.LENGTH_SHORT).show();
                    goToNextScreen();
                });
    }

    // New helper method to handle Geocoding before saving
    private void processAndSaveLocation(Location location, String userId) {
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

        // Geocoder network operations must run on a background thread
        new Thread(() -> {
            String foundLocationName = null;
            try {
                Geocoder geocoder = new Geocoder(Location_access.this, Locale.getDefault());
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

            // Switch back to Main Thread to save to Firestore and update UI
            String finalName = foundLocationName;
            runOnUiThread(() -> saveToFirestore(userId, geoPoint, finalName));

        }).start();
    }

    private void saveToFirestore(String userId, GeoPoint geoPoint, String locationName) {
        Map<String, Object> data = new HashMap<>();
        data.put("location", geoPoint);

        if (locationName != null) {
            data.put("locationName", locationName);
        }

        db.collection("users").document(userId)
                .set(data, SetOptions.merge()) // Use merge to avoid overwriting other fields
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Location (GeoPoint) saved successfully!");
                    if (locationName != null) {
                        Log.d(TAG, "Location Name saved: " + locationName);
                    }
                    Toast.makeText(Location_access.this, "Location saved!", Toast.LENGTH_SHORT).show();
                    goToNextScreen();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing location to Firestore", e);
                    Toast.makeText(Location_access.this, "Failed to save location.", Toast.LENGTH_SHORT).show();
                    goToNextScreen();
                });
    }

    private void goToNextScreen() {
        Intent intent = new Intent(Location_access.this, congrats.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
