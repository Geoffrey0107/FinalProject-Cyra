package edu.fandm.enovak.finalproject_cyra;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User {

    private String userId;
    private String username;
    private long timeCreated;
    private boolean showLocation; // NOTE: This should be changed to canCommunicate. Right now, that's how this functions
    private String profilePicture; // URL or null
    private String description;

    // Empty constructor required for Firestore
    public User() {}

    // Constructor
    public User(String userId, String username, long timeCreated, boolean showLocation,
                String profilePicture, String description) {
        this.userId = userId;
        this.username = username;
        this.timeCreated = timeCreated;
        this.showLocation = showLocation;
        this.profilePicture = profilePicture;
        this.description = description;
    }

    // Getters & Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public long getTimeCreated() { return timeCreated; }
    public void setTimeCreated(long timeCreated) { this.timeCreated = timeCreated; }

    public boolean isShowLocation() { return showLocation; }
    public void setShowLocation(boolean showLocation) { this.showLocation = showLocation; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
