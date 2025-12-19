package com.example.fitup;

import com.google.firebase.Timestamp;

public class Comment {
    private String text;
    private String userId;
    private String username;
    private String avatarUrl;
    private Timestamp timestamp;

    // Required empty public constructor for Firestore
    public Comment() { }

    public Comment(String text, String userId, String username, String avatarUrl, Timestamp timestamp) {
        this.text = text;
        this.userId = userId;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
