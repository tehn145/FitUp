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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MyNetworkFragment extends Fragment {

    private static final String TAG = "MyNetworkFragment";

    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Use the XML layout you provided
        return inflater.inflate(R.layout.activity_my_network, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

       // View tabsCardView = view.findViewById(R.id.tabs_card_view);
        //if (tabsCardView != null) {
            //tabsCardView.setVisibility(View.GONE);
        //}

        // Also hide the separator if you don't want it, or keep it as a spacer
        View topSep = view.findViewById(R.id.topsep);
        // topSep.setVisibility(View.GONE); // Uncomment if you want to hide the line too

        // 2. Setup RecyclerView
        postsRecyclerView = view.findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        postsRecyclerView.setAdapter(postAdapter);

        // 3. Load Data
        loadPosts();
    }

    private void loadPosts() {
        if (mAuth.getCurrentUser() == null) return;

        // Logic: Fetch posts from the "posts" collection
        // For "My Network", you might want to filter by friends in the future.
        // For now, we fetch all posts ordered by timestamp.
        Query query = db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                //.whereNotEqualTo("userId", mAuth.getCurrentUser().getUid())
                .limit(20);

        query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Listen failed.", e);
                return;
            }

            if (snapshots != null) {
                List<Post> newPosts = new ArrayList<>();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    Post post = doc.toObject(Post.class);
                    if (post != null) {
                        post.setPostId(doc.getId());
                        newPosts.add(post);
                    }
                }

                java.util.Collections.shuffle(newPosts);
                postAdapter.updatePosts(newPosts);
            }
        });
    }
}
