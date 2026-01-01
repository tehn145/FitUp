package com.example.fitup;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class StartScanActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private NfcAdapter nfcAdapter;

    private final ActivityResultLauncher<Intent> mrzScannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                boolean isSuccess = false;

                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    isSuccess = result.getData().getBooleanExtra("IS_SUCCESS", false);
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("IS_SUCCESS", isSuccess);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startscan);

        // Check NFC availability
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Button startButton = findViewById(R.id.btnStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionsAndStart();
            }
        });
    }

    private void checkPermissionsAndStart() {
        // Check if NFC is enabled
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC in your settings", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
            return;
        }

        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            startMRZScanner();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMRZScanner();
            } else {
                Toast.makeText(this, "Camera permission is required to scan MRZ code",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startMRZScanner() {
        Intent intent = new Intent(this, MRZScannerActivity.class);
        mrzScannerLauncher.launch(intent);
    }
}