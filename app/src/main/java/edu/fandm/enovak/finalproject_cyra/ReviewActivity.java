package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    LinearLayout navActivity;

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


        String selectedPlaceFromItinerary = getIntent().getStringExtra("place_name");

        List<String> visitedPlaces = new ArrayList<>();
        visitedPlaces.add("-- Select a Place --");

        // add itinerary items into spinner
//        visitedPlaces.addAll(ItineraryData.itineraryList);
        visitedPlaces.addAll(UserSessionManager.getInstance().getItineraryList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                visitedPlaces
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlaces.setAdapter(adapter);

        // preselect the tapped place if it exists
        if (selectedPlaceFromItinerary != null) {
            int position = visitedPlaces.indexOf(selectedPlaceFromItinerary);
            if (position >= 0) {
                spinnerPlaces.setSelection(position);
                tvReviewTitle.setText("Review: " + selectedPlaceFromItinerary);
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

        String summary = "Review Submitted for " + selectedPlace + "\n" +
                "Rating: " + starRating + " Stars\n" +
                "Review: \"" + reviewText + "\"";

        Toast.makeText(this, summary, Toast.LENGTH_LONG).show();
    }
}