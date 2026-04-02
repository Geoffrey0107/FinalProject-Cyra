package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Define variables at the top so they are accessible everywhere in the class
    ImageButton btnAdd1, btnAdd2, btnProfile, btnMore;
    LinearLayout navActivity, navItinerary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // This handles the "Edge to Edge" display.
        // Ensure R.id.main matches the ID of your top-level layout in activity_main.xml
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize UI Elements
        btnAdd1 = findViewById(R.id.btnAdd1);
        btnAdd2 = findViewById(R.id.btnAdd2);
        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        btnProfile = findViewById(R.id.btnProfile);
        btnMore = findViewById(R.id.btnMore);

        // --- NAVIGATION LOGIC ---

        // Opens the Profile Page
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        // Opens the Login/More Page
        btnMore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Opens the Itinerary Page
        navItinerary.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ItineraryActivity.class);
            startActivity(intent);
        });

        // --- BUTTON ACTIONS ---

        btnAdd1.setOnClickListener(view -> addToItinerary("Central Market"));
        btnAdd2.setOnClickListener(view -> addToItinerary("River Trail Walk"));
    }

    private void addToItinerary(String activityName) {
        // Ensure ItineraryData class and list exist in your project
        if (!ItineraryData.itineraryList.contains(activityName)) {
            ItineraryData.itineraryList.add(activityName);
            Toast.makeText(MainActivity.this, activityName + " added to itinerary", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, activityName + " is already in itinerary", Toast.LENGTH_SHORT).show();
        }
    }
}