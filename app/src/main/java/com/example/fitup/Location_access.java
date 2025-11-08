package com.example.fitup;

import android.Manifest;
import android.content.Intent; // Import the Intent class
import android.content.pm.PackageManager;
import android.os.Bundle;
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

public class Location_access extends AppCompatActivity {
    private Button buttonAllowAccess;
    private final String permission = Manifest.permission.ACCESS_FINE_LOCATION;

    // Khai báo Trình khởi chạy (Launcher)
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Permission Granted! Setup complete.", Toast.LENGTH_SHORT).show();
                    goToNextScreen();
                } else {
                    Toast.makeText(this, "Permission Denied. Some features may be limited.", Toast.LENGTH_SHORT).show();
                    goToNextScreen();
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location_access);

        buttonAllowAccess = findViewById(R.id.button_allow_access);

        buttonAllowAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        Location_access.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(Location_access.this, "Permission already granted.", Toast.LENGTH_SHORT).show();
                    goToNextScreen();
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

    private void goToNextScreen() {
        Intent intent = new Intent(Location_access.this, congrats.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}
