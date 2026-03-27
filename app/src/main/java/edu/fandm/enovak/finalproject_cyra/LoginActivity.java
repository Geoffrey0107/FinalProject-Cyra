package edu.fandm.enovak.finalproject_cyra;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    String TAG = "LOGIN ACTIVITY"; // name of activity
    FirebaseAuth fba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fba = FirebaseAuth.getInstance();
        FirebaseUser user = fba.getCurrentUser();

//        if the user actually exists then send them to main page with user info
//        if (user != null) {
//
//        }

    }


    // signs user in using email and password
    private void signIn(String email, String password){
        Task s = fba.signInWithEmailAndPassword(email, password);
        s.addOnCompleteListener(new OnCompleteListener() {

            // when user has logged in successfully, get the user and update information
            // on main page
            // otherwise log failure

            @Override
            public void onComplete(Task task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = fba.getCurrentUser();
                    Toast.makeText(getApplicationContext(), "Login Successful!",
                            Toast.LENGTH_LONG).show();
//                    updateUI(user);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to login :(",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // creates a new user with the given email and password
    private void registerNewUser(String email, String password){
        Task<AuthResult> task = fba.createUserWithEmailAndPassword(email,
                password);
        task.addOnCompleteListener(new OnCompleteListener() {

            // when user is registered successfully, get the user and update information
            // on main page
            // otherwise log failure
            @Override
            public void onComplete(Task task) {
                Log.d(TAG, "task: " + task);
                if (task.isSuccessful()) {
                    FirebaseUser user = fba.getCurrentUser(); // we're now logged in immediately!
                            Toast.makeText(getApplicationContext(), "New User Created!",
                                    Toast.LENGTH_LONG).show();
//                    updateUI(user);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to create new user :(",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // Display the user's UID
//    private void updateUI(FirebaseUser user){
//        TextView tv = (TextView) findViewById(R.id.fb_login_uid_tv);
//        tv.setText("Login Successful!\nUID: " + user.getUid());
//    }
}