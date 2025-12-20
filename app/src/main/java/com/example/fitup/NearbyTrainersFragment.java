package com.example.fitup;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

// MAPBOX Imports
import com.mapbox.geojson.Point;
import com.mapbox.maps.AnnotatedFeature;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;


import de.hdodenhof.circleimageview.CircleImageView;

// NOTE: This fragment no longer needs OnMapReadyCallback
public class NearbyTrainersFragment extends Fragment {

    private static final String TAG = "NearbyTrainers";
    private MapView mapView; // Mapbox MapView
    private ViewAnnotationManager viewAnnotationManager; // To add custom views as markers

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // The XML now contains a MapView, not a Fragment
        return inflater.inflate(R.layout.activity_nearby_trainers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize Mapbox MapView
        mapView = view.findViewById(R.id.mapView);
        // Get the manager that lets us add custom Views to the map
        viewAnnotationManager = mapView.getViewAnnotationManager();

        // Load the map style and then fetch data
        mapView.getMapboxMap().loadStyle(Style.DARK, style -> {
            fetchUserLocationAndTrainers();
        });
    }

    private void fetchUserLocationAndTrainers() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                GeoPoint geoPoint = document.getGeoPoint("location");
                double lat = (geoPoint != null) ? geoPoint.getLatitude() : 40.7128;
                double lng = (geoPoint != null) ? geoPoint.getLongitude() : -74.0060;
                Point userPoint = Point.fromLngLat(lng, lat);

                // Move camera to user
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder()
                        .center(userPoint)
                        .zoom(14.0)
                        .build());

                // Add markers
                String myAvatarUrl = document.getString("avatar");
                addAvatarMarker(userPoint, myAvatarUrl);
                loadNearbyTrainers();
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching user location", e));
    }

    private void loadNearbyTrainers() {
        db.collection("users").limit(20).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                //if (doc.getId().equals(mAuth.getCurrentUser().getUid())) continue;

                GeoPoint gp = doc.getGeoPoint("location");
                if (gp != null) {
                    Point trainerPoint = Point.fromLngLat(gp.getLongitude(), gp.getLatitude());
                    String avatarUrl = doc.getString("avatar");
                    addAvatarMarker(trainerPoint, avatarUrl);
                }
            }
        });
    }

    private void addAvatarMarker(Point position, String imageUrl) {
        // 1. Create the Annotation Options
        ViewAnnotationOptions options = new ViewAnnotationOptions.Builder()
                .annotatedFeature(AnnotatedFeature.valueOf(position))
                .allowOverlap(true)      // Recommended to allow markers to overlap
                .build();

        // 2. Inflate the custom marker layout
        View markerView = viewAnnotationManager.addViewAnnotation(R.layout.layout_marker_avatar, options);
        CircleImageView markerImg = markerView.findViewById(R.id.marker_avatar);

        // 3. Load image into the view using Glide
        if (getContext() != null) {
            Glide.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.defaultavt)
                    .into(markerImg);
        }
    }

    // You no longer need the createBitmapFromView() helper function with this approach
}
