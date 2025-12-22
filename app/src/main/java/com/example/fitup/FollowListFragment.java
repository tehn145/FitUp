package com.example.fitup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FollowListFragment extends Fragment {

    private static final String ARG_TYPE = "type"; // "following" or "followers"
    private static final String ARG_USER_ID = "userId";

    private String type;
    private String userId;
    private RecyclerView recyclerView;
    private UserListAdapter adapter;
    private List<User> userList;
    private FirebaseFirestore db;

    public FollowListFragment() {
        // Required empty public constructor
    }

    public static FollowListFragment newInstance(String type, String userId) {
        FollowListFragment fragment = new FollowListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
            userId = getArguments().getString(ARG_USER_ID);
        }
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_following, container, false);
        recyclerView = view.findViewById(R.id.rvFollowing);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        adapter = new UserListAdapter(getContext(), userList, type, userId);
        recyclerView.setAdapter(adapter);

        loadData();

        return view;
    }

    private void loadData() {
        if (userId == null || type == null) return;

        db.collection("users").document(userId).collection(type)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            User user = doc.toObject(User.class);

                            if (user != null) {
                                if (doc.contains("locationName")) {
                                    user.setLocationName(doc.getString("locationName"));
                                }
                                user.setUserId(doc.getId());

                                userList.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FollowListFragment", "Error loading data", e);
                    if(getContext() != null)
                        Toast.makeText(getContext(), "Error loading list", Toast.LENGTH_SHORT).show();
                });
    }
}
