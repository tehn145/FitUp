package com.example.fitup;

public class Message {

    private String senderId;
    private String receiverId;
    private String text;
    private String type;
    private String sessionId;
    private long timestamp;
    private boolean showDateHeader = false;

    public Message() {}

//    public Message(String senderId, String receiverId, String text, long timestamp) {
//        this.senderId = senderId;
//        this.receiverId = receiverId;
//        this.text = text;
//        this.timestamp = timestamp;
//        this.type = "text";
//        this.sessionId = "";
//    }

    public Message(String senderId, String receiverId, String text, long timestamp, String type) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
        this.type = type;
        this.sessionId = "";
    }

    public Message(String senderId, String receiverId, String text, long timestamp, String type, String sessionId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
        this.type = type;
        this.sessionId = sessionId;
    }

    public String getSenderId() { return senderId; }
    public String getText() { return text; }
    public String getSessionId() { return sessionId; }
    public String getType() { return type; }
    public long getTimestamp() { return timestamp; }
    public boolean isShowDateHeader() { return showDateHeader; }
    public void setShowDateHeader(boolean showDateHeader) { this.showDateHeader = showDateHeader; }
}
