package com.example.fitup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditPasswordFragment extends Fragment {

    private static final String TAG = "EditPasswordFragment";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private TextInputEditText etOldPassword, etNewPassword, etRetypeNewPassword;
    private Button btnSavePassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the correct layout for editing the password
        return inflater.inflate(R.layout.fragment_edit_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize views from the layout
        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etRetypeNewPassword = view.findViewById(R.id.etRetypeNewPassword);
        btnSavePassword = view.findViewById(R.id.btnSavePassword);

        // Set up the save button's OnClickListener
        btnSavePassword.setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String retypedPassword = etRetypeNewPassword.getText().toString().trim();

        // --- Input Validation ---
        if (oldPassword.isEmpty() || newPassword.isEmpty() || retypedPassword.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(getContext(), "New password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(retypedPassword)) {
            Toast.makeText(getContext(), "New passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(getContext(), "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Firebase Re-authentication and Password Update ---
        // 1. Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPassword);

        // 2. Re-authenticate the user with the old password
        currentUser.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
            if (reauthTask.isSuccessful()) {
                Log.d(TAG, "User re-authenticated successfully.");

                // 3. If re-authentication is successful, update the password
                currentUser.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Log.d(TAG, "User password updated successfully.");
                        Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
                        dismissBottomSheet();
                    } else {
                        Log.e(TAG, "Error updating password.", updateTask.getException());
                        Toast.makeText(getContext(), "Failed to update password. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e(TAG, "Re-authentication failed.", reauthTask.getException());
                Toast.makeText(getContext(), "Re-authentication failed. Please check your old password.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Finds the parent BottomSheetBehavior and sets its state to hidden.
     */
    private void dismissBottomSheet() {
        // Find the hosting activity and then the bottom sheet view
        if (getActivity() != null) {
            View bottomSheet = getActivity().findViewById(R.id.standard_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }
    }
}
