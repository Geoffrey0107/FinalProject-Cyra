package edu.fandm.enovak.finalproject_cyra;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import edu.fandm.enovak.finalproject_cyra.R;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView profileImage;
    private EditText etName, etAge, etInterests;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this).load(uri).circleCrop().into(profileImage);
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
        btnSaveProfile.setOnClickListener(v -> {
            if (etName.getText().toString().isEmpty() || selectedImageUri == null) {
                Toast.makeText(this, "Complete your profile first!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Profile saved locally!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}