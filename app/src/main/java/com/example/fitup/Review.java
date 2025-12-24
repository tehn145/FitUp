package com.example.fitup;

public class Review {
    private String sessionId;
    private String trainerId;
    private String clientId;private double rating;
    private String comment;
    private long timestamp;

    public Review() { } // Empty constructor for Firestore

    public String getSessionId() { return sessionId; }
    public String getTrainerId() { return trainerId; }
    public String getClientId() { return clientId; }
    public double getRating() { return rating; }
    public String getComment() { return comment; }
    public long getTimestamp() { return timestamp; }
}
