package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private TextView textSearchQuery;
    private AutoCompleteTextView actv;
    private LinearLayout resultsContainer;

    LinearLayout navActivity, navItinerary, navPost, navChat, navSearch;

    private FirebaseFirestore db;
    private ArrayList<Post> allPosts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = FirebaseFirestore.getInstance();

        textSearchQuery = findViewById(R.id.textSearchQuery);
        actv = findViewById(R.id.autocomplete_tv);
        resultsContainer = findViewById(R.id.resultsContainer);

        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);
        navChat = findViewById(R.id.navChat);
        navSearch = findViewById(R.id.navSearch);

        navItinerary.setOnClickListener(v ->
                startActivity(new Intent(SearchActivity.this, ItineraryActivity.class)));

        navActivity.setOnClickListener(v ->
                startActivity(new Intent(SearchActivity.this, MainActivity.class)));

        navPost.setOnClickListener(v ->
                startActivity(new Intent(SearchActivity.this, CreatePostActivity.class)));

        loadPosts();

        actv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPosts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadPosts() {
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allPosts.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Post post = doc.toObject(Post.class);
                        if (post != null) {
                            allPosts.add(post);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(SearchActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show()
                );
    }

    private void filterPosts(String query) {
        resultsContainer.removeAllViews();

        if (query == null || query.trim().isEmpty()) {
            return;
        }

        String lowerQuery = query.toLowerCase().trim();

        for (Post post : allPosts) {
            if (post.getTitle() != null &&
                    post.getTitle().toLowerCase().contains(lowerQuery)) {

                Button btn = new Button(this);
                btn.setText(post.getTitle());
                btn.setAllCaps(false);
                btn.setPadding(20, 20, 20, 20);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 20, 0, 0);
                btn.setLayoutParams(params);

                btn.setOnClickListener(v -> openPost(post));

                resultsContainer.addView(btn);
            }
        }
    }

    private void openPost(Post post) {
        Intent intent = new Intent(SearchActivity.this, PlaceDetails.class);

        intent.putExtra("place_title", post.getTitle());
        intent.putExtra("place_description", post.getDescription());
        intent.putExtra("place_city", post.getCity());
        intent.putExtra("place_state", post.getState());
        intent.putExtra("place_country", post.getCountry());
        intent.putExtra("place_image_url", post.getImageUrl());
        intent.putExtra("post_user_id", post.getUserId());
        intent.putExtra("post_username", post.getUsername());

        startActivity(intent);
    }
}