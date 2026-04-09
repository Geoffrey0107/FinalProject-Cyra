package edu.fandm.enovak.finalproject_cyra;
public class Post {

    private String title;
    private String description;
    private String country;
    private String state;
    private String city;
    private String imageUrl;
    private String userId;
    private String username;
    private long timestamp;

    public Post() {}

    public Post(String title, String description, String country, String state, String city,
                String imageUrl, String userId, String username, long timestamp) {
        this.title = title;
        this.description = description;
        this.country = country;
        this.state = state;
        this.city = city;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.username = username;
        this.timestamp = timestamp;
    }

    // getters (important for adapter)
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCountry() { return country; }
    public String getState() { return state; }
    public String getCity() { return city; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getUserId() { return userId; }
    public long getTimestamp() { return timestamp; }
    public String getUsername() { return username; }
}