package edu.fandm.enovak.finalproject_cyra;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ItineraryActivity extends AppCompatActivity {

    ListView itineraryListView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        itineraryListView = findViewById(R.id.itineraryListView);

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
    }
}