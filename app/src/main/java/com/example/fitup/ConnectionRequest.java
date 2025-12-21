package com.example.fitup;

public class ConnectionRequest {
    private String fromUid;
    private String toUid;
    private String status;
    private long timestamp;

    private String senderName;
    private String senderAvatar;
    private String senderRole;
    private String senderLocation;

    public ConnectionRequest() {}

    public String getFromUid() { return fromUid; }
    public void setFromUid(String fromUid) { this.fromUid = fromUid; }

    public String getToUid() { return toUid; }
    public void setToUid(String toUid) { this.toUid = toUid; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }

    public String getSenderLocation() { return senderLocation; }
    public void setSenderLocation(String senderLocation) { this.senderLocation = senderLocation; }
}