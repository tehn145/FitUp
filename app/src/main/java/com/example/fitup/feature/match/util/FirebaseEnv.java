package com.example.fitup.feature.match.util;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FirebaseEnv {

    // TRUE = Firebase thật, FALSE = emulator
    private static final boolean USE_PROD = true;

    public static void init() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // (Khuyến nghị) tắt cache để tránh dính "offline cache" khi đổi môi trường
        FirebaseFirestoreSettings settings =
                new FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(false)
                        .build();
        db.setFirestoreSettings(settings);

        if (!USE_PROD) {
            // Emulator Firestore (chỉ khi bạn muốn dev local)
            db.useEmulator("10.0.2.2", 8080);
        }
    }
}
