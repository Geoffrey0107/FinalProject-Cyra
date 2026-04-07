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

// Explicitly importing your project's R file to fix the red text errors
import edu.fandm.enovak.finalproject_cyra.R;

import com.bumptech.glide.Glide;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private EditText etName, etAge, etInterests;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this)
                            .load(uri)
                            .circleCrop()
                            .into(profileImage);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profileImage = findViewById(R.id.profile_image);
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etInterests = findViewById(R.id.et_interests);
        Button btnSelectImage = findViewById(R.id.btn_select_image);
        Button btnSaveProfile = findViewById(R.id.btn_save_profile);

        btnSelectImage.setOnClickListener(v -> getContentLauncher.launch("image/*"));

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String interests = etInterests.getText().toString().trim();

        if (name.isEmpty() || age.isEmpty() || interests.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select a picture.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Profile Created: " + name, Toast.LENGTH_LONG).show();
        // Return to main screen after saving
        finish();
    }
}