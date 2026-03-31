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

    Button testBut;
    ImageButton btnAdd1, btnAdd2;
    LinearLayout navItinerary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        testBut = findViewById(R.id.testBut);
        btnAdd1 = findViewById(R.id.btnAdd1);
        btnAdd2 = findViewById(R.id.btnAdd2);
        navItinerary = findViewById(R.id.navItinerary);

        testBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        btnAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToItinerary("Central Market");
            }
        });

        btnAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToItinerary("River Trail Walk");
            }
        });

        navItinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ItineraryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addToItinerary(String activityName) {
        if (!ItineraryData.itineraryList.contains(activityName)) {
            ItineraryData.itineraryList.add(activityName);
            Toast.makeText(MainActivity.this, activityName + " added to itinerary", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, activityName + " is already in itinerary", Toast.LENGTH_SHORT).show();
        }
    }
}