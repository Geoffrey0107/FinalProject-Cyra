package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import edu.fandm.enovak.finalproject_cyra.R;

import com.bumptech.glide.Glide;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private EditText etName, etAge, etInterests;
    private Uri selectedImageUri;

    // Register a launcher for the modern Activity Result API to handle image selection.
    private final ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                // This callback runs when the user selects an image from the gallery.
                if (uri != null) {
                    selectedImageUri = uri;
                    // Use Glide to load the selected image URI into the ImageView efficiently.
                    Glide.with(this)
                            .load(uri)
                            .circleCrop() // Optional: Makes the image round
                            .into(profileImage);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize UI Elements
        profileImage = findViewById(R.id.profile_image);
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etInterests = findViewById(R.id.et_interests);
        Button btnSelectImage = findViewById(R.id.btn_select_image);
        Button btnSaveProfile = findViewById(R.id.btn_save_profile);

        // Set up the listener to launch the image picker
        btnSelectImage.setOnClickListener(v -> {
            // "image/*" launches the system gallery to pick any image type.
            getContentLauncher.launch("image/*");
        });

        // Set up the listener to validate and "save" the profile.
        btnSaveProfile.setOnClickListener(v -> {
            saveProfile();
        });
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String interests = etInterests.getText().toString().trim();

        // 1. Validation
        if (name.isEmpty() || age.isEmpty() || interests.isEmpty()) {
            Toast.makeText(this, "Please fill out all text fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select a profile picture.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Formatting data
        // For now, we will just show a Toast confirming the data was received.
        String successMessage = "Profile Created!\nName: " + name + ", Age: " + age;
        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();

        // 3. (Optional) Pass data to another screen or save it.
        // For example, to pass the data to MainActivity:
        // Intent intent = new Intent(this, MainActivity.class);
        // intent.putExtra("USER_NAME", name);
        // intent.putExtra("USER_IMAGE_URI", selectedImageUri.toString());
        // startActivity(intent);
        // finish(); // Closes the creation screen
    }
}