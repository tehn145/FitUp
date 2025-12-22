package com.example.fitup.feature.match.model;

public class MatchProfile {
    public String userId;
    public String name;
    public String goal;          // lose_weight / gain_muscle / cardio
    public int level;            // 1-3
    public String availability;  // morning / evening
    public int age;

    public MatchProfile() {}

    public MatchProfile(String userId, String name, String goal,
                        int level, String availability, int age) {
        this.userId = userId;
        this.name = name;
        this.goal = goal;
        this.level = level;
        this.availability = availability;
        this.age = age;
    }
}
