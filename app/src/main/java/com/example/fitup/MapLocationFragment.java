package com.example.fitup;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

// Mapbox Imports
import com.mapbox.geojson.Point;
import com.mapbox.maps.AnnotatedFeature;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.GesturesUtils;

public class MapLocationFragment extends Fragment {

    private static final String ARG_IS_SELECTION_MODE = "is_selection_mode";
    private static final String ARG_INITIAL_LAT = "initial_lat";
    private static final String ARG_INITIAL_LNG = "initial_lng";

    public static final String REQUEST_KEY = "map_selection_request";
    public static final String RESULT_LAT = "result_lat";
    public static final String RESULT_LNG = "result_lng";

    private boolean isSelectionMode;
    private double initialLat;
    private double initialLng;

    // Use Mapbox MapView
    private MapView mapView;
    private MaterialButton btnConfirmLocation;
    private ImageButton btnBack;
    private ImageView centerMarker;

    private ViewAnnotationManager viewAnnotationManager;

    public MapLocationFragment() {
        // Required empty public constructor
    }

    public static MapLocationFragment newInstance(boolean isSelectionMode, double lat, double lng) {
        MapLocationFragment fragment = new MapLocationFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_SELECTION_MODE, isSelectionMode);
        args.putDouble(ARG_INITIAL_LAT, lat);
        args.putDouble(ARG_INITIAL_LNG, lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isSelectionMode = getArguments().getBoolean(ARG_IS_SELECTION_MODE);
            initialLat = getArguments().getDouble(ARG_INITIAL_LAT);
            initialLng = getArguments().getDouble(ARG_INITIAL_LNG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_location, container, false);

        // Note: Ensure your XML uses <com.mapbox.maps.MapView ... /> instead of the Google Maps tag
        mapView = view.findViewById(R.id.mapView);
        btnConfirmLocation = view.findViewById(R.id.btnConfirmLocation);
        btnBack = view.findViewById(R.id.btnBack);
        centerMarker = view.findViewById(R.id.ivCenterMarker);

        // Initialize ViewAnnotationManager
        if (mapView != null) {
            viewAnnotationManager = mapView.getViewAnnotationManager();
        }

        setupUI();

        // Load Mapbox Style
        if (mapView != null) {
            mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, style -> {
                onMapReady();
            });
        }

        return view;
    }

    private void setupUI() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        if (isSelectionMode) {
            centerMarker.setVisibility(View.VISIBLE);
            btnConfirmLocation.setVisibility(View.VISIBLE);
            btnConfirmLocation.setText("Confirm Location");
            btnConfirmLocation.setOnClickListener(v -> confirmSelection());
        } else {
            centerMarker.setVisibility(View.GONE);
            btnConfirmLocation.setVisibility(View.GONE);
//            View container = getView().findViewById(R.id.bottomSheetContainer);
//            if(container != null) container.setVisibility(View.GONE);
        }
    }

    private void confirmSelection() {
        if (mapView != null) {
            // Get center from Mapbox Camera State
            Point center = mapView.getMapboxMap().getCameraState().getCenter();

            if (center != null) {
                Bundle result = new Bundle();
                result.putDouble(RESULT_LAT, center.latitude());
                result.putDouble(RESULT_LNG, center.longitude());
                getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);

                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        }
    }

    private void onMapReady() {
        // Set Initial Camera Position
        // Mapbox uses Point (Longitude, Latitude) order
        Point initialPoint = Point.fromLngLat(initialLng, initialLat);

        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(initialPoint)
                .zoom(14.0)
                .build();

        mapView.getMapboxMap().setCamera(cameraOptions);

        if (isSelectionMode) {
            Toast.makeText(getContext(), "Drag map to select location", Toast.LENGTH_SHORT).show();
        } else {
            // VIEW ONLY MODE: Add a marker
            addMarker(initialPoint);
        }
    }

    private void addMarker(Point position) {
        if (viewAnnotationManager == null) return;

        // 1. Create the Annotation Options
        // Using .geometry(position) is standard for v11 SDK to place a view at a coordinate
        ViewAnnotationOptions options = new ViewAnnotationOptions.Builder()
                .annotatedFeature(AnnotatedFeature.valueOf(position))
                .allowOverlap(true)
                .build();

        // 2. Inflate the custom marker layout (using layout from your snippet)
        // Ensure R.layout.layout_marker_avatar exists
        View markerView = viewAnnotationManager.addViewAnnotation(R.layout.layout_marker_avatar, options);

        // 3. Setup the marker view
        // Since this fragment is for a generic location (no user avatar available),
        // we set a default icon or placeholder.
        ImageView markerImg = markerView.findViewById(R.id.marker_avatar);
        if (markerImg != null) {
            markerImg.setImageResource(R.drawable.defaultavt);
        }
    }

    // --- Lifecycle methods required for MapView ---

    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null) mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // viewAnnotationManager does not need explicit destroy call if tied to mapView
        if (mapView != null) mapView.onDestroy();
    }
}
