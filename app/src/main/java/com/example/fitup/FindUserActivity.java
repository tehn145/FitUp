package com.example.fitup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FindUserActivity extends AppCompatActivity {

    private static final String TAG = "FindUserActivity";

    private FirebaseFirestore db;
    private SearchView searchView;
    private RecyclerView usersRecyclerView;
    private TextView tvNoResults;
    private ImageView btnBack;

    private UserSearchAdapter adapter;
    private List<User> userList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        searchView = findViewById(R.id.searchView);
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        tvNoResults = findViewById(R.id.tv_no_results);
        btnBack = findViewById(R.id.btn_back);

        // Setup RecyclerView
        userList = new ArrayList<>();
        adapter = new UserSearchAdapter(this, userList);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(adapter);

        // Setup Listeners
        setupSearchViewListener();
        btnBack.setOnClickListener(v -> finish()); // Go back to the previous screen
    }

    private void setupSearchViewListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search when user presses enter
                searchForUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search as user types
                if (!TextUtils.isEmpty(newText)) {
                    searchForUsers(newText);
                } else {
                    // Clear results if search text is empty
                    clearResults();
                }
                return true;
            }
        });
    }

    private void searchForUsers(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            clearResults();
            return;
        }

        // Firestore queries are case-sensitive. We search for names that are
        // greater than or equal to the search text and less than the search text + a high-value character.
        // This is a common prefix search pattern in Firestore.
        String endText = searchText + "\uf8ff";

        Query query = db.collection("users")
                .orderBy("name")
                .startAt(searchText)
                .endAt(endText)
                .limit(20); // Limit results to avoid fetching too much data

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    // You might want to add the document ID to your user model if needed later
                    // user.setUid(document.getId());
                    userList.add(user);
                }
                adapter.notifyDataSetChanged();
                updateNoResultsView();
            } else {
                // Handle error
                tvNoResults.setText("Error fetching data");
                tvNoResults.setVisibility(View.VISIBLE);
                usersRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void clearResults() {
        userList.clear();
        adapter.notifyDataSetChanged();
        tvNoResults.setVisibility(View.GONE);
        usersRecyclerView.setVisibility(View.VISIBLE);
    }

    private void updateNoResultsView() {
        if (userList.isEmpty()) {
            tvNoResults.setText("No users found");
            tvNoResults.setVisibility(View.VISIBLE);
            usersRecyclerView.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            usersRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
