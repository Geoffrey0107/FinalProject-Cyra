package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

//import com.bumptech.glide.Glide;

public class PlaceDetails extends AppCompatActivity {

    private ImageView placeImage;
    private TextView placeName;
    private TextView placeDescription;
    private Button btnReview;
    private TextView placeLocation;

    private LinearLayout reviewsContainer;
    private TextView textNoReviews;

    private ImageView posterProfileImage;
    private TextView posterName;

    LinearLayout navActivity, navItinerary, navPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details3);


        placeImage = findViewById(R.id.image_place);
        placeName = findViewById(R.id.text_place_name);
        placeDescription = findViewById(R.id.text_place_description);
        btnReview = findViewById(R.id.btn_review);
        posterProfileImage = findViewById(R.id.image_poster_profile);
        posterName = findViewById(R.id.text_poster_name);
        String postUserId = getIntent().getStringExtra("post_user_id");
        String postUsername = getIntent().getStringExtra("post_username");

        placeLocation = findViewById(R.id.text_place_location);
        String country = getIntent().getStringExtra("place_country");
        String state = getIntent().getStringExtra("place_state");
        String city = getIntent().getStringExtra("place_city");

        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);
        reviewsContainer = findViewById(R.id.reviewsContainer);
        if (postUsername != null && !postUsername.isEmpty()) {
            posterName.setText("Posted by " + postUsername);
        }
        if (postUserId != null && !postUserId.isEmpty()) {
            loadPosterProfile(postUserId);
        }


        textNoReviews = findViewById(R.id.text_no_reviews);

        String title = getIntent().getStringExtra("place_title");
        String description = getIntent().getStringExtra("place_description");
        String imageUrl = getIntent().getStringExtra("place_image_url");

        placeName.setText(title);
        placeDescription.setText(description);
        loadReviews(placeName.getText().toString());

        Glide.with(this)
                .load(imageUrl)
                .into(placeImage);

        StringBuilder locationText = new StringBuilder();

        if (city != null && !city.isEmpty()) {
            locationText.append(city);
        }
        if (state != null && !state.isEmpty()) {
            if (locationText.length() > 0) locationText.append(", ");
            locationText.append(state);
        }
        if (country != null && !country.isEmpty()) {
            if (locationText.length() > 0) locationText.append(", ");
            locationText.append(country);
        }

        placeLocation.setText(locationText.toString());
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlaceDetails.this, "Opening Review Page...", Toast.LENGTH_SHORT).show();
            }
        });
        btnReview.setOnClickListener(v -> {
            Intent intent = new Intent(PlaceDetails.this, ReviewActivity.class);
            intent.putExtra("place_name", placeName.getText().toString());
            startActivity(intent);
        });

        navActivity.setOnClickListener(v -> {
            Intent intent = new Intent(PlaceDetails.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        navPost.setOnClickListener(v -> {
            Intent intent = new Intent(PlaceDetails.this, CreatePostActivity.class);
            startActivity(intent);
        });

        navItinerary.setOnClickListener(v -> {
            Intent intent = new Intent(PlaceDetails.this, ItineraryActivity.class);
            startActivity(intent);
        });
    }
    private void loadReviews(String placeTitle) {
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("reviews")
                .whereEqualTo("placeName", placeTitle)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Review> reviewList = new ArrayList<>();

                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        Review review = doc.toObject(Review.class);
                        if (review != null) {
                            reviewList.add(review);
                        }
                    }

                    Log.d("REVIEW_DEBUG", "Loaded " + reviewList.size() + " reviews");

                    reviewsContainer.removeAllViews();

                    if (reviewList.isEmpty()) {
                        textNoReviews.setVisibility(View.VISIBLE);
                    } else {
                        textNoReviews.setVisibility(View.GONE);

                        for (Review review : reviewList) {
                            View reviewView = getLayoutInflater().inflate(R.layout.item_review, reviewsContainer, false);

                            TextView tvReviewText = reviewView.findViewById(R.id.tvReviewText);
                            tvReviewText.setText("⭐ " + review.getRating() + "/5\n" + review.getReviewText());

                            reviewsContainer.addView(reviewView);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    textNoReviews.setVisibility(View.VISIBLE);
                    textNoReviews.setText("Failed to load reviews.");
                });
    }
    private void loadPosterProfile(String userId) {
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String profileImageUrl = doc.getString("profileImageUrl");
                        String username = doc.getString("username");

                        if (username != null && !username.isEmpty()) {
                            posterName.setText("Posted by " + username);
                        }

                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(profileImageUrl)
                                    .circleCrop()
                                    .into(posterProfileImage);
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReviews(placeName.getText().toString());
    }
}
