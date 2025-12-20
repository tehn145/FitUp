package com.example.fitup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestFragment extends Fragment {

    private RecyclerView rvIncoming, rvOutgoing;
    private PendingRequestAdapter incomingAdapter, outgoingAdapter;
    private List<ConnectionRequest> incomingList, outgoingList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public PendingRequestFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pending_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvIncoming = view.findViewById(R.id.rvIncomingRequests);
        rvIncoming.setLayoutManager(new LinearLayoutManager(getContext()));
        incomingList = new ArrayList<>();
        incomingAdapter = new PendingRequestAdapter(getContext(), incomingList, PendingRequestAdapter.TYPE_INCOMING, request -> {
            acceptRequest(request);
        });
        rvIncoming.setAdapter(incomingAdapter);

        rvOutgoing = view.findViewById(R.id.rvOutgoingRequests);
        rvOutgoing.setLayoutManager(new LinearLayoutManager(getContext()));
        outgoingList = new ArrayList<>();
        outgoingAdapter = new PendingRequestAdapter(getContext(), outgoingList, PendingRequestAdapter.TYPE_OUTGOING, null); // No action for outgoing
        rvOutgoing.setAdapter(outgoingAdapter);

        loadIncomingRequests();
        loadOutgoingRequests();
    }

    private void loadIncomingRequests() {
        if (mAuth.getCurrentUser() == null) return;
        String myUid = mAuth.getCurrentUser().getUid();

        db.collection("connect_requests")
                .whereEqualTo("toUid", myUid)
                .whereEqualTo("status", "pending")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    if (snapshots != null) {
                        incomingList.clear();
                        incomingAdapter.notifyDataSetChanged();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            ConnectionRequest req = doc.toObject(ConnectionRequest.class);
                            if (req != null) {
                                fetchUserInfo(req, req.getFromUid(), incomingList, incomingAdapter);
                            }
                        }
                    }
                });
    }

    private void loadOutgoingRequests() {
        if (mAuth.getCurrentUser() == null) return;
        String myUid = mAuth.getCurrentUser().getUid();

        db.collection("connect_requests")
                .whereEqualTo("fromUid", myUid)
                .whereEqualTo("status", "pending")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    if (snapshots != null) {
                        outgoingList.clear();
                        outgoingAdapter.notifyDataSetChanged();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            ConnectionRequest req = doc.toObject(ConnectionRequest.class);
                            if (req != null) {
                                fetchUserInfo(req, req.getToUid(), outgoingList, outgoingAdapter);
                            }
                        }
                    }
                });
    }

    private void fetchUserInfo(ConnectionRequest req, String targetUid, List<ConnectionRequest> list, PendingRequestAdapter adapter) {
        db.collection("users").document(targetUid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        req.setSenderName(document.getString("name"));
                        req.setSenderAvatar(document.getString("avatar"));
                        req.setSenderRole(document.getString("role"));

                        list.add(req);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void acceptRequest(ConnectionRequest request) {
        db.collection("connect_requests")
                .whereEqualTo("fromUid", request.getFromUid())
                .whereEqualTo("toUid", mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(snapshots -> {
                    for(DocumentSnapshot doc : snapshots) {
                        doc.getReference().update("status", "accepted")
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Connected!", Toast.LENGTH_SHORT).show());
                    }
                });
    }
}