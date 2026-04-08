package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    private Spinner spinnerPlaces;
    private RatingBar ratingBar;
    private TextView tvRatingValue;
    private EditText etReviewText;
    private Button btnSubmitReview;
    private TextView tvReviewTitle;

    LinearLayout navActivity,navPost,navSearch,navItinerary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        spinnerPlaces = findViewById(R.id.spinner_places);
        ratingBar = findViewById(R.id.rating_bar);
        tvRatingValue = findViewById(R.id.tv_rating_value);
        etReviewText = findViewById(R.id.et_review_text);
        btnSubmitReview = findViewById(R.id.btn_submit_review);
        tvReviewTitle = findViewById(R.id.tv_review_title);

        navActivity = findViewById(R.id.navActivity);
        navPost = findViewById(R.id.navPost);
        navSearch = findViewById(R.id.navSearch);
        navItinerary = findViewById(R.id.navItinerary);

        String selectedPlace = getIntent().getStringExtra("place_name");

        List<String> visitedPlaces = new ArrayList<>();
        visitedPlaces.add("-- Select a Place --");
        visitedPlaces.addAll(ItineraryData.itineraryList);

        if (selectedPlace != null && !visitedPlaces.contains(selectedPlace)) {
            visitedPlaces.add(selectedPlace);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                visitedPlaces
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlaces.setAdapter(adapter);

        if (selectedPlace != null) {
            int position = visitedPlaces.indexOf(selectedPlace);
            if (position >= 0) {
                spinnerPlaces.setSelection(position);
            }
        }

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            int intRating = (int) rating;
            tvRatingValue.setText(intRating + " / 5 Stars");
        });

        navActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewActivity.this, MainActivity.class);
            startActivity(intent);
        });
        navPost.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewActivity.this, CreatePostActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewActivity.this, SearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });


        btnSubmitReview.setOnClickListener(v -> submitReviewData());
    }

    private void submitReviewData() {
        String selectedPlace = spinnerPlaces.getSelectedItem().toString();
        int starRating = (int) ratingBar.getRating();
        String reviewText = etReviewText.getText().toString().trim();

        if (selectedPlace.equals("-- Select a Place --")) {
            Toast.makeText(this, "Please select a place to review.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (starRating == 0) {
            Toast.makeText(this, "Please provide a star rating.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Please write your review.", Toast.LENGTH_SHORT).show();
            etReviewText.requestFocus();
            return;
        }

        long timestamp = System.currentTimeMillis();

        Review review = new Review(selectedPlace, starRating, reviewText, timestamp);

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("reviews")
                .add(review)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                });
    }
}