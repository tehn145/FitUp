package com.example.fitup;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsFragment extends BottomSheetDialogFragment {

    private static final String ARG_POST_ID = "post_id";
    private static final String TAG = "CommentsFragment";

    private String postId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private TextView tvTitle;
    private TextView tvNoComments;
    private EditText etCommentInput;
    private ImageButton btnSend;
    private ImageButton btnClose;
    private RecyclerView rvComments;

    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public static CommentsFragment newInstance(String postId) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
        }
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;

            // FIX 1: Use the correct internal ID from the Material library
            // Look up the ID dynamically to avoid R class issues
            int designBottomSheetId = getResources().getIdentifier("design_bottom_sheet", "id", "com.google.android.material");
            android.widget.FrameLayout bottomSheet = bottomSheetDialog.findViewById(designBottomSheetId);

            if (bottomSheet != null) {
                // Remove default background
                bottomSheet.setBackgroundResource(android.R.color.transparent);

                // FIX 2: Force expanded state
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                // Optional: Ensure it doesn't peek
                behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitle = view.findViewById(R.id.tv_comments_title);
        tvNoComments = view.findViewById(R.id.tv_no_comments);
        etCommentInput = view.findViewById(R.id.et_comment_input);
        btnSend = view.findViewById(R.id.btn_send_comment);
        btnClose = view.findViewById(R.id.btn_close_comments);
        rvComments = view.findViewById(R.id.rv_comments);

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(getContext(), commentList);

        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComments.setAdapter(commentAdapter);

        btnClose.setOnClickListener(v -> dismiss());
        btnSend.setOnClickListener(v -> postComment());

        loadComments();
    }

    private void loadComments() {
        if (postId == null) {
            Log.e(TAG, "Post ID is null, cannot load comments");
            return;
        }

        db.collection("posts").document(postId).collection("comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        commentList.clear();
                        int count = snapshots.size();

                        Log.d(TAG, "Loaded " + count + " comments for post " + postId);
                        tvTitle.setText(count + " Comments");

                        if (count == 0) {
                            tvNoComments.setVisibility(View.VISIBLE);
                            rvComments.setVisibility(View.GONE);
                        } else {
                            tvNoComments.setVisibility(View.GONE);
                            rvComments.setVisibility(View.VISIBLE);

                            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                try {
                                    Comment comment = doc.toObject(Comment.class);
                                    String oldName = comment.getUsername();

                                    if (comment.getUserId() != null) {
                                        db.collection("users").document(comment.getUserId())
                                                .get()
                                                .addOnSuccessListener(userSnap -> {
                                                    if (userSnap.exists()) {
                                                        String currentName = userSnap.getString("name");
                                                        String currentAvatar = userSnap.getString("avatar");

                                                        // Update the model locally
                                                        if (currentName != null) comment.setUsername(currentName);
                                                        if (currentAvatar != null) comment.setAvatarUrl(currentAvatar);

                                                        Log.d(TAG, "NEW NAME: " + currentName);

                                                        // Refresh the specific item in the list
                                                        // (Ideally find the index, but notifyDataSetChanged is safer here)

                                                        if (currentName != null && !currentName.equals(oldName)) {
                                                            commentAdapter.notifyDataSetChanged();
                                                        }

                                                    }
                                                });
                                    }

                                    if (comment != null) {
                                        // Initially add the comment with whatever data is in the document
                                        Log.d(TAG, "NAME: " + comment.getUsername());
                                        commentList.add(comment);

                                        // Now, fetch the latest username/avatar from the 'users' collection
                                        // This ensures we have the correct name even if they changed it
                                        commentAdapter.notifyDataSetChanged();
                                        Log.d(TAG, "Added comment to list: " + comment.getText());
                                    } else {
                                        Log.w(TAG, "Comment object was null for doc: " + doc.getId());
                                    }
                                } catch (Exception ex) {
                                    Log.e(TAG, "Error parsing comment: " + ex.getMessage());
                                }
                            }
                            commentAdapter.notifyDataSetChanged();

                            if (!commentList.isEmpty()) {
                                rvComments.smoothScrollToPosition(commentList.size() - 1);
                            }
                        }
                    }
                });
    }

    private void postComment() {
        // (Same as your previous code)
        String commentText = etCommentInput.getText().toString().trim();
        if (TextUtils.isEmpty(commentText)) return;

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        btnSend.setEnabled(false);

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String username = documentSnapshot.getString("name");
                    String avatarUrl = documentSnapshot.getString("avatar");
                    if (username == null) username = "User";

                    Map<String, Object> commentData = new HashMap<>();
                    commentData.put("text", commentText);
                    commentData.put("userId", currentUser.getUid());
                    commentData.put("username", username);
                    commentData.put("avatarUrl", avatarUrl);
                    commentData.put("timestamp", Timestamp.now());

                    db.collection("posts").document(postId).collection("comments")
                            .add(commentData)
                            .addOnSuccessListener(documentReference -> {
                                etCommentInput.setText("");
                                btnSend.setEnabled(true);
                                db.collection("posts").document(postId)
                                        .update("commentCount", FieldValue.increment(1));
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to send comment", Toast.LENGTH_SHORT).show();
                                btnSend.setEnabled(true);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching user", Toast.LENGTH_SHORT).show();
                    btnSend.setEnabled(true);
                });
    }
}
