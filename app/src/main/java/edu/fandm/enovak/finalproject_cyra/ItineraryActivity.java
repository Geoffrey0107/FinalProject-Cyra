package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ItineraryActivity extends AppCompatActivity {

    ListView itineraryListView;
    ArrayAdapter<String> adapter;

    LinearLayout navActivity, navItinerary, navPost,navSearch,navChat;

    ImageView ivChatIcon;
    TextView tvChatText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);
        navSearch = findViewById(R.id.navSearch);
        navChat = findViewById(R.id.navChat);

        ImageView ivChatIcon = findViewById(R.id.ivChatIcon);
        TextView tvChatText = findViewById(R.id.ivChatText);

        setupChatUI(navChat, ivChatIcon, tvChatText);

        ImageView ivItineraryIcon = findViewById(R.id.ivItineraryIcon);
        TextView tvItineraryText = findViewById(R.id.tvItineraryText);

        int activeColor = android.graphics.Color.parseColor("#4DA3FF");

        ivItineraryIcon.setColorFilter(activeColor);
        tvItineraryText.setTextColor(activeColor);

        itineraryListView = findViewById(R.id.itineraryListView);

        if (itineraryListView == null) {
            throw new RuntimeException("ListView not found");
        }
//        adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_list_item_1,
//                ItineraryData.itineraryList
//        );

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                UserSessionManager.getInstance().getItineraryList()
        );

        itineraryListView.setAdapter(adapter);

        itineraryListView.setOnItemLongClickListener((parent, view, position, id) -> {
//            String itemToDelete = ItineraryData.itineraryList.get(position);
            String itemToDelete = UserSessionManager.getInstance().getItineraryList().get(position);

            new AlertDialog.Builder(ItineraryActivity.this)
                    .setTitle("Delete Item")
                    .setMessage("Remove \"" + itemToDelete + "\" from itinerary?")
                    .setPositiveButton("Delete", (dialog, which) -> {
//                        ItineraryData.itineraryList.remove(position);
                        UserSessionManager.getInstance().removeFromItinerary(itemToDelete);
                        adapter.notifyDataSetChanged();

                        if (UserSessionManager.getInstance().isLoggedIn()) {
                            saveItineraryToFirestore();
                        }

                        Toast.makeText(ItineraryActivity.this, itemToDelete + " removed", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });
        navChat.setOnClickListener(v -> {
            if (!UserSessionManager.getInstance().getCommsStatus()) {
                Toast.makeText(ItineraryActivity.this, "Connection mode is off", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(ItineraryActivity.this, InboxActivity.class);
            startActivity(intent);
        });
        navActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ItineraryActivity.this, MainActivity.class);
            startActivity(intent);
        });
        navPost.setOnClickListener(v -> {
            Intent intent = new Intent(ItineraryActivity.this, CreatePostActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(ItineraryActivity.this, SearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        itineraryListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPlace = UserSessionManager.getInstance().getItineraryList().get(position);

            FirebaseFirestore.getInstance()
                    .collection("posts")
                    .whereEqualTo("title", selectedPlace)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(ItineraryActivity.this, "Place not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        Post post = doc.toObject(Post.class);

                        if (post == null) {
                            Toast.makeText(ItineraryActivity.this, "Error loading place", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent(ItineraryActivity.this, PlaceDetails.class);
                        intent.putExtra("place_title", post.getTitle());
                        intent.putExtra("place_description", post.getDescription());
                        intent.putExtra("place_image_url", post.getImageUrl());
                        intent.putExtra("place_country", post.getCountry());
                        intent.putExtra("place_state", post.getState());
                        intent.putExtra("place_city", post.getCity());
                        intent.putExtra("post_user_id", post.getUserId());
                        intent.putExtra("post_username", post.getUsername());
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ItineraryActivity.this, "Failed to load place", Toast.LENGTH_SHORT).show();
                    });
        });

        ImageButton ib = (ImageButton) findViewById(R.id.share_button);
        ib.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                StringBuilder itineraryText = new StringBuilder();
                itineraryText.append("My itinerary:\n\n");

                for (String item : UserSessionManager.getInstance().getItineraryList()) {
                    itineraryText.append("• ").append(item).append("\n");
                }
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, itineraryText.toString());
                shareIntent.setType("text/plain");

                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();

        if (navChat != null && ivChatIcon != null && tvChatText != null) {
            setupChatUI(navChat, ivChatIcon, tvChatText);
        }
    }

    public void saveItineraryToFirestore() {
        if (!UserSessionManager.getInstance().isLoggedIn()) return; // only save if logged in

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = UserSessionManager.getInstance().getUserId();

        Map<String, Object> data = new HashMap<>();
        data.put("items", UserSessionManager.getInstance().getItineraryList());
        data.put("timestamp", System.currentTimeMillis());

        db.collection("itineraries")
                .document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "Itinerary saved successfully"))
                .addOnFailureListener(e -> Log.e("FIRESTORE", "Error saving itinerary", e));
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