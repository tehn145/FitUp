package com.example.fitup;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log; // Import added
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class CommentOptionsFragment extends BottomSheetDialogFragment {

    private static final String TAG = "CommentOptions";
    private static final String ARG_POST_ID = "post_id";
    private static final String ARG_COMMENT_ID = "comment_id";
    private static final String ARG_OWNER_ID = "owner_id";

    private String postId;
    private String commentId;
    private String ownerId;

    public CommentOptionsFragment() {
        // Required empty public constructor
    }

    public static CommentOptionsFragment newInstance(String postId, String commentId, String ownerId) {
        CommentOptionsFragment fragment = new CommentOptionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        args.putString(ARG_COMMENT_ID, commentId);
        args.putString(ARG_OWNER_ID, ownerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
            commentId = getArguments().getString(ARG_COMMENT_ID);
            ownerId = getArguments().getString(ARG_OWNER_ID);
        }

        // Log received arguments
        Log.d(TAG, "onCreate: Initializing fragment with - PostID: " + postId + ", CommentID: " + commentId + ", OwnerID: " + ownerId);
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
        return inflater.inflate(R.layout.fragment_comments_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View btnDelete = view.findViewById(R.id.btnOptionDelete);
        View btnReport = view.findViewById(R.id.btnOptionReport);
        View btnCancel = view.findViewById(R.id.btnCancelOptions);

        // --- REPORT ---
        btnReport.setOnClickListener(v -> {
            Log.d(TAG, "Report button clicked");
            Toast.makeText(getContext(), "Report submitted", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        // --- DELETE ---
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = (currentUser != null) ? currentUser.getUid() : "null";

        Log.d(TAG, "Permission Check: Current User: " + currentUid + " | Owner ID: " + ownerId);

        // Allow delete if User owns the comment
        if (currentUser != null && ownerId != null && currentUser.getUid().equals(ownerId)) {
            Log.d(TAG, "User owns this comment. Showing delete button.");
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v -> deleteComment());
        } else {
            Log.d(TAG, "User does NOT own this comment. Hiding delete button.");
            btnDelete.setVisibility(View.GONE);
        }

        btnCancel.setOnClickListener(v -> {
            Log.d(TAG, "Cancel button clicked");
            dismiss();
        });
    }

    private void deleteComment() {
        if (postId == null || commentId == null) {
            Log.e(TAG, "Cannot delete: PostID or CommentID is null");
            return;
        }

        Log.d(TAG, "Attempting to delete comment: " + commentId + " from post: " + postId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts").document(postId).collection("comments").document(commentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Delete successful in Firestore");

                    // Decrement comment count on parent post
                    db.collection("posts").document(postId)
                            .update("commentCount", FieldValue.increment(-1))
                            .addOnSuccessListener(a -> Log.d(TAG, "Comment count decremented successfully"))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to decrement comment count", e));

                    Toast.makeText(getContext(), "Comment deleted", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete comment from Firestore", e);
                    Toast.makeText(getContext(), "Failed to delete comment", Toast.LENGTH_SHORT).show();
                });
    }
}
