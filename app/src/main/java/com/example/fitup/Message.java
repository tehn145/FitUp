package com.example.fitup;

public class Message {
<<<<<<< HEAD
    private String senderId;
    private String receiverId;
    private String text;
    private long timestamp;
    private boolean showDateHeader = false;

    public Message() {}

    public Message(String senderId, String receiverId, String text, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getSenderId() { return senderId; }
    public String getText() { return text; }
    public long getTimestamp() { return timestamp; }
    public boolean isShowDateHeader() { return showDateHeader; }
    public void setShowDateHeader(boolean showDateHeader) { this.showDateHeader = showDateHeader; }
}
