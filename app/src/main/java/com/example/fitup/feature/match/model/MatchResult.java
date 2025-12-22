package com.example.fitup.feature.match.model;

public class MatchResult {
    public MatchProfile profile;
    public int score;
    public String reason;

    public MatchResult(MatchProfile profile, int score, String reason) {
        this.profile = profile;
        this.score = score;
        this.reason = reason;
    }
}
