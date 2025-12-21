package com.example.fitup;
import java.util.Map;

public class ChatSummary {
    public String lastMessage;
    public long lastTimestamp;

    public String lastSenderId;
    public boolean isRead;

    public Map<String, Boolean> members;
    public Map<String, String> memberNames;
    public String chatId;

    public ChatSummary() {}

    public String getOtherUserName(String myId) {
        if (memberNames != null) {
            for (String uid : memberNames.keySet()) {
                if (!uid.equals(myId)) return memberNames.get(uid);
            }
        }
        return "User";
    }

    public String getOtherUserId(String myId) {
        if (members != null) {
            for (String uid : members.keySet()) {
                if (!uid.equals(myId)) return uid;
            }
        }
        return null;
    }
}