package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ItineraryActivity extends AppCompatActivity {

    ListView itineraryListView;
    ArrayAdapter<String> adapter;

    LinearLayout navActivity, navItinerary, navPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);
        itineraryListView = findViewById(R.id.itineraryListView);
        ImageButton btnShare = findViewById(R.id.share_button);

        if (itineraryListView == null) {
            throw new RuntimeException("ListView not found");
        }

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                ItineraryData.itineraryList
        );

        itineraryListView.setAdapter(adapter);

        itineraryListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String itemToDelete = ItineraryData.itineraryList.get(position);

            new AlertDialog.Builder(ItineraryActivity.this)
                    .setTitle("Delete Item")
                    .setMessage("Remove \"" + itemToDelete + "\" from itinerary?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        ItineraryData.itineraryList.remove(position);
                        adapter.notifyDataSetChanged();
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
        itineraryListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPlace = ItineraryData.itineraryList.get(position);

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
}