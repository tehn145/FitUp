package com.example.fitup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.fitup.models.MRZData;
import com.example.fitup.utils.MRZParser;

import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MRZScannerActivity extends AppCompatActivity {

    private static final String TAG = "MRZScanner";
    private PreviewView previewView;
    private TextView statusText;
    private ExecutorService cameraExecutor;
    private TextRecognizer textRecognizer;
    private boolean isProcessing = false;

    private final ActivityResultLauncher<Intent> nfcReaderLauncher = registerForActivityResult(
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
        setContentView(R.layout.activity_mrz_scanner);

        previewView = findViewById(R.id.previewView);
        statusText = findViewById(R.id.statusText);

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        cameraExecutor = Executors.newSingleThreadExecutor();

        statusText.setText("Position the back of ID card in view");
        startCamera();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera initialization failed", e);
                Toast.makeText(this, "Camera initialization failed", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                processImage(imageProxy);
            }
        });

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private void processImage(@NonNull ImageProxy imageProxy) {
        if (isProcessing) {
            imageProxy.close();
            return;
        }

        @SuppressWarnings("UnsafeOptInUsageError")
        android.media.Image mediaImage = imageProxy.getImage();
        if (mediaImage == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(mediaImage,
                imageProxy.getImageInfo().getRotationDegrees());

        isProcessing = true;

        Task<Text> result = textRecognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String detectedText = visionText.getText();
                    handleDetectedText(detectedText);
                    isProcessing = false;
                    imageProxy.close();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Text recognition failed", e);
                    isProcessing = false;
                    imageProxy.close();
                });
    }

    private void handleDetectedText(String text) {
        MRZData mrzData = MRZParser.parseMRZ(text);

        if (mrzData != null) {
            runOnUiThread(() -> {
                statusText.setText("MRZ detected! Starting NFC read...");
                //Toast.makeText(this, "MRZ code detected successfully!",
                //        Toast.LENGTH_SHORT).show();
            });

            // Wait a moment then go to NFC reader
            new android.os.Handler().postDelayed(() -> {
                Intent intent = new Intent(MRZScannerActivity.this, NFCReaderActivity.class);
                intent.putExtra("documentNumber", mrzData.getDocumentNumber());
                intent.putExtra("dateOfBirth", mrzData.getDateOfBirth());
                intent.putExtra("dateOfExpiry", mrzData.getDateOfExpiry());

                nfcReaderLauncher.launch(intent);
            }, 1000); }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        textRecognizer.close();
    }
}