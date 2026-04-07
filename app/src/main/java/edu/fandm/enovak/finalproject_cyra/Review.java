package edu.fandm.enovak.finalproject_cyra;

public class Review {
    private String placeName;
    private int rating;
    private String reviewText;
    private long timestamp;

    public Review() {
    }

    public Review(String placeName, int rating, String reviewText, long timestamp) {
        this.placeName = placeName;
        this.rating = rating;
        this.reviewText = reviewText;
        this.timestamp = timestamp;
    }

    public String getPlaceName() {
        return placeName;
    }

    public int getRating() {
        return rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public long getTimestamp() {
        return timestamp;
    }
}