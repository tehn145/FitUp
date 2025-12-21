package com.example.fitup;

public class User {
    private String name;
    private String role;
    private String avatar;

    private String location;
    private boolean isFollowing;

    public User() {}

    public User(String name, String role, String avatar, String location, boolean isFollowing) {
        this.name = name;
        this.role = role;
        this.avatar = avatar;
        this.location = location;
        this.isFollowing = isFollowing;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
