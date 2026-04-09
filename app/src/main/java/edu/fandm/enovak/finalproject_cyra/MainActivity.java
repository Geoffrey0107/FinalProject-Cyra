package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button testBut;
    ImageButton btnAdd1, btnAdd2,btnProfile,btnMore;
    LinearLayout navActivity, navItinerary, navPost, navSearch, navChat;

    private static final String PREFS_NAME = "cyra_prefs";
    private static final String KEY_COUNTRY = "selected_country";
    private static final String KEY_STATE = "selected_state";
    private static final String KEY_CITY = "selected_city";
    SwitchCompat connectionToggle;
    private String userId;
    private boolean isConnectionMode;
    TextView tvTopLocation, tvConnectionLabel;
    FirebaseFirestore db;

    String selectedCountry;
    String selectedState;
    String selectedCity;
    ArrayList<String> availableLocations = new ArrayList<>();
    ArrayList<Post> postList;
    PostAdapter postAdapter;
    ListView placesListView;

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

        db = FirebaseFirestore.getInstance();

        loadSelectedLocation();
        tvTopLocation = findViewById(R.id.tvTopLocation);
        tvTopLocation.setText(selectedCity);

        tvTopLocation.setOnClickListener(v -> loadLocationsAndShowDialog());

        tvConnectionLabel = findViewById(R.id.tvConnectionLabel);
        connectionToggle = findViewById(R.id.switchConnectionMode);

        userId = UserSessionManager.getInstance().getUserId();

        btnProfile = findViewById(R.id.btnProfile);
        btnMore = findViewById(R.id.btnMore);

        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);
        navSearch = findViewById(R.id.navSearch);
        navChat = findViewById(R.id.navChat);

        placesListView = findViewById(R.id.placesListView);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        placesListView.setAdapter(postAdapter);

        saveSelectedLocation();
        tvTopLocation.setText(selectedCity);
        loadPosts();

        ImageButton btnProfile, btnMore;

        btnProfile = findViewById(R.id.btnProfile);
        btnMore = findViewById(R.id.btnMore);
        btnMore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

//        btnAdd1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addToItinerary("Central Market");
//            }
//        });
//
//        btnAdd2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addToItinerary("River Trail Walk");
//            }
//        });

        navItinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ItineraryActivity.class);
                startActivity(intent);
            }
        });

        navPost.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
            startActivity(intent);
        });

        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        navChat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InboxActivity.class);
            startActivity(intent);
        });

        // initial state setup
        isConnectionMode = UserSessionManager.getInstance().getCommsStatus();

        if (!isConnectionMode) {
            navChat.setEnabled(false);
        } else {
            navChat.setEnabled(true);
        }

        connectionToggle.setChecked(isConnectionMode);
        tvConnectionLabel.setText(isConnectionMode ? "On" : "Off");

        connectionToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {

            // checks if user is logged in.
            // will not allow user to use connection mode if not logged in
            if (!UserSessionManager.getInstance().isLoggedIn()) {
                connectionToggle.setEnabled(false); // disables toggle
                Toast.makeText(MainActivity.this, "Must be logged in to use connection mode",
                        Toast.LENGTH_LONG).show();
                return;
            } else {
                connectionToggle.setEnabled(true);
            }

            if (isChecked) {
                // ON
                tvConnectionLabel.setText("On");
                Toast.makeText(this, "Connection Mode ON", Toast.LENGTH_SHORT).show();
                UserSessionManager.getInstance().setComms(isChecked); // update comms

                navChat.setVisibility(View.VISIBLE);
            } else {
                // OFF
                tvConnectionLabel.setText("Off");
                Toast.makeText(this, "Connection Mode OFF", Toast.LENGTH_SHORT).show();
                UserSessionManager.getInstance().setComms(isChecked);

                navChat.setVisibility(View.INVISIBLE);
            }

            db.collection("users")
                    .document(userId)
                    .update("showLocation", isChecked)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show();
                    });
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
    private void loadPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts")
                .whereEqualTo("country", selectedCountry)
                .whereEqualTo("state", selectedState)
                .whereEqualTo("city", selectedCity)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Post post = doc.toObject(Post.class);
                        if (post != null) {
                            postList.add(post);
                        }
                    }

                    postAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPosts();
    }

    private void showDynamicLocationDialog() {
        String[] locations = availableLocations.toArray(new String[0]);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select Location")
                .setItems(locations, (dialog, which) -> {
                    String selected = locations[which];

                    String[] parts = selected.split(", ");
                    if (parts.length == 3) {
                        selectedCity = parts[0];
                        selectedState = parts[1];
                        selectedCountry = parts[2];
                    }

                    saveSelectedLocation();
                    tvTopLocation.setText(selectedCity);
                    loadPosts();
                })
                .show();
    }
    private void loadSelectedLocation() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        selectedCountry = prefs.getString(KEY_COUNTRY, "USA");
        selectedState = prefs.getString(KEY_STATE, "NY");
        selectedCity = prefs.getString(KEY_CITY, "New York");

    }
    private void saveSelectedLocation() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_COUNTRY, selectedCountry)
                .putString(KEY_STATE, selectedState)
                .putString(KEY_CITY, selectedCity)
                .apply();
    }
    private void loadLocationsAndShowDialog() {
        FirebaseFirestore.getInstance()
                .collection("locations")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    availableLocations.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String displayName = doc.getString("displayName");
                        if (displayName != null && !displayName.isEmpty()) {
                            availableLocations.add(displayName);
                        }
                    }

                    if (availableLocations.isEmpty()) {
                        Toast.makeText(MainActivity.this, "No locations available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    showDynamicLocationDialog();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to load locations", Toast.LENGTH_SHORT).show();
                });
    }
}
