package com.example.fitup;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class InviteFriendsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView tvCodeValue, tvInvitedCount, tvInvitedCount2, tvHaveCode;
    private String myShortCode;
    private String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invite_friends);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        myUid = currentUser.getUid();

        tvCodeValue = findViewById(R.id.tvCodeValue);
        tvInvitedCount = findViewById(R.id.tvInvitedCount);
        tvInvitedCount2 = findViewById(R.id.tvInvitedCount2);
        tvHaveCode = findViewById(R.id.tvHaveCode);
        ImageView btnCopy = findViewById(R.id.btnCopy);
        MaterialButton btnShare = findViewById(R.id.btnShare);
        ImageView imgbtn_Back = findViewById(R.id.imgbtn_Back);

        setupDescriptionText();
        setupMyReferralCode();
        loadInvitedCount();
        imgbtn_Back.setOnClickListener(v -> finish());

        btnCopy.setOnClickListener(v -> {
            if (myShortCode != null) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("FitUp Code", myShortCode);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Code copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        btnShare.setOnClickListener(v -> {
            if (myShortCode != null) {
                String shareBody = "Join me on FitUp! Enter my code " + myShortCode + " to get started.";
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "FitUp Invitation");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        tvHaveCode.setOnClickListener(v -> showEnterCodeDialog());
    }

    private void setupDescriptionText() {
        TextView tvDescription = findViewById(R.id.tvDescription);
        String content = "Earn 2 FitGem for\neach successful referral!";
        SpannableString spannableString = new SpannableString(content);
        String targetWord = "2 FitGem";
        int startIndex = content.indexOf(targetWord);
        if (startIndex != -1) {
            spannableString.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#EB5E28")),
                    startIndex,
                    startIndex + targetWord.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        tvDescription.setText(spannableString);
    }

    private void setupMyReferralCode() {
        if (myUid.length() >= 6) {
            myShortCode = myUid.substring(0, 6).toUpperCase();
        } else {
            myShortCode = myUid.toUpperCase();
        }
        tvCodeValue.setText(myShortCode);

        DocumentReference userRef = db.collection("users").document(myUid);
        userRef.update("short_code", myShortCode)
                .addOnFailureListener(e -> {
                    userRef.set(java.util.Collections.singletonMap("short_code", myShortCode), com.google.firebase.firestore.SetOptions.merge());
                });
    }

    private void loadInvitedCount() {
        db.collection("users").document(myUid).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                long count = 0;
                if (snapshot.contains("invited_count")) {
                    count = snapshot.getLong("invited_count");
                }
                tvInvitedCount.setText(String.valueOf(count));

                if (snapshot.contains("invited_by")) {
                    //tvHaveCode.setVisibility(View.GONE);
                    tvHaveCode.setText("Referral code applied");
                    tvHaveCode.setEnabled(false);
                }
            }
        });
    }

    private void showEnterCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_enter_code, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        EditText edtCode = dialogView.findViewById(R.id.edtReferralCode);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String enteredCode = edtCode.getText().toString().trim().toUpperCase();
            if (TextUtils.isEmpty(enteredCode)) {
                Toast.makeText(this, "Please enter a code", Toast.LENGTH_SHORT).show();
            } else if (enteredCode.equals(myShortCode)) {
                Toast.makeText(this, "You cannot use your own code", Toast.LENGTH_SHORT).show();
            } else {
                processReferralCode(enteredCode, dialog);
            }
        });

        dialog.show();
    }

    private void processReferralCode(String code, AlertDialog dialog) {
        db.collection("users")
                .whereEqualTo("short_code", code)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Invalid referral code", Toast.LENGTH_SHORT).show();
                    } else {
                        DocumentSnapshot inviterDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String inviterUid = inviterDoc.getId();
                        executeReferralTransaction(inviterUid, dialog);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void executeReferralTransaction(String inviterUid, AlertDialog dialog) {
        DocumentReference myRef = db.collection("users").document(myUid);
        DocumentReference inviterRef = db.collection("users").document(inviterUid);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot mySnapshot = transaction.get(myRef);
            DocumentSnapshot inviterSnapshot = transaction.get(inviterRef);

            if (mySnapshot.contains("invited_by")) {
                throw new FirebaseFirestoreException("User already invited", FirebaseFirestoreException.Code.ABORTED);
            }

            double currentGem = 0;
            if (inviterSnapshot.contains("gem")) {
                currentGem = inviterSnapshot.getDouble("gem");
            }
            long currentInvitedCount = 0;
            if (inviterSnapshot.contains("invited_count")) {
                currentInvitedCount = inviterSnapshot.getLong("invited_count");
            }

            transaction.update(inviterRef, "gem", currentGem + 2);
            transaction.update(inviterRef, "invited_count", currentInvitedCount + 1);

            transaction.update(myRef, "invited_by", inviterUid);

            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Success! Friend received +2 FitGem", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            tvHaveCode.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            if (e instanceof FirebaseFirestoreException && ((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.ABORTED) {
                Toast.makeText(this, "You have already used a referral code.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Transaction failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}