package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class CreatePostActivity extends AppCompatActivity {

    EditText etTitle, etDescription, etCountry,etState,etCity ;
    Button btnSubmitPost, btnSelectImage;

    LinearLayout navActivity, navItinerary, navPost,navSearch,navChat;

    private Uri imageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);
        navSearch = findViewById(R.id.navSearch);

        etCountry = findViewById(R.id.etCountry);
        etState = findViewById(R.id.etState);
        etCity = findViewById(R.id.etCity);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        btnSelectImage = findViewById(R.id.btnUploadImage);

        ImageView ivActivityIcon = findViewById(R.id.ivPostIcon);
        TextView tvActivityText = findViewById(R.id.tvPostIcon);

        int activeColor = android.graphics.Color.parseColor("#1E3A5F");

        ivActivityIcon.setColorFilter(activeColor);
        tvActivityText.setTextColor(activeColor);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                        Log.d("CREATE_POST", "Selected URI: " + uri);
                    }
                }
        );

        btnSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        navActivity.setOnClickListener(v -> {
            Intent intent = new Intent(CreatePostActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        navItinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreatePostActivity.this, ItineraryActivity.class);
                startActivity(intent);
            }
        });
        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreatePostActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        btnSubmitPost.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            String country = etCountry.getText().toString().trim();
            String state = etState.getText().toString().trim();
            String city = etCity.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || country.isEmpty() || state.isEmpty() || city.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri == null) {
                Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = UserSessionManager.getInstance().getUserId();
            String username = UserSessionManager.getInstance().getUsername();

            Log.d("CREATE_POST", "Session userId = " + userId);
            Log.d("CREATE_POST", "Session username = " + username);

            if (userId == null || username == null) {
                Toast.makeText(this, "User session missing. Please log in again.", Toast.LENGTH_LONG).show();
                return;
            }

            long timestamp = System.currentTimeMillis();

            StorageReference ref = FirebaseStorage.getInstance()
                    .getReference()
                    .child("images/" + timestamp + ".jpg");

            Log.d("CREATE_POST", "Uploading URI: " + imageUri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);

                byte[] data = baos.toByteArray();

                ref.putBytes(data)
                        .addOnSuccessListener(taskSnapshot -> {
                            ref.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        String imageUrl = uri.toString();

                                        Post post = new Post(
                                                title,
                                                description,
                                                country,
                                                state,
                                                city,
                                                imageUrl,
                                                userId,
                                                username,
                                                timestamp
                                        );

                                        FirebaseFirestore.getInstance()
                                                .collection("posts")
                                                .add(post)
                                                .addOnSuccessListener(doc -> {
                                                    saveLocationIfNeeded(city, state, country);
                                                    Toast.makeText(CreatePostActivity.this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("CREATE_POST", "Firestore write failed", e);
                                                    Toast.makeText(CreatePostActivity.this, "Failed to save post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("CREATE_POST", "Failed to get download URL", e);
                                        Toast.makeText(CreatePostActivity.this, "Failed to get image URL", Toast.LENGTH_LONG).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("CREATE_POST", "Upload failed", e);
                            Toast.makeText(CreatePostActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });

            } catch (Exception e) {
                Log.e("CREATE_POST", "Image processing failed", e);
                Toast.makeText(this, "Image processing failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveLocationIfNeeded(String city, String state, String country) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String locationId = city + "_" + state + "_" + country;
        locationId = locationId.replace(" ", "_");

        java.util.HashMap<String, Object> locationMap = new java.util.HashMap<>();
        locationMap.put("city", city);
        locationMap.put("state", state);
        locationMap.put("country", country);
        locationMap.put("displayName", city + ", " + state + ", " + country);

        db.collection("locations")
                .document(locationId)
                .set(locationMap);
    }
}