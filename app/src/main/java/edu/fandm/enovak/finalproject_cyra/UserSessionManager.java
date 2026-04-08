package edu.fandm.enovak.finalproject_cyra;

import java.util.ArrayList;

public class UserSessionManager {

    private static UserSessionManager instance;
    private String userId; // null if not logged in
    private String username;
    private ArrayList<String> itineraryList;

    // constructor
    private UserSessionManager() {
        itineraryList = new ArrayList<>();
    }

    // gets the instance of UserSessionManager for the current session
    // creates a new one if the session just started
    public static UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }

    // Returns if the user logged in by checking if the userId has been updated
    public boolean isLoggedIn() { return userId != null; }

    // returns the userId
    public String getUserId() { return userId; }
    // sets the user id
    public void setUserId(String id) { this.userId = id; }

    // returns username
    public String getUsername() { return username; }
    // sets the username
    public void setUsername(String name) { this.username = name; }

    // gets the itineraryList
    public ArrayList<String> getItineraryList() { return itineraryList; }
    // sets the itineraryList
    public void setItineraryList(ArrayList<String> list) { this.itineraryList = list; }

    // add to the itinerary
    public void addToItinerary(String item) {
        if (!itineraryList.contains(item)) {
            itineraryList.add(item);
        }
    }

    // removes from the itinerary
    public void removeFromItinerary(String item) {
        itineraryList.remove(item);
    }

    // clears session when logging out
    public void clear() {
        userId = null;
        username = null;
        itineraryList.clear();
        instance = null;
    }
}
