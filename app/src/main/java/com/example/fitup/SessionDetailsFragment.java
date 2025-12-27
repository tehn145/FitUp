package com.example.fitup;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;import android.os.Bundle;
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
import java.text.SimpleDateFormat; // Added import
import java.util.Date; // Added import
import java.util.List;
import java.util.Locale;

public class SessionDetailsFragment extends BottomSheetDialogFragment {

    private static final String ARG_SESSION_ID = "session_id";
    private String sessionId;
    private TextView tvName, tvStatus, tvPrice, tvNote, tvLocation, tvTime;
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
        tvTime = view.findViewById(R.id.tvDateTime);

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

                        Long scheduledTime = documentSnapshot.getLong("scheduledTimestamp");

                        long sHour = (documentSnapshot.getLong("startHour") != null) ? documentSnapshot.getLong("startHour") : -1;
                        long sMin = (documentSnapshot.getLong("startMinute") != null) ? documentSnapshot.getLong("startMinute") : 0;
                        long eHour = (documentSnapshot.getLong("endHour") != null) ? documentSnapshot.getLong("endHour") : -1;
                        long eMin = (documentSnapshot.getLong("endMinute") != null) ? documentSnapshot.getLong("endMinute") : 0;

                        if (scheduledTime != null) {
                            SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                            String dateStr = sdfDate.format(new Date(scheduledTime));

                            String timeRangeStr;
                            if (sHour != -1 && eHour != -1) {
                                String startTime = formatTimeSimple((int) sHour, (int) sMin);
                                String endTime = formatTimeSimple((int) eHour, (int) eMin);
                                timeRangeStr = startTime + " - " + endTime;
                            } else {
                                SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a", Locale.getDefault());
                                timeRangeStr = sdfTime.format(new Date(scheduledTime));
                            }

                            if (tvTime != null) {
                                tvTime.setText(dateStr + " • " + timeRangeStr);
                            } else {
                                tvStatus.append("\n" + dateStr + " • " + timeRangeStr);
                            }
                        }
                        // --------------------------

                        if ("completed".equalsIgnoreCase(status)
                                || "expired".equalsIgnoreCase(status)
                                || "cancelled".equalsIgnoreCase(status)) {
                            btnFinish.setVisibility(View.GONE);
                            btnCancel.setVisibility(View.GONE);
                        } else {
                            btnFinish.setVisibility(View.VISIBLE);
                            btnCancel.setVisibility(View.VISIBLE);
                        }

                        tvNote.setText(documentSnapshot.getString("note"));

                        String existingLocationName = documentSnapshot.getString("locationName");
                        GeoPoint geoPoint = documentSnapshot.getGeoPoint("location");

                        if (existingLocationName != null && !existingLocationName.isEmpty()) {
                            tvLocation.setText(existingLocationName);
                        } else if (geoPoint != null) {
                            tvLocation.setText("Locating...");
                            resolveLocationName(geoPoint.getLatitude(), geoPoint.getLongitude());
                        } else {
                            tvLocation.setText("Online / No Location");
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load session details", Toast.LENGTH_SHORT).show());
    }

    // Helper method for formatting time
    private String formatTimeSimple(int hour, int minute) {
        String amPm = "AM";
        if (hour >= 12) {
            amPm = "PM";
            if (hour > 12) hour -= 12;
        }
        if (hour == 0) hour = 12;
        return String.format(Locale.getDefault(), "%d:%02d %s", hour, minute, amPm);
    }

    private void resolveLocationName(double lat, double lng) {
        // ... (rest of existing code) ...
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    StringBuilder sb = new StringBuilder();
                    if (address.getMaxAddressLineIndex() > 0) {
                        sb.append(address.getAddressLine(0));
                    } else {
                        if (address.getThoroughfare() != null) sb.append(address.getThoroughfare()).append(", ");
                        if (address.getLocality() != null) sb.append(address.getLocality());
                    }

                    String finalAddress = sb.toString();
                    if(finalAddress.isEmpty()) finalAddress = "Unknown Location (" + lat + ", " + lng + ")";

                    String addrToSave = finalAddress;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            tvLocation.setText(addrToSave);
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
