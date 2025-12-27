package com.example.fitup.feature.match.model;

public class MatchProfile {

    // ===== Common =====
    public String userId;
    public String name;

    /**
     * role trong Firestore:
     *  - "trainer"
     *  - "user"
     */
    public String role;

    public Integer age;            // optional
    public String gender;          // male / female / other

    /**
     * Firestore đang dùng:
     *  - locationName
     */
    public String location;

    /**
     * Không thấy trong dataset hiện tại
     * -> optional
     */
    public String time;

    // ===== User fields =====

    /**
     * Firestore đang dùng:
     *  - primaryGoal
     *  Ví dụ: muscle_building, lose_weight
     */
    public String goal;

    public Integer level;          // optional
    public Integer height;         // optional
    public Integer weight;         // optional

    // ===== Trainer fields =====
    public Integer experience;     // years (optional)
    public Double rating;          // 0–5 (optional)

    // ===== Optional =====
    public String bio;
    public String email;
    public String phone;

    public MatchProfile() {}

    // ===== Role helpers =====

    /** Trainer (PT) */
    public boolean isTrainer() {
        return "trainer".equalsIgnoreCase(role);
    }

    /** User (client) */
    public boolean isUser() {
        return "user".equalsIgnoreCase(role);
    }

    /**
     * Giữ lại để KHÔNG làm crash code cũ
     * (alias cho trainer)
     */
    public boolean isCoach() {
        return isTrainer();
    }
}
