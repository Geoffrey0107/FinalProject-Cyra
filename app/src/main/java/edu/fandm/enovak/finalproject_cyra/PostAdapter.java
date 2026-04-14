package edu.fandm.enovak.finalproject_cyra;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostAdapter extends BaseAdapter {

    private final Activity activity;
    private final ArrayList<Post> postList;

    public PostAdapter(Activity activity, ArrayList<Post> postList) {
        this.activity = activity;
        this.postList = postList;
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public Object getItem(int position) {
        return postList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.item_post, parent, false);
        }

        Post currentPost = postList.get(position);

        TextView tvPostTitle = convertView.findViewById(R.id.tvPostTitle);
        TextView tvPostDesc = convertView.findViewById(R.id.tvPostDesc);

        tvPostTitle.setText(currentPost.getTitle());

        String fullDescription = currentPost.getDescription();
        String shortDescription;

        if (fullDescription != null && fullDescription.length() > 20) {
            shortDescription = fullDescription.substring(0, 20) + "...";
        } else {
            shortDescription = fullDescription;
        }

        tvPostDesc.setText(shortDescription);
        ImageView imageView = convertView.findViewById(R.id.postImage);
        ImageButton btnAddPost = convertView.findViewById(R.id.btnAddPost);
        ImageButton btnLikePost = convertView.findViewById(R.id.btnLikePost);
        ImageButton btnDislikePost = convertView.findViewById(R.id.btnDislikePost);

        tvPostTitle.setText(currentPost.getTitle());
        tvPostDesc.setText(currentPost.getDescription());

        Glide.with(activity)
                .load(currentPost.getImageUrl())
                .into(imageView);

        btnAddPost.setOnClickListener(v -> {
            String activityName = currentPost.getTitle();
//            if (!ItineraryData.itineraryList.contains(activityName)) {
//                ItineraryData.itineraryList.add(activityName);
//                Toast.makeText(activity, activityName + " added to itinerary", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(activity, activityName + " is already in itinerary", Toast.LENGTH_SHORT).show();
//            }

            if (!UserSessionManager.getInstance().getItineraryList().contains(activityName)) {
                UserSessionManager.getInstance().addToItinerary(activityName);

                // Save to Firestore if logged in
                saveItineraryToFirestore();

                Toast.makeText(activity, activityName + " added to itinerary", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, activityName + " is already in itinerary", Toast.LENGTH_SHORT).show();
            }
        });

        btnLikePost.setOnClickListener(v ->
                Toast.makeText(activity, "Liked " + currentPost.getTitle(), Toast.LENGTH_SHORT).show()
        );

        btnDislikePost.setOnClickListener(v -> {
            postList.remove(position);
            notifyDataSetChanged();
            Toast.makeText(activity, "Removed " + currentPost.getTitle(), Toast.LENGTH_SHORT).show();
        });

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, PlaceDetails.class);
            intent.putExtra("place_title", currentPost.getTitle());
            intent.putExtra("place_description", currentPost.getDescription());
            intent.putExtra("place_image_url", currentPost.getImageUrl());
            intent.putExtra("place_country", currentPost.getCountry());
            intent.putExtra("place_state", currentPost.getState());
            intent.putExtra("place_city", currentPost.getCity());
            intent.putExtra("post_user_id", currentPost.getUserId());
            intent.putExtra("post_username", currentPost.getUsername());
            activity.startActivity(intent);
        });

        return convertView;
    }

    private void saveItineraryToFirestore() {
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