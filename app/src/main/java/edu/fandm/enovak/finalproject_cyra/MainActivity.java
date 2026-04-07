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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button testBut;
    ImageButton btnAdd1, btnAdd2;
    LinearLayout navActivity, navItinerary, navPost, navSearch, navChat;

    String selectedCountry = "USA";
    String selectedState = "NY";
    String selectedCity = "New York";
    TextView tvTopLocation;

    ArrayList<Post> postList;
    PostAdapter postAdapter;
    ListView placesListView;
    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_USERNAME = "extra_username";


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

        tvTopLocation = findViewById(R.id.tvTopLocation);
        tvTopLocation.setText(selectedCity);

        tvTopLocation.setOnClickListener(v -> showLocationDialog());

        //btnAdd1 = findViewById(R.id.btnAdd1);
        //btnAdd2 = findViewById(R.id.btnAdd2);
        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);
        navSearch = findViewById(R.id.navSearch);
        navChat = findViewById(R.id.navChat);

        placesListView = findViewById(R.id.placesListView);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        placesListView.setAdapter(postAdapter);

        loadPosts();

        ImageButton btnProfile, btnMore;

        btnProfile = findViewById(R.id.btnProfile);
        btnMore = findViewById(R.id.btnMore);
        btnMore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts")
                .whereEqualTo("country", "USA")
                .whereEqualTo("state", "PA")
                .whereEqualTo("city", "Lancaster")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("FIRESTORE", "Loaded posts: " + postList.size());
                    postList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Post post = doc.toObject(Post.class);
                        if (post != null) {
                            postList.add(post);
                            Log.d("FIRESTORE", "Post title: " + post.getTitle());
                        }
                    }

                    postAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
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

    private void showLocationDialog() {
        String[] locations = {
                "Lancaster, PA, USA",
                "Philadelphia, PA, USA",
                "New York, NY, USA",
        };

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

                    tvTopLocation.setText(selectedCity);
                    loadPosts();
                })
                .show();
    }
}