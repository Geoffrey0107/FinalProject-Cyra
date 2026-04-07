package edu.fandm.enovak.finalproject_cyra;

import java.util.ArrayList;
import java.util.HashMap;

public class ReviewData {

    public static HashMap<String, ArrayList<Review>> reviewMap = new HashMap<>();

    public static void addReview(String placeName, Review review) {
        if (!reviewMap.containsKey(placeName)) {
            reviewMap.put(placeName, new ArrayList<>());
        }
        reviewMap.get(placeName).add(review);
    }

    public static ArrayList<Review> getReviews(String placeName) {
        if (!reviewMap.containsKey(placeName)) {
            return new ArrayList<>();
        }
        return reviewMap.get(placeName);
    }
}