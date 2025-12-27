package com.example.fitup;

public class Trainer {
    private String uid;
    private String name;
    private String avatar;
    private String primaryGoal;
    private String locationName;
    private String fitnessLevel;
    private long gem;
    private boolean requestSent = false;
    private boolean connected = false;

    private boolean incomingRequest = false;

    public Trainer() {}

    public Trainer(String name, String avatar, String primaryGoal, long gem) {
        this.name = name;
        this.avatar = avatar;
        this.primaryGoal = primaryGoal;
        this.gem = gem;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public String getAvatar() { return avatar; }
    public void setAvatarUrl(String avatar) { this.avatar = avatar; }

    public String getPrimaryGoal() { return primaryGoal; }
    public long getGem() { return gem; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    public String getFitnessLevel() { return fitnessLevel; }
    public void setFitnessLevel(String fitnessLevel) { this.fitnessLevel = fitnessLevel; }

    public boolean isRequestSent() { return requestSent; }
    public void setRequestSent(boolean requestSent) { this.requestSent = requestSent; }
    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }

    public boolean isIncomingRequest() { return incomingRequest; }
    public void setIncomingRequest(boolean incomingRequest) { this.incomingRequest = incomingRequest; }
}