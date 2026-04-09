package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private static final String BUCKET_URL = "gs://cyra-c61a5"; // Your Bucket

    private ImageView profileImage;
    private EditText etName, etAge, etInterests;
    private Uri selectedImageUri;

    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private final ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    // This shows the image LOCALLY
                    Glide.with(this).load(uri).circleCrop().into(profileImage);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        profileImage = findViewById(R.id.profile_image);
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etInterests = findViewById(R.id.et_interests);
        Button btnSelectImage = findViewById(R.id.btn_select_image);
        Button btnSaveProfile = findViewById(R.id.btn_save_profile);

        // Fetch data from Firestore so it loads when you open the activity
        if(UserSessionManager.getInstance().isLoggedIn()){
            loadUserData();
        }


        btnSelectImage.setOnClickListener(v -> getContentLauncher.launch("image/*"));
        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void loadUserData() {
        if (currentUser == null) return;

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        etName.setText(doc.getString("username"));
                        etAge.setText(String.valueOf(doc.get("age") != null ? doc.get("age") : ""));
                        etInterests.setText(doc.getString("interests"));

                        String url = doc.getString("profileImageUrl");
                        if (url != null && !url.isEmpty()) {
                            // This pulls the image FROM FIREBASE
                            Glide.with(this).load(url).circleCrop().into(profileImage);
                        }
                    }
                });
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();

        if (UserSessionManager.getInstance().isLoggedIn()) {
            Toast.makeText(this, "Not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        // If a NEW image was selected, upload it.
        // If no new image was selected (uri is null), just update the name/age.
        if (selectedImageUri != null) {
            uploadToFirebase(name);
        } else {
            updateFirestore(name, null);
        }
    }

    private void uploadToFirebase(String name) {
        Toast.makeText(this, "Uploading to Firebase...", Toast.LENGTH_SHORT).show();

        // Target your specific bucket
        FirebaseStorage storage = FirebaseStorage.getInstance(BUCKET_URL);
        StorageReference ref = storage.getReference()
                .child("profile_pics/" + currentUser.getUid() + ".jpg");

        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), selectedImageUri));
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            // Perform the upload
            ref.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "Image successfully uploaded to Storage");

                        // Now get the URL to save in the Database
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            updateFirestore(name, uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Upload failed: " + e.getMessage());
                        Toast.makeText(this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    });

        } catch (IOException e) {
            Log.e(TAG, "Bitmap conversion failed", e);
        }
    }

    private void updateFirestore(String name, String imageUrl) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", name);
        user.put("age", etAge.getText().toString());
        user.put("interests", etInterests.getText().toString());

        if (imageUrl != null) {
            user.put("profileImageUrl", imageUrl);
        }
        if (UserSessionManager.getInstance().isLoggedIn()) {
            db.collection("users").document(currentUser.getUid())
                    .update(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile Saved to Cloud!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Firestore update failed", e));
        } else {
            new AlertDialog.Builder(UserProfileActivity.this)
                    .setTitle("Go to Login?")
                    .setMessage("You must be logged in to save your profile!")
                    .setPositiveButton("Login", (dialog, which) -> {
                        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
}