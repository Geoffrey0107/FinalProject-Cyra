package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity {

    LinearLayout navActivity, navItinerary, navPost, navSearch;
    FloatingActionButton butAddReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inbox);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inbox_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);
        navSearch = findViewById(R.id.navSearch);
        butAddReq = findViewById(R.id.butAddRequest);

        // go to itinerary
        navItinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InboxActivity.this, ItineraryActivity.class);
                startActivity(intent);
            }
        });

        // go back to main activity/feed screen
        navActivity.setOnClickListener(v -> {
            Intent intent = new Intent(InboxActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // go to create post screen
        navPost.setOnClickListener(v -> {
            Intent intent = new Intent(InboxActivity.this, CreatePostActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(InboxActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // creates a dialog that prompts user to put in place and request
        butAddReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if not logged in, then do not allow
                if (!UserSessionManager.getInstance().isLoggedIn()) {
                    Toast.makeText(InboxActivity.this,
                            "You must be logged in to make a request.", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(InboxActivity.this);
                builder.setTitle("Create Request");

                View layoutInflater = getLayoutInflater().inflate(R.layout.dialog_create_request, null);
                builder.setView(layoutInflater);

                EditText etPlace = layoutInflater.findViewById(R.id.etPlace);
                EditText etEmail = layoutInflater.findViewById(R.id.etEmail);

                // sets positive and negative buttons for sending and cancelling
                builder.setPositiveButton("Send", null);
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                // Create the dialog
                AlertDialog dialog = builder.create();

                // Show the dialog
                dialog.show();

                // Override positive button to keep dialog open on invalid input
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String place = etPlace.getText().toString().trim();
                        String email = etEmail.getText().toString().trim();

                        if (place.isEmpty()) {
                            etPlace.setError("Place is required");
                            return; // keep dialog open
                        }

                        if (email.isEmpty()) {
                            etEmail.setError("Email is required");
                            return; // keep dialog open
                        }

                        // logic to save to firebase and send out request here
                        saveRequestToFirestore(email, place);

                        dialog.dismiss(); // Close dialog after success
                    }
                });

                // Negative button still closes automatically
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    public void saveRequestToFirestore(String email, String place) {
        if (!UserSessionManager.getInstance().isLoggedIn()) return; // only save if logged in

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String senderId = UserSessionManager.getInstance().getUserId();


        // Get all itineraries
        db.collection("itineraries")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot itineraryDoc : querySnapshot) {
                        String receiverId = itineraryDoc.getId();

                        // Skip sending to self
                        if (receiverId.equals(senderId)) continue;

                        // Check if itinerary contains the place
                        ArrayList<String> places = (ArrayList<String>) itineraryDoc.get("items");
                        if (places != null && places.contains(place)) {

                            // Optional: check communications toggle if stored in itinerary
                            db.collection("users").document(receiverId).get()
                                .addOnSuccessListener(userDoc -> {
                                    Boolean canCommunicate = userDoc.getBoolean("showLocation");

                                    if (canCommunicate != null && canCommunicate) {
                                        // Create and save request
                                        Request req = new Request(place, senderId, receiverId, email, System.currentTimeMillis());
                                        db.collection("requests")
                                                .add(req)
                                                .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "Request sent to " + receiverId))
                                                .addOnFailureListener(e -> Log.e("FIRESTORE", "Failed to send request", e));
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("FIRESTORE", "Error fetching user for communications toggle", e));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FIRESTORE", "Error fetching itineraries", e));
    }
}