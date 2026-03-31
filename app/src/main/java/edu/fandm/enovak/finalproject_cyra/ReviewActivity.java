package edu.fandm.enovak.finalproject_cyra;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize UI Elements
        spinnerPlaces = findViewById(R.id.spinner_places);
        ratingBar = findViewById(R.id.rating_bar);
        tvRatingValue = findViewById(R.id.tv_rating_value);
        etReviewText = findViewById(R.id.et_review_text);
        btnSubmitReview = findViewById(R.id.btn_submit_review);

        // --- 1. SET UP THE SPINNER (DROPDOWN MENU) ---

        // Example data: A list of places the user "has visited"
        List<String> visitedPlaces = new ArrayList<>();
        visitedPlaces.add("-- Select a Place --"); // Prompt text
        visitedPlaces.add("Eiffel Tower");
        visitedPlaces.add("The Colosseum");
        visitedPlaces.add("Kyoto Arashiyama Bamboo Grove");
        visitedPlaces.add("Times Square NYC");
        visitedPlaces.add("The Great Wall of China");

        // Create an ArrayAdapter using the simple_spinner_item layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, visitedPlaces);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerPlaces.setAdapter(adapter);


        // --- 2. SET UP RATING BAR LISTENER ---
        // Update the text view below the rating bar dynamically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Cast to integer to show whole number stars (as requested: 1-5)
                int intRating = (int) rating;
                tvRatingValue.setText(intRating + " / 5 Stars");
            }
        });


        // --- 3. SET UP SUBMIT BUTTON LOGIC ---
        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReviewData();
            }
        });
    }

    // Method to handle validation and submission
    private void submitReviewData() {
        String selectedPlace = spinnerPlaces.getSelectedItem().toString();
        int starRating = (int) ratingBar.getRating();
        String reviewText = etReviewText.getText().toString().trim();

        // VALIDATION
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

        // SUBMISSION LOGIC (Where you send data to your server/database)
        // For now, we show a success message with the data.
        String summary = "Review Submitted for " + selectedPlace + "\n" +
                "Rating: " + starRating + " Stars\n" +
                "Review: \"" + reviewText + "\"";

        Toast.makeText(this, summary, Toast.LENGTH_LONG).show();

        // Optional: Finish the activity and go back to previous screen
        // finish();
    }
}