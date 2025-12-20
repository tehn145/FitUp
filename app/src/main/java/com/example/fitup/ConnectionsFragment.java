package com.example.fitup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConnectionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ConnectionsAdapter adapter;
    private List<ConnectionRequest> connectionList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Set<String> addedIds = new HashSet<>();

    public ConnectionsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connections, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.connections_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        connectionList = new ArrayList<>();
        adapter = new ConnectionsAdapter(getContext(), connectionList);
        recyclerView.setAdapter(adapter);

        loadConnections();
    }

    private void loadConnections() {
        if (mAuth.getCurrentUser() == null) return;
        String myUid = mAuth.getCurrentUser().getUid();

        connectionList.clear();
        addedIds.clear();
        adapter.notifyDataSetChanged();

        db.collection("connect_requests")
                .whereEqualTo("toUid", myUid)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnSuccessListener(snapshots -> {
                    for (DocumentSnapshot doc : snapshots) {
                        ConnectionRequest req = doc.toObject(ConnectionRequest.class);
                        if (req != null && !addedIds.contains(doc.getId())) {
                            addedIds.add(doc.getId());
                            fetchUserInfo(req, req.getFromUid());
                        }
                    }
                });

        db.collection("connect_requests")
                .whereEqualTo("fromUid", myUid)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnSuccessListener(snapshots -> {
                    for (DocumentSnapshot doc : snapshots) {
                        ConnectionRequest req = doc.toObject(ConnectionRequest.class);
                        if (req != null && !addedIds.contains(doc.getId())) {
                            addedIds.add(doc.getId());
                            fetchUserInfo(req, req.getToUid());
                        }
                    }
                });
    }

    private void fetchUserInfo(ConnectionRequest req, String targetUid) {
        db.collection("users").document(targetUid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        req.setSenderName(document.getString("name"));
                        req.setSenderAvatar(document.getString("avatar"));
                        req.setSenderRole(document.getString("role"));

                        connectionList.add(req);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}