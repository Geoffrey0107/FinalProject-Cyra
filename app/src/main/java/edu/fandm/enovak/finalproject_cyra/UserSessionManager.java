package edu.fandm.enovak.finalproject_cyra;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserSessionManager {

    private static UserSessionManager instance;
    private String userId; // null if not logged in
    private String username;
    private ArrayList<String> itineraryList;
    private Boolean openToComms;
    private ArrayList<Request> sentRequests = new ArrayList<Request>();
    private ArrayList<Request> recRequests = new ArrayList<Request>();
    private UserSessionManager() {
        itineraryList = new ArrayList<>();
    }

    public static UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }

    public boolean isLoggedIn() { return userId != null; }

    public String getUserId() { return userId; }
    public void setUserId(String id) { this.userId = id; }

    public String getUsername() { return username; }
    public void setUsername(String name) { this.username = name; }

    public ArrayList<String> getItineraryList() { return itineraryList; }
    public void setItineraryList(ArrayList<String> list) { this.itineraryList = list; }

    public boolean getCommsStatus() {
        if (this.openToComms == null) {
            return false; // default value if not yet set
        }
        return this.openToComms; // auto-unboxes Boolean to boolean safely
    }

    public void setComms(Boolean status) {
        this.openToComms = status;
    }

    public void setSentRequests(ArrayList<Request> requests) {
        this.sentRequests = requests;
    }

    public void setRecRequests(ArrayList<Request> requests) {
        this.recRequests = requests;
    }

    public ArrayList<Request> getSentRequests() {
        return this.sentRequests;
    }

    public ArrayList<Request> getRecRequests() {
        return this.recRequests;
    }

    public void addToItinerary(String item) {
        if (!itineraryList.contains(item)) {
            itineraryList.add(item);
        }
    }

    public void removeFromItinerary(String item) {
        itineraryList.remove(item);
    }
}
