package com.example.fitup;

public class Trainer {
    private String name;
    private String avatar;
    private String primaryGoal;
    private long gem;
    //Them thuoc tinh vi tri...
    public Trainer() {}

    public Trainer(String name, String avatar, String primaryGoal, long gem) {
        this.name = name;
        this.avatar = avatar;
        this.primaryGoal = primaryGoal;
        this.gem = gem;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getPrimaryGoal() {
        return primaryGoal;
    }

    public void setAvatarUrl(String avatar) {
        this.avatar = avatar;
    }

    public long getGem() {
        return gem;
    }
}
