package com.example.fitup.feature.match.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitup.R;
import com.example.fitup.feature.match.engine.AIMatchEngine;
import com.example.fitup.feature.match.model.MatchProfile;
import com.example.fitup.feature.match.model.MatchResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends AppCompatActivity {

    private static final String EMULATOR_HOST = "10.101.25.210";
    private static final int FIRESTORE_PORT = 8080;
    private static final int AUTH_PORT = 9099;

    private static final String ROLE_TRAINER = "trainer";

    private RecyclerView rvMatches;
    private EditText etQuery;
    private NestedScrollView scrollRoot;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        auth = FirebaseAuth.getInstance();
        useAuthEmulatorSafe(auth);

        db = FirebaseFirestore.getInstance();
        setupFirestoreEmulatorSafe(db);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Nếu layout có NestedScrollView id thì bật lại:
        // scrollRoot = findViewById(R.id.scrollRoot);

        rvMatches = findViewById(R.id.rvMatches);
        rvMatches.setLayoutManager(new LinearLayoutManager(this));
        rvMatches.setVisibility(View.GONE);

        etQuery = findViewById(R.id.etQuery);

        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            String q = etQuery.getText() == null ? "" : etQuery.getText().toString().trim();
            if (TextUtils.isEmpty(q)) {
                Toast.makeText(this, "Nhập từ khóa để AI tìm trainer nhé", Toast.LENGTH_SHORT).show();
                return;
            }
            runMatchFromFirestore(q);
        });

        findViewById(R.id.btnConnectNow).setOnClickListener(v -> runMatchFromFirestore("trainer"));
        findViewById(R.id.btnFindNow).setOnClickListener(v -> runMatchFromFirestore("buddy"));

        findViewById(R.id.chipGym).setOnClickListener(v -> runMatchFromFirestore("gym"));
        findViewById(R.id.chipWeightLoss).setOnClickListener(v -> runMatchFromFirestore("lose_weight"));
        findViewById(R.id.chipUnknown).setOnClickListener(v -> runMatchFromFirestore("unknown"));
    }

    // ----------------- Emulator setup -----------------

    private void useAuthEmulatorSafe(FirebaseAuth auth) {
        try { auth.useEmulator(EMULATOR_HOST, AUTH_PORT); }
        catch (Exception ignored) {}
    }

    private void setupFirestoreEmulatorSafe(FirebaseFirestore db) {
        try {
            db.useEmulator(EMULATOR_HOST, FIRESTORE_PORT);
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build();
            db.setFirestoreSettings(settings);
        } catch (Exception ignored) {}
    }

    // ----------------- Main flow -----------------

    private void runMatchFromFirestore(String query) {
        String uid = auth.getUid();
        if (uid == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1) Load ME
        db.collection("users").document(uid).get()
                .addOnSuccessListener(meDoc -> {
                    MatchProfile me = mapDocToProfile(meDoc);
                    if (me == null) {
                        Toast.makeText(this,
                                "Không thấy profile của bạn tại users/" + uid +
                                        "\n→ Hãy tạo document users/" + uid + " trong Firestore emulator",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    // 2) Load TRAINERS
                    db.collection("users")
                            .whereEqualTo("role", ROLE_TRAINER)
                            .get()
                            .addOnSuccessListener(qs -> onLoadedTrainers(uid, me, query, qs))
                            .addOnFailureListener(e ->
                                    Toast.makeText(this,
                                            "Load trainer lỗi: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Load user lỗi: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    private void onLoadedTrainers(String uid, MatchProfile me, String query, QuerySnapshot qs) {
        // Debug nhanh: xem query role=trainer có ra không
        Toast.makeText(this, "trainer docs=" + qs.size(), Toast.LENGTH_SHORT).show();

        List<MatchProfile> trainers = new ArrayList<>();
        for (DocumentSnapshot d : qs.getDocuments()) {
            if (uid.equals(d.getId())) continue; // loại chính mình
            MatchProfile p = mapDocToProfile(d);
            if (p != null && p.isTrainer()) trainers.add(p);
        }

        if (trainers.isEmpty()) {
            Toast.makeText(this, "Chưa có trainer khác trong hệ thống!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3) Try AI engine
        List<MatchResult> results = null;
        try {
            results = AIMatchEngine.match(me, trainers);
        } catch (Exception ignored) {}

        // 4) Fallback nếu engine trả rỗng/null
        if (results == null || results.isEmpty()) {
            results = toSimpleResults(trainers);
            Toast.makeText(this,
                    "AI engine chưa đủ dữ liệu, hiển thị trainer (fallback).",
                    Toast.LENGTH_SHORT).show();
        }

        if (results.size() > 3) results = results.subList(0, 3);

        // 5) Show
        rvMatches.setVisibility(View.VISIBLE);
        rvMatches.setAdapter(new MatchAdapter(this, results));

        if (scrollRoot != null) {
            scrollRoot.post(() -> scrollRoot.smoothScrollTo(0, rvMatches.getTop()));
        }

        Toast.makeText(this,
                "Matched " + results.size() + " trainer for: " + query,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Fallback list -> MatchResult để UI luôn có dữ liệu hiển thị
     * (Không phụ thuộc AIMatchEngine)
     */
    private List<MatchResult> toSimpleResults(List<MatchProfile> trainers) {
        List<MatchResult> out = new ArrayList<>();

        int score = 80; // điểm giả cho đẹp
        for (MatchProfile t : trainers) {
            out.add(new MatchResult(
                    t,
                    score,
                    "Trainer available"
            ));
            score -= 5;
        }

        return out;
    }

    // ----------------- Mapper (schema Firestore hiện tại) -----------------

    private MatchProfile mapDocToProfile(DocumentSnapshot d) {
        if (d == null || !d.exists()) return null;

        MatchProfile p = new MatchProfile();
        p.userId = d.getId();

        p.name = getString(d, "name", "Unknown");
        p.role = getString(d, "role", "user");

        // ✅ keys đúng của bạn
        p.location = getString(d, "locationName", "");
        p.goal = getString(d, "primaryGoal", "");

        p.weight = getIntObj(d, "weight");
        p.phone = getString(d, "phone", "");

        // optional
        p.age = getIntObj(d, "age");
        p.height = getIntObj(d, "height");
        p.level = getIntObj(d, "level");
        p.gender = getString(d, "gender", "");
        p.time = getString(d, "time", "");

        p.experience = getIntObj(d, "experience");
        p.rating = getDoubleObj(d, "rating");

        p.bio = getString(d, "bio", "");
        p.email = getString(d, "email", "");

        return p;
    }

    private String getString(@NonNull DocumentSnapshot d, String key, String def) {
        Object o = d.get(key);
        if (o == null) return def;
        String s = String.valueOf(o);
        return s.isEmpty() ? def : s;
    }

    private Integer getIntObj(@NonNull DocumentSnapshot d, String key) {
        Object o = d.get(key);
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.parseInt(String.valueOf(o)); }
        catch (Exception ignored) {}
        return null;
    }

    private Double getDoubleObj(@NonNull DocumentSnapshot d, String key) {
        Object o = d.get(key);
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(String.valueOf(o)); }
        catch (Exception ignored) {}
        return null;
    }
}
