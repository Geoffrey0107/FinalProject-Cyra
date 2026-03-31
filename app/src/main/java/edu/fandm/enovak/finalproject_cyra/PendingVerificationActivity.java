package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PendingVerificationActivity extends AppCompatActivity {
    private Button resendButton, continueButton;
    private FirebaseAuth fba;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String username;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_verification);

        resendButton = findViewById(R.id.resend_verification_button);
        continueButton = findViewById(R.id.continue_button);

        fba = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = fba.getCurrentUser();

        username = getIntent().getStringExtra("username");

        resendButton.setEnabled(false);
        enableResendAfterDelay();

        resendButton.setOnClickListener(v -> {
            if (user != null && !user.isEmailVerified()) {
                user.sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Verification email resent", Toast.LENGTH_SHORT).show();
                        resendButton.setEnabled(false);
                        enableResendAfterDelay();
                    } else {
                        Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        continueButton.setOnClickListener(v -> {
            if (user != null) {
                user.reload().addOnCompleteListener(task -> {
                    if (user.isEmailVerified()) {
                        // Save user in Firestore
                        db.collection("users").document(user.getUid())
                                .get()
                                .addOnSuccessListener(doc -> {
                                    if (!doc.exists()) {
                                        User newUser = new User(user.getUid(), username, System.currentTimeMillis(), true, null, null);
                                        db.collection("users").document(user.getUid()).set(newUser);
                                    }
                                    // Go to MainActivity
                                    Intent i = new Intent(PendingVerificationActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                });
                    } else {
                        Toast.makeText(this, "Email not verified yet", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void enableResendAfterDelay() {
        handler.postDelayed(() -> resendButton.setEnabled(true), 60000); // 1 min
    }
}
