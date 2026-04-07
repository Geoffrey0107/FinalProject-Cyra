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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ItineraryActivity extends AppCompatActivity {

    ListView itineraryListView;
    ArrayAdapter<String> adapter;

    LinearLayout navActivity, navItinerary, navPost,navSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);
        navSearch = findViewById(R.id.navSearch);
        itineraryListView = findViewById(R.id.itineraryListView);
        ImageButton btnShare = findViewById(R.id.share_button);

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

            Intent intent = new Intent(ItineraryActivity.this, ReviewActivity.class);
            intent.putExtra("place_name", selectedPlace);
            startActivity(intent);
        });

        ImageButton ib = (ImageButton) findViewById(R.id.share_button);
        ib.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                StringBuilder itineraryText = new StringBuilder();
                itineraryText.append("My itinerary:\n\n");

                for (String item : ItineraryData.itineraryList) {
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
}