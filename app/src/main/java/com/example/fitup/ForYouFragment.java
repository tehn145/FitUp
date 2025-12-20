package com.example.fitup;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ForYouFragment extends Fragment {

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

        String currentUserId = mAuth.getCurrentUser().getUid();

        // 1. Get current user's friends
        db.collection("users").document(currentUserId)
                .collection("friends")
                .limit(10)
                .get()
                .addOnSuccessListener(friendSnapshots -> {
                    if (friendSnapshots.isEmpty()) {
                        Log.d(TAG, "No friends found, loading general posts.");
                        loadGeneralPosts(currentUserId); // Fallback if no friends
                        return;
                    }

                    // Get all friend UIDs
                    List<String> allFriendIds = new ArrayList<>();
                    for (DocumentSnapshot doc : friendSnapshots.getDocuments()) {
                        // Assuming the document ID is the friend's User ID
                        // Or if stored as a field: doc.getString("friendId");
                        allFriendIds.add(doc.getId());
                    }

                    // 2. Select 'x' random friends (e.g., 5)
                    java.util.Collections.shuffle(allFriendIds);
                    int limit = Math.min(allFriendIds.size(), 5);
                    List<String> selectedFriendIds = allFriendIds.subList(0, limit);

                    fetchPostsFromFriends(selectedFriendIds, currentUserId);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching friends", e));
    }

    private void fetchPostsFromFriends(List<String> friendIds, String currentUserId) {
        List<String> postIdsToFetch = new ArrayList<>();
        // Counter to track when all friend subcollections have been queried
        final int[] completedQueries = {0};

        for (String friendId : friendIds) {
            // 3. Query each friend's "userPosts" subcollection
            db.collection("users").document(friendId).collection("userPosts")
                    .limit(3) // Get top 3 posts per friend
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(userPostsSnapshots -> {
                        for (DocumentSnapshot doc : userPostsSnapshots.getDocuments()) {
                            // Assuming the doc ID in userPosts matches the ID in the main 'posts' collection
                            postIdsToFetch.add(doc.getId());
                        }

                        completedQueries[0]++;

                        // Check if this was the last friend to query
                        if (completedQueries[0] == friendIds.size()) {
                            fetchActualPosts(postIdsToFetch, currentUserId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching friend's posts", e);
                        completedQueries[0]++; // Count failure as complete to avoid hanging
                        if (completedQueries[0] == friendIds.size()) {
                            fetchActualPosts(postIdsToFetch, currentUserId);
                        }
                    });
        }
    }

    private void fetchActualPosts(List<String> postIds, String currentUserId) {
        if (postIds.isEmpty()) {
            loadGeneralPosts(currentUserId);
            return;
        }

        // 4. Fetch the actual post documents from the main "posts" collection
        // Firestore 'in' queries are limited to 10 items, so we might need to batch or loop.
        // For simplicity, we loop here (or use Tasks.whenAllSuccess if you prefer).

        List<Post> finalPosts = new ArrayList<>();
        final int[] fetchedCount = {0};

        for (String postId : postIds) {
            db.collection("posts").document(postId).get()
                    .addOnSuccessListener(doc -> {
                        Post post = doc.toObject(Post.class);
                        if (post != null) {
                            post.setPostId(doc.getId());
                            finalPosts.add(post);
                        }

                        fetchedCount[0]++;
                        checkAndDisplay(fetchedCount[0], postIds.size(), finalPosts, currentUserId);
                    })
                    .addOnFailureListener(e -> {
                        fetchedCount[0]++;
                        checkAndDisplay(fetchedCount[0], postIds.size(), finalPosts, currentUserId);
                    });
        }
    }

    private void checkAndDisplay(int current, int total, List<Post> posts, String currentUserId) {
        if (current == total) {
            // If we didn't get enough friend posts, maybe mix in general ones?
            if (posts.size() < 5) {
                loadGeneralPosts(currentUserId); // Or append to general posts
            } else {
                java.util.Collections.shuffle(posts);
                postAdapter.updatePosts(posts);
            }
        }
    }

    // Fallback method: essentially your original logic
    private void loadGeneralPosts(String currentUserId) {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereNotEqualTo("userId", currentUserId)
                .limit(20)
                .get()
                .addOnSuccessListener(snapshots -> {
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
                });
    }

}
