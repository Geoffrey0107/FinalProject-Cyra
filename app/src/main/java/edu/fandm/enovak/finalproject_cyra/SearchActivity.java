package edu.fandm.enovak.finalproject_cyra;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        textSearchQuery = findViewById(R.id.textSearchQuery);

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