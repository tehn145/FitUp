package com.example.fitup;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookSessionActivity extends AppCompatActivity {

    private TextInputEditText etSessionName, etPrice, etNote, etLocationDis;
    private AutoCompleteTextView etLocationType;
    private CalendarView calendarView;
    private String receiverId, receiverName, chatId;

    // To store the final selected location data
    private double selectedLat = 0.0;
    private double selectedLng = 0.0;
    private GeoPoint selectedPos;
    private String finalLocationString = "";

    // To store selected date (default to current time)
    private long selectedTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_session);

        try {
            FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);
            Log.d("Kutaru", "Connected to RTDB Emulator");
        } catch (Exception e) {
            Log.e("Kutaru", "Emulator connection failed or already set: " + e.getMessage());
        }

        receiverId = getIntent().getStringExtra("RECEIVER_ID");
        receiverName = getIntent().getStringExtra("RECEIVER_NAME");

        if (getIntent().hasExtra("CHAT_ID")) {
            chatId = getIntent().getStringExtra("CHAT_ID");
        }

        // Initialize Views
        ImageView btnBack = findViewById(R.id.btnBack);
        etSessionName = findViewById(R.id.etSessionName);
        etPrice = findViewById(R.id.etSessionPrice);
        etNote = findViewById(R.id.etNote);
        etLocationType = findViewById(R.id.etLocation);
        etLocationDis = findViewById(R.id.et_location_dis);
        calendarView = findViewById(R.id.calendarView);

        MaterialButton btnSubmit = findViewById(R.id.btnSubmitSession);

        selectedTimestamp = System.currentTimeMillis();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedTimestamp = calendar.getTimeInMillis();
        });

        String[] locationOptions = {"Use my current location", "Choose on map"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, locationOptions) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.parseColor("#333333"));
                return view;
            }
        };
        etLocationType.setAdapter(adapter);

        etLocationType.setOnItemClickListener((parent, view, position, id) -> {
            String selection = adapter.getItem(position);
            if ("Use my current location".equals(selection)) {
                fetchCurrentUserLocation();
            } else if ("Choose on map".equals(selection)) {
                openMapSelection();
            }
        });

        getSupportFragmentManager().setFragmentResultListener(MapLocationFragment.REQUEST_KEY, this, (requestKey, result) -> {
            selectedLat = result.getDouble(MapLocationFragment.RESULT_LAT);
            selectedLng = result.getDouble(MapLocationFragment.RESULT_LNG);
            finalLocationString = selectedLat + ", " + selectedLng;
            selectedPos = new GeoPoint(selectedLat, selectedLng);
            etLocationDis.setText(finalLocationString);
            etLocationType.dismissDropDown();
        });

        btnBack.setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> submitSession());
    }

    private void fetchCurrentUserLocation() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        GeoPoint geoPoint = documentSnapshot.getGeoPoint("location");
                        if (geoPoint != null) {
                            selectedLat = geoPoint.getLatitude();
                            selectedLng = geoPoint.getLongitude();
                            finalLocationString = selectedLat + ", " + selectedLng;
                            selectedPos = new GeoPoint(selectedLat, selectedLng);
                            etLocationDis.setText(finalLocationString);
                            etLocationType.dismissDropDown();
                        } else {
                            Toast.makeText(this, "No GeoPoint location found in profile.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openMapSelection() {
        double startLat = (selectedLat != 0) ? selectedLat : 10.7769;
        double startLng = (selectedLng != 0) ? selectedLng : 106.7009;
        MapLocationFragment mapFragment = MapLocationFragment.newInstance(true, startLat, startLng);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, mapFragment, "MAP_SELECTION");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void submitSession() {
        String sessionName = etSessionName.getText().toString();
        String priceStr = etPrice.getText().toString();
        String location = etLocationDis.getText().toString();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (sessionName.isEmpty() || priceStr.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 10000) { etPrice.setError("Price is too low"); return; }
            if (price > 100000000) { etPrice.setError("Price is too high"); return; }
        } catch (NumberFormatException e) {
            etPrice.setError("Invalid price format");
            return;
        }

        Calendar currentCal = Calendar.getInstance();
        currentCal.set(Calendar.HOUR_OF_DAY, 0); currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0); currentCal.set(Calendar.MILLISECOND, 0);

        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTimeInMillis(selectedTimestamp);
        selectedCal.set(Calendar.HOUR_OF_DAY, 0); selectedCal.set(Calendar.MINUTE, 0);
        selectedCal.set(Calendar.SECOND, 0); selectedCal.set(Calendar.MILLISECOND, 0);

        if (selectedCal.before(currentCal)) {
            Toast.makeText(this, "Cannot book a session in the past!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("sessionName", sessionName);
        sessionData.put("price", price);
        sessionData.put("location", selectedPos);
        sessionData.put("note", etNote.getText().toString());
        sessionData.put("scheduledTimestamp", selectedTimestamp);
        sessionData.put("senderId", currentUserId);
        sessionData.put("clientId", receiverId);
        sessionData.put("trainerId", currentUserId);
        sessionData.put("status", "pending");
        sessionData.put("timestamp", System.currentTimeMillis());
        sessionData.put("isRated", false);

        FirebaseFirestore.getInstance().collection("sessions")
                .add(sessionData)
                .addOnSuccessListener(documentReference -> {
                    // Send message immediately, pass session ID and Name
                    // IMPORTANT: Do NOT finish() here. Finish inside sendSessionMessage success.
                    sendSessionMessage(documentReference.getId(), sessionName);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void sendSessionMessage(String sessionId, String sessionName) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 1. If chatId is null, generate it safely (fallback)
        if (chatId == null) {
            if (currentUserId.compareTo(receiverId) < 0) {
                chatId = currentUserId + "_" + receiverId;
            } else {
                chatId = receiverId + "_" + currentUserId;
            }
        }

        Log.i("Kutaru", "Sending to ChatID: " + chatId);

        DatabaseReference rtdbRef = FirebaseDatabase.getInstance().getReference();
        long timestamp = System.currentTimeMillis();

        // 2. Prepare Message Data
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("senderId", currentUserId);
        messageMap.put("receiverId", receiverId);
        messageMap.put("content", sessionName);
        messageMap.put("sessionId", sessionId);
        messageMap.put("timestamp", timestamp);
        messageMap.put("type", "session");

        // 3. Prepare Atomic Update (Message + Chat Metadata)
        // This ensures both get written, or neither, and gives us ONE success listener
        String messageId = rtdbRef.child("messages").child(chatId).push().getKey();
        if (messageId == null) return; // Rare error case

        Map<String, Object> globalUpdates = new HashMap<>();
        globalUpdates.put("/messages/" + chatId + "/" + messageId, messageMap);
        globalUpdates.put("/chats/" + chatId + "/lastMessage", "Session Request: " + sessionName);
        globalUpdates.put("/chats/" + chatId + "/lastTimestamp", timestamp);
        globalUpdates.put("/chats/" + chatId + "/lastSenderId", currentUserId);

        rtdbRef.updateChildren(globalUpdates)
                .addOnSuccessListener(aVoid -> {
                    Log.i("Kutaru", "Message sent successfully");
                    Toast.makeText(BookSessionActivity.this, "Session Request Sent!", Toast.LENGTH_SHORT).show();
                    finish(); // Finish activity ONLY after DB write confirms
                })
                .addOnFailureListener(e -> {
                    Log.e("Kutaru", "Failed to send message", e);
                    Toast.makeText(BookSessionActivity.this, "Failed to send message, but session created.", Toast.LENGTH_LONG).show();
                    finish(); // Close anyway so user isn't stuck
                });
    }
}
