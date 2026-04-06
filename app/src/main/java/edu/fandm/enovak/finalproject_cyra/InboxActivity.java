package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InboxActivity extends AppCompatActivity {

    LinearLayout navActivity, navItinerary, navPost, navSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inbox);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inbox_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);
        navSearch = findViewById(R.id.navSearch);

        // go to itinerary
        navItinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InboxActivity.this, ItineraryActivity.class);
                startActivity(intent);
            }
        });

        // go back to main activity/feed screen
        navActivity.setOnClickListener(v -> {
            Intent intent = new Intent(InboxActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // go to create post screen
        navPost.setOnClickListener(v -> {
            Intent intent = new Intent(InboxActivity.this, CreatePostActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(InboxActivity.this, SearchActivity.class);
            startActivity(intent);
        });

    }
}