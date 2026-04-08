package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    private static final String[] places = {
            "Lancaster, PA, United States",
            "Tokyo, Japan",
            "London, United Kingdom",
            "New York City, United States",
            "Paris, France",
            "Berlin, Germany",
            "Seoul, South Korea",
            "Sydney, Australia",
            "Mumbai, India",
            "Sao Paulo, Brazil",
            "Cairo, Egypt",
            "Toronto, Canada",
            "Mexico City, Mexico",
            "Rome, Italy",
            "Shanghai, China",
            "Lagos, Nigeria",
            "Istanbul, Turkey",
            "Bangkok, Thailand",
            "Buenos Aires, Argentina",
            "Dubai, United Arab Emirates",
            "Singapore, Singapore"
    };

    private TextView textSearchQuery;

    LinearLayout navActivity, navItinerary, navPost,navChat,navSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        textSearchQuery = findViewById(R.id.textSearchQuery);

        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);

        ImageView ivActivityIcon = findViewById(R.id.ivSearchIcon);
        TextView tvActivityText = findViewById(R.id.tvSearchText);

        int activeColor = android.graphics.Color.parseColor("#1E3A5F");

        ivActivityIcon.setColorFilter(activeColor);
        tvActivityText.setTextColor(activeColor);

        navItinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, ItineraryActivity.class);
                startActivity(intent);
            }
        });
        navActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        navPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, CreatePostActivity.class);
                startActivity(intent);
            }
        });
        String query = getIntent().getStringExtra("SEARCH_QUERY");
        if (query != null && !query.isEmpty()) {
            textSearchQuery.setText("Showing results for: \"" + query + "\"");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,places);

        AutoCompleteTextView actv = (AutoCompleteTextView)findViewById(R.id.autocomplete_tv);
        actv.setAdapter(adapter);
        actv.setThreshold(1);

    }
}