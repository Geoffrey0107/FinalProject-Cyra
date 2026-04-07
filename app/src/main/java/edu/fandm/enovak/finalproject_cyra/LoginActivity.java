package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.Fragment;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class LoginActivity extends AppCompatActivity {
    String TAG = "LOGIN ACTIVITY"; // name of activity
    private Button loginBut;
    private Button registerBut;
    private Button aboutCyraBut;
    private Button logOut;


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

        loginBut = (Button) findViewById(R.id.loginBut);
        registerBut = (Button) findViewById(R.id.registerBut);
        aboutCyraBut = (Button) findViewById(R.id.about_cyra_but);
        logOut = (Button) findViewById(R.id.logOutBut);

        // if not logged in, hide log out button
        if (!UserSessionManager.getInstance().isLoggedIn()) {
            logOut.setVisibility(View.GONE);
            registerBut.setVisibility(View.VISIBLE);
            loginBut.setVisibility(View.VISIBLE);
        } else { // hide other buttons otherwise
            registerBut.setVisibility(View.GONE);
            loginBut.setVisibility(View.GONE);
            logOut.setVisibility(View.VISIBLE);
        }

        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new LoginFragment());
            }
        });

        registerBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new RegisterFragment());
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // clears user session then goes back to main
                UserSessionManager.getInstance().clear();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        aboutCyraBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Welcome To Cyra")
                        .setMessage(
                                "About Cyra\n\n" +

                                        "Cyra is an app designed for solo travelers that provides activity discovery based on user location. " +
                                        "Users can browse experiences through the inspiration feed, save activities, and create " +
                                        "or respond to coordination requests. The app prioritizes privacy, limiting user visibility and " +
                                        "interactions unless initiated by the user. Uses firebase for authentication, data storage and " +
                                        "messaging."
                        )
                        .setPositiveButton("Got it", null)
                        .show();
            }
        });
    }

    // loads fragments that are created.
    // fragments will get rid of the need to use new activities every time
    // it is more space efficient

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.login_activity, fragment)
                .addToBackStack(null) // adds to the back of stack so it can be popped to return to parent activity
                .commit();
    }


    // signs user in using email and password
//    private void signIn(String email, String password){
//        Task s = fba.signInWithEmailAndPassword(email, password);
//        s.addOnCompleteListener(new OnCompleteListener() {
//
//            // when user has logged in successfully, get the user and update information
//            // on main page
//            // otherwise log failure
//
//            @Override
//            public void onComplete(Task task) {
//                if (task.isSuccessful()) {
//                    FirebaseUser user = fba.getCurrentUser();
//                    Toast.makeText(getApplicationContext(), "Login Successful!",
//                            Toast.LENGTH_LONG).show();
////                    updateUI(user);
//                } else {
//                    Toast.makeText(getApplicationContext(), "Failed to login :(",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    // creates a new user with the given email and password
//    private void registerNewUser(String email, String password){
//        Task<AuthResult> task = fba.createUserWithEmailAndPassword(email,
//                password);
//        task.addOnCompleteListener(new OnCompleteListener() {
//
//            // when user is registered successfully, get the user and update information
//            // on main page
//            // otherwise log failure
//            @Override
//            public void onComplete(Task task) {
//                Log.d(TAG, "task: " + task);
//                if (task.isSuccessful()) {
//                    FirebaseUser user = fba.getCurrentUser(); // we're now logged in immediately!
//                            Toast.makeText(getApplicationContext(), "New User Created!",
//                                    Toast.LENGTH_LONG).show();
////                    updateUI(user);
//                } else {
//                    Toast.makeText(getApplicationContext(), "Failed to create new user :(",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
    // Display the user's UID
//    private void updateUI(FirebaseUser user){
//        TextView tv = (TextView) findViewById(R.id.fb_login_uid_tv);
//        tv.setText("Login Successful!\nUID: " + user.getUid());
//    }
}