package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // --- CRITICAL FIX: Constants for other activities to use ---
    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_USERNAME = "extra_username";

    Button testBut;
    ImageButton btnAdd1, btnAdd2, btnProfile, btnMore;
    LinearLayout navActivity, navItinerary, navPost, navSearch;

    String selectedCountry = "USA";
    String selectedState = "PA";
    String selectedCity = "Lancaster";
    TextView tvTopLocation;

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

        // Initialize UI Elements
        tvTopLocation = findViewById(R.id.tvTopLocation);
        tvTopLocation.setText(selectedCity);

        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);
        navSearch = findViewById(R.id.navSearch);

        btnProfile = findViewById(R.id.btnProfile);
        btnMore = findViewById(R.id.btnMore);

        placesListView = findViewById(R.id.placesListView);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        placesListView.setAdapter(postAdapter);

        // --- NAVIGATION LOGIC ---

        // Open User Profile
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        btnMore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // The old commented-out buttons for the static cards
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

        navItinerary.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ItineraryActivity.class);
            startActivity(intent);
        });

        navPost.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
            startActivity(intent);
        });

        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // Initial load of Firestore posts
        loadPosts();
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
                    Log.d("FIRESTORE", "Loaded posts: " + postList.size());
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
}