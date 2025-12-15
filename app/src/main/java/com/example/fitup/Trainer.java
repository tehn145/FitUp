package com.example.fitup;

public class Trainer {
    private String name;
    private String avatarUrl;
    private String primaryGoal;
    private long gem;

    public Trainer() {}

    public Trainer(String name, String avatarUrl, String primaryGoal, long gem) {
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.primaryGoal = primaryGoal;
        this.gem = gem;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getPrimaryGoal() {
        return primaryGoal;
    }

    public long getGem() {
        return gem;
    }
}
