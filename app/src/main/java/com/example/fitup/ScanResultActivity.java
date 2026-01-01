package com.example.fitup;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.fitup.models.PersonalInfo;
import com.example.fitup.utils.NFCHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ScanResultActivity extends AppCompatActivity {

    private ImageView photoImageView;
    private TextView nameText;
    private TextView documentNumberText;
    private TextView dateOfBirthText;
    private TextView dateOfExpiryText;
    private TextView nationalityText;
    private TextView genderText;

    // New UI references for Success/Failure logic
    private CardView photoSection;
    private CardView infoSection;
    private ImageView imgVerificationStatus;
    private TextView tvVerificationStatus;

    boolean isSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Initialize views
        photoImageView = findViewById(R.id.photoImageView);
        nameText = findViewById(R.id.nameText);
        documentNumberText = findViewById(R.id.documentNumberText);
        dateOfBirthText = findViewById(R.id.dateOfBirthText);
        dateOfExpiryText = findViewById(R.id.dateOfExpiryText);
        nationalityText = findViewById(R.id.nationalityText);
        genderText = findViewById(R.id.genderText);

        photoSection = findViewById(R.id.photoSection);
        infoSection = findViewById(R.id.infoSection);

        imgVerificationStatus = findViewById(R.id.imgVerificationStatus);
        tvVerificationStatus = findViewById(R.id.tvVerificationStatus);

        Button closeButton = findViewById(R.id.btnClose);
        closeButton.setOnClickListener(v -> setResultAndFinish());

        isSuccess = getIntent().getBooleanExtra("IS_SUCCESS", false);

        if (isSuccess) {
            handleSuccess();
        } else {
            String errorMessage = getIntent().getStringExtra("ERROR_MESSAGE");
            handleFailure(errorMessage);
        }
    }

    private void setResultAndFinish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("IS_SUCCESS", isSuccess);
        setResult(RESULT_OK, resultIntent);
        Log.d("ScanResult", "Setting result and finishing");
        finish();
    }

    private void handleSuccess() {

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        PersonalInfo info = (PersonalInfo) getIntent().getSerializableExtra("personalInfo");

        if (info != null) {
            displayInfo(info);
        }

        String scannedDocNumber = info.getDocumentNumber();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (currentUserId == null) {
            isSuccess = false;
            handleFailure("Error: User not logged in.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("documentIds")
                .whereEqualTo("docNumber", scannedDocNumber)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean isDuplicate = false;

                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot doc : querySnapshot) {
                            //if (!doc.getId().equals(currentUserId)) {
                                isDuplicate = true;
                                break;
                            //}
                        }
                    }

                    if (isDuplicate) {
                        handleFailure("Document already verified in another account!");
                    } else {

                        if (userId != null) {
                            FirebaseFirestore.getInstance().collection("users").document(userId)
                                    .update("isVerified", true)
                                    .addOnSuccessListener(aVoid -> Log.d("ScanResult", "User verified in Firestore"))
                                    .addOnFailureListener(e -> Log.e("ScanResult", "Failed to update verification", e));

                            java.util.Map<String, Object> docData = new java.util.HashMap<>();
                            docData.put("docNumber", scannedDocNumber);

                            FirebaseFirestore.getInstance().collection("documentIds").document(userId)
                                    .set(docData, com.google.firebase.firestore.SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> Log.d("ScanResult", "ID stored in Firestore"))
                                    .addOnFailureListener(e -> Log.e("ScanResult", "Failed to store ID", e));                        }

                        if (photoSection != null) photoSection.setVisibility(View.VISIBLE);
                        if (infoSection != null) infoSection.setVisibility(View.VISIBLE);

                        if (tvVerificationStatus != null) {
                            tvVerificationStatus.setText("VERIFIED");
                            tvVerificationStatus.setTextColor(ContextCompat.getColor(this, R.color.green)); // Or specific hex/color
                        }
                        if (imgVerificationStatus != null) {
                            imgVerificationStatus.setColorFilter(ContextCompat.getColor(this, R.color.green));
                        }
                        if (tvVerificationStatus != null) {
                            tvVerificationStatus.setText("Identity matches database records.");
                        }
                    }
                })

                .addOnFailureListener(e -> {
                    Log.e("ScanResult", "Database check failed", e);
                    handleFailure("Network error checking database.");
                });
    }

    private void handleFailure(String errorMessage) {
        isSuccess = false;

        if (photoSection != null) photoSection.setVisibility(View.GONE);
        if (infoSection != null) infoSection.setVisibility(View.GONE);

        if (imgVerificationStatus != null) {
            imgVerificationStatus.setColorFilter(Color.RED);
        }

        if (tvVerificationStatus != null) {
            tvVerificationStatus.setText(errorMessage != null ? errorMessage : "Unknown error occurred.");
            tvVerificationStatus.setTextColor(Color.RED); // Make error details readable
        }
    }

    private void displayInfo(PersonalInfo info) {
        // Display photo if available
        if (info.getPhotoData() != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        info.getPhotoData(),
                        0,
                        info.getPhotoData().length
                );
                photoImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                // Photo decoding failed, keep placeholder
            }
        }

        // Display text information
        nameText.setText(NFCHelper.formatName(info.getName()));
        documentNumberText.setText(info.getDocumentNumber());
        dateOfBirthText.setText(info.getFormattedDateOfBirth());
        dateOfExpiryText.setText(info.getFormattedDateOfExpiry());
        nationalityText.setText(info.getNationality());

        // Format gender
        String gender = info.getGender();
        if (gender != null) {
            switch (gender.toUpperCase()) {
                case "M":
                    genderText.setText("Male");
                    break;
                case "F":
                    genderText.setText("Female");
                    break;
                default:
                    genderText.setText(gender);
                    break;
            }
        }
    }
}
