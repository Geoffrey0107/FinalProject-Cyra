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
import android.widget.Toast;

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
    ImageView ivChatIcon;
    TextView ivChatText;

    LinearLayout navActivity, navItinerary, navPost,navChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        textSearchQuery = findViewById(R.id.textSearchQuery);

        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);

        ImageView ivChatIcon = findViewById(R.id.ivChatIcon);
        TextView ivChatText = findViewById(R.id.ivChatText);
        LinearLayout navChat = findViewById(R.id.navChat);

        setupChatUI(navChat, ivChatIcon, ivChatText);

        ImageView ivActivityIcon = findViewById(R.id.ivSearchIcon);
        TextView tvActivityText = findViewById(R.id.tvSearchText);

        int activeColor = android.graphics.Color.parseColor("#4DA3FF");

        ivActivityIcon.setColorFilter(activeColor);
        tvActivityText.setTextColor(activeColor);

        navItinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, ItineraryActivity.class);
                startActivity(intent);
            }
        });
        navChat.setOnClickListener(v -> {
            if (!UserSessionManager.getInstance().getCommsStatus()) {
                Toast.makeText(SearchActivity.this, "Connection mode is off", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(SearchActivity.this, InboxActivity.class);
            startActivity(intent);
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
    @Override
    protected void onResume() {
        super.onResume();

        if (navChat != null && ivChatIcon != null && ivChatText != null) {
            setupChatUI(navChat, ivChatIcon, ivChatText);
        }
    }
    private void setupChatUI(LinearLayout navChat, ImageView ivChatIcon, TextView tvChatText) {
        boolean enabled = UserSessionManager.getInstance().getCommsStatus();

        int defaultColor = android.graphics.Color.parseColor("#000000");
        int inactiveColor = android.graphics.Color.parseColor("#A9A9A9");

        navChat.setEnabled(enabled);
        navChat.setClickable(enabled);
        navChat.setAlpha(enabled ? 1.0f : 0.4f);

        ivChatIcon.setColorFilter(enabled ? defaultColor : inactiveColor);
        tvChatText.setTextColor(enabled ? defaultColor : inactiveColor);
    }
}