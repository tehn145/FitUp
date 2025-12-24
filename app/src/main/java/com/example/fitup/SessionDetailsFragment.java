package com.example.fitup;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SessionDetailsFragment extends BottomSheetDialogFragment {

    private static final String ARG_SESSION_ID = "session_id";
    private String sessionId;
    private TextView tvName, tvStatus, tvPrice, tvNote, tvLocation; // Added tvLocation
    private MaterialButton btnFinish, btnCancel;

    public static SessionDetailsFragment newInstance(String sessionId) {
        SessionDetailsFragment fragment = new SessionDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SESSION_ID, sessionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sessionId = getArguments().getString(ARG_SESSION_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;

            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackgroundResource(android.R.color.transparent);
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tvSessionName);
        tvStatus = view.findViewById(R.id.tvSessionStatus);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvNote = view.findViewById(R.id.tvNote);
        tvLocation = view.findViewById(R.id.tvLocation);
        btnFinish = view.findViewById(R.id.btnFinishSession);
        btnCancel = view.findViewById(R.id.btnCancelSession);

        loadSessionData();

        btnFinish.setOnClickListener(v -> updateStatus("completed"));
        btnCancel.setOnClickListener(v -> updateStatus("cancelled"));
    }

    private void loadSessionData() {
        FirebaseFirestore.getInstance().collection("sessions").document(sessionId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("sessionName");
                        tvName.setText(title);

                        Double price = documentSnapshot.getDouble("price");
                        tvPrice.setText(price != null ? String.format("%.0f VND", price) : "N/A");

                        String status = documentSnapshot.getString("status");
                        tvStatus.setText(status != null ? status.toUpperCase() : "UNKNOWN");

                        tvNote.setText(documentSnapshot.getString("note"));

                        String existingLocationName = documentSnapshot.getString("locationName");
                        GeoPoint geoPoint = documentSnapshot.getGeoPoint("location");

                        if (existingLocationName != null && !existingLocationName.isEmpty()) {
                            tvLocation.setText(existingLocationName);
                        } else if (geoPoint != null) {
                            tvLocation.setText("Locating..."); // Temporary text
                            resolveLocationName(geoPoint.getLatitude(), geoPoint.getLongitude());
                        } else {
                            tvLocation.setText("Online / No Location");
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load session details", Toast.LENGTH_SHORT).show());
    }

    private void resolveLocationName(double lat, double lng) {
        // Run Geocoder in a background thread or async manner is best practice,
        // but simple Geocoder works on UI thread for small tasks often.
        // Ideally, use a Thread or Executor.
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    // Build a string: "123 Main St, City"
                    StringBuilder sb = new StringBuilder();
                    if (address.getMaxAddressLineIndex() > 0) {
                        sb.append(address.getAddressLine(0));
                    } else {
                        // Fallback components
                        if (address.getThoroughfare() != null) sb.append(address.getThoroughfare()).append(", ");
                        if (address.getLocality() != null) sb.append(address.getLocality());
                    }

                    String finalAddress = sb.toString();
                    if(finalAddress.isEmpty()) finalAddress = "Unknown Location (" + lat + ", " + lng + ")";

                    // Update UI on Main Thread
                    String addrToSave = finalAddress;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            tvLocation.setText(addrToSave);
                            // SAVE BACK TO FIRESTORE
                            saveLocationToFirestore(addrToSave);
                        });
                    }
                } else {
                    if (getActivity() != null) getActivity().runOnUiThread(() -> tvLocation.setText("Location not found"));
                }
            } catch (IOException e) {
                Log.e("SessionDetails", "Geocoder failed", e);
                if (getActivity() != null) getActivity().runOnUiThread(() -> tvLocation.setText("Error loading address"));
            }
        }).start();
    }

    private void saveLocationToFirestore(String locationName) {
        FirebaseFirestore.getInstance().collection("sessions").document(sessionId)
                .update("locationName", locationName)
                .addOnSuccessListener(aVoid -> Log.d("SessionDetails", "Location name cached successfully."))
                .addOnFailureListener(e -> Log.e("SessionDetails", "Failed to cache location name", e));
    }

    private void updateStatus(String newStatus) {
        FirebaseFirestore.getInstance().collection("sessions").document(sessionId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Session " + newStatus, Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error updating session", Toast.LENGTH_SHORT).show()
                );
    }
}
