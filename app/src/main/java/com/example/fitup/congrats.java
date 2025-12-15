package com.example.fitup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView; // Import TextView

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Import Firebase components
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class congrats extends AppCompatActivity {
    private static final int GOTO_HOME_DELAY = 3000;
    private static final String TAG = "CongratsActivity";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView txtCongrats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_congrats);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        txtCongrats = findViewById(R.id.txtCongrats);

        loadUserName();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                goToHomeScreen();
            }
        }, GOTO_HOME_DELAY);
    }

    /**
     * Fetches the current user's data from Firestore and updates the TextView.
     */
    private void loadUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "No user is logged in. Cannot fetch name.");
            // Optionally set a default text if no user is found
            txtCongrats.setText("Congratulations!");
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Get the "name" field from the document
                    String userName = document.getString("name");

                    // Check if the name exists and update the TextView
                    if (userName != null && !userName.isEmpty()) {
                        txtCongrats.setText("Congratulations, " + userName + "!");
                    } else {
                        // Fallback if the name field is empty or doesn't exist
                        txtCongrats.setText("Congratulations!");
                    }
                } else {
                    Log.d(TAG, "No such document");
                    txtCongrats.setText("Congratulations!");
                }
            } else {
                Log.e(TAG, "get failed with ", task.getException());
                txtCongrats.setText("Congratulations!");
            }
        });
    }

    private void goToHomeScreen() {
        Intent intent = new Intent(congrats.this, MainView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
