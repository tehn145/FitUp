package com.example.fitup;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostOptionsFragment extends BottomSheetDialogFragment {

    private static final String ARG_POST_ID = "post_id";
    private static final String ARG_OWNER_ID = "owner_id";

    private String postId;
    private String ownerId;

    public PostOptionsFragment() {
        // Required empty public constructor
    }

    public static PostOptionsFragment newInstance(String postId, String ownerId) {
        PostOptionsFragment fragment = new PostOptionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        args.putString(ARG_OWNER_ID, ownerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
            ownerId = getArguments().getString(ARG_OWNER_ID);
        }
    }

    // Fix for white background/corners
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);            if (bottomSheet != null) {
                bottomSheet.setBackgroundResource(android.R.color.transparent);
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Make sure this matches your XML file name
        return inflater.inflate(R.layout.fragment_post_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View btnDelete = view.findViewById(R.id.btnOptionDelete);
        View btnReport = view.findViewById(R.id.btnOptionReport);
        View btnCancel = view.findViewById(R.id.btnCancelOptions);

        // --- REPORT LOGIC (Placeholder) ---
        btnReport.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Report submitted for review", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        // --- DELETE LOGIC ---
        // Check if the current user is the owner
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && ownerId != null && currentUser.getUid().equals(ownerId)) {
            // User OWNS the post -> Show Delete Button
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v -> deletePost());
        } else {
            // User does NOT own the post -> Hide Delete Button
            btnDelete.setVisibility(View.GONE);
        }

        // --- CANCEL LOGIC ---
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void deletePost() {
        if (postId == null || ownerId == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        com.google.firebase.firestore.WriteBatch batch = db.batch();

        batch.delete(db.collection("posts").document(postId));
        batch.delete(db.collection("users").document(ownerId).collection("userPosts").document(postId));

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete post", Toast.LENGTH_SHORT).show();
                });
    }
}
