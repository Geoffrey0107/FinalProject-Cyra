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
    private Handler handler = new Handler(Looper.getMainLooper()); // used for delay. creates a thread on the main UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_verification);

        resendButton = findViewById(R.id.resend_verification_button);
        continueButton = findViewById(R.id.continue_button);

        // initializes firebase auth, database, and gets the current user
        fba = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = fba.getCurrentUser();

        // gets username from fragment
        username = getIntent().getStringExtra("username");

        // disables resend button
        resendButton.setEnabled(false);

        // enables reset button after a minute
        enableResendAfterDelay();

        // allows user to resend verification email
        resendButton.setOnClickListener(v -> {
            if (user != null && !user.isEmailVerified()) {
                user.sendEmailVerification().addOnCompleteListener(task -> { // sends verification again
                    if (task.isSuccessful()) { // toasts user and then disables button again
                        Toast.makeText(this, "Verification email resent", Toast.LENGTH_SHORT).show();
                        resendButton.setEnabled(false);
                        enableResendAfterDelay(); // delays again after sending
                    } else { // failed to send
                        Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // only allows users to continue once verified
        continueButton.setOnClickListener(v -> {
            if (user != null) {
                user.reload().addOnCompleteListener(task -> { // reloads user to get if email is verified
                    if (user.isEmailVerified()) { // only if the email is verified
                        // Save user in Firestore
                        db.collection("users").document(user.getUid())
                                .get()
                                .addOnSuccessListener(doc -> {
                                    if (!doc.exists()) { // if the user does not exist then create it
                                        User newUser = new User(user.getUid(), username, System.currentTimeMillis(), true, null, null);
                                        db.collection("users").document(user.getUid()).set(newUser);

                                        // routes to main and sends user information over for profile creation
                                        Intent i = new Intent(PendingVerificationActivity.this, MainActivity.class);
                                        i.putExtra(MainActivity.EXTRA_USER_ID, newUser.getUserId());
                                        i.putExtra(MainActivity.EXTRA_USERNAME, newUser.getUsername());

                                        // Go to MainActivity after verification

                                        startActivity(i);
                                    }
                                    finish();
                                });
                    } else {
                        // delete email off of auth so it does not take up extra space
                        user.delete().addOnCompleteListener(delTask -> {
                            if (delTask.isSuccessful()) { // if deletion was successful
                                Toast.makeText(this, "Email not verified. Account deleted.", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(PendingVerificationActivity.this, LoginActivity.class); // redirects back to login activity
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to delete account: " + delTask.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });
    }

    // sets a delay on the resend button
    private void enableResendAfterDelay() {
        // delays for 1 minute
        handler.postDelayed(() -> resendButton.setEnabled(true), 60000); // 1 min
    }
}
