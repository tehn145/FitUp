package com.example.fitup;

import com.google.firebase.firestore.GeoPoint;

public class User {
    private String userId; // Or uid
    private String name;
    private String avatar;
    private String role;
    private String locationName;

    public User() {}

    public String getUid() { return userId; }
    public void setUid(String userId) { this.userId = userId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
