package com.example.fitup.feature.match.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitup.R;
import com.example.fitup.feature.match.engine.AIMatchEngine;
import com.example.fitup.feature.match.model.MatchProfile;
import com.example.fitup.feature.match.model.MatchResult;

import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends AppCompatActivity {

    private RecyclerView rvMatches;
    private EditText etQuery;

    // nếu layout của bạn có NestedScrollView ngoài cùng thì lấy nó để auto scroll xuống kết quả
    private NestedScrollView scrollRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Optional: nếu file activity_match.xml root là NestedScrollView (như bạn gửi)
        // thì bạn thêm id cho nó: android:id="@+id/scrollRoot"
        // rồi bật dòng dưới:
        // scrollRoot = findViewById(R.id.scrollRoot);

        rvMatches = findViewById(R.id.rvMatches);
        rvMatches.setLayoutManager(new LinearLayoutManager(this));
        rvMatches.setVisibility(View.GONE);

        etQuery = findViewById(R.id.etQuery);

        // Search button
        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            String q = etQuery.getText() == null ? "" : etQuery.getText().toString().trim();
            if (TextUtils.isEmpty(q)) {
                Toast.makeText(this, "Nhập từ khóa để AI tìm trainer nhé", Toast.LENGTH_SHORT).show();
                return;
            }
            runMatch(q);
        });

        // Banner buttons
        findViewById(R.id.btnConnectNow).setOnClickListener(v -> runMatch("trainer"));
        findViewById(R.id.btnFindNow).setOnClickListener(v -> runMatch("buddy"));

        // Chips
        findViewById(R.id.chipGym).setOnClickListener(v -> runMatch("gym"));
        findViewById(R.id.chipWeightLoss).setOnClickListener(v -> runMatch("weightloss"));
        findViewById(R.id.chipUnknown).setOnClickListener(v -> runMatch("unknown"));
    }

    private void runMatch(String query) {
        // Demo: user hiện tại
        MatchProfile me = new MatchProfile("me", "You", "lose_weight", 2, "evening", 22);

        // Demo: danh sách ứng viên
        List<MatchProfile> candidates = new ArrayList<>();
        candidates.add(new MatchProfile("u1", "Alex", "lose_weight", 2, "evening", 24));
        candidates.add(new MatchProfile("u2", "Emma", "lose_weight", 1, "evening", 21));
        candidates.add(new MatchProfile("u3", "Mia", "cardio", 2, "evening", 23));
        candidates.add(new MatchProfile("u4", "John", "gain_muscle", 3, "morning", 30));

        List<MatchResult> results = AIMatchEngine.match(me, candidates);

        // top 3
        if (results.size() > 3) results = results.subList(0, 3);

        rvMatches.setVisibility(View.VISIBLE);
        rvMatches.setAdapter(new MatchAdapter(this, results));

        // Auto scroll xuống list kết quả (nếu bạn có scrollRoot)
        if (scrollRoot != null) {
            scrollRoot.post(() -> scrollRoot.smoothScrollTo(0, rvMatches.getTop()));
        }

        // Log/Toast để test nhanh
        Toast.makeText(this, "AI matched: " + results.size() + " results for \"" + query + "\"", Toast.LENGTH_SHORT).show();
    }
}
