package edu.fandm.enovak.finalproject_cyra;

public class Request {

    // place name
    private String place;
    // sender userId
    private String senderId;
    // receiver userId
    private String receiverId;
    // sender email
    private String email;
    // timestamp
    private long timestamp;
    // was request accepted or rejected
    private boolean handled;

    // Default constructor required for Firebase
    public Request() {

    }

    public Request(String place, String senderId, String receiverId, String email, long timestamp) {
        this.place = place;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.email = email;
        this.timestamp = timestamp;
        this.handled = false;
    }

    // Getters and setters
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }

    public String getSenderId() {
        return senderId;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isHandled() {
        return handled;
    }
    public void setHandled(boolean handled) {
        this.handled = handled;
    }
}
