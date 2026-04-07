package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CreatePostActivity extends AppCompatActivity {

    EditText etTitle, etDescription;
    Button btnSubmitPost, btnSelectImage;
    LinearLayout navActivity, navItinerary, navPost;

    String selectedCountry = "USA";
    String selectedState = "PA";
    String selectedCity = "Lancaster";

    private Uri imageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // --- UI INITIALIZATION ---
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        btnSelectImage = findViewById(R.id.btnUploadImage);

        // NAVIGATION BUTTONS (Added null checks to prevent crashing if IDs don't exist yet)
        navActivity = findViewById(R.id.navActivity);
        navItinerary = findViewById(R.id.navItinerary);
        navPost = findViewById(R.id.navPost);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Set listeners only if the views exist in the layout
        if (navActivity != null) {
            navActivity.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            });
        }

        if (navItinerary != null) {
            navItinerary.setOnClickListener(v -> startActivity(new Intent(this, ItineraryActivity.class)));
        }

        btnSubmitPost.setOnClickListener(v -> uploadPost());
    }

    private void uploadPost() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp = System.currentTimeMillis();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + timestamp + ".jpg");

        try {
            // Updated image processing for modern Android versions
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(), imageUri));
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            ref.putBytes(data).addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveToFirestore(title, description, uri.toString(), timestamp);
                });
            }).addOnFailureListener(e -> Log.e("CREATE_POST", "Upload failed", e));

        } catch (IOException e) {
            Log.e("CREATE_POST", "Bitmap failed", e);
        }
    }

    private void saveToFirestore(String title, String desc, String url, long time) {
        Post post = new Post(title, desc, selectedCountry, selectedState, selectedCity, url, "test_user", time);
        FirebaseFirestore.getInstance().collection("posts").add(post)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}