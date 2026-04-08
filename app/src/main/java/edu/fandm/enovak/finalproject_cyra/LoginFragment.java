package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the @link LoginFragment#newInstance factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button backButton;
    private Button resetPass;
    private FirebaseAuth fba;
    private FirebaseFirestore db;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * EditText, emailEditText, passwordEditText
     * Button, loginButton
     * @return A new instance of fragment LoginFragment.
     */

//    public static LoginFragment newInstance(String param1, String param2) {
//        LoginFragment fragment = new LoginFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(LayoutInflater inflater, ViewGroup container,
//                         Bundle savedInstanceState) {
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // initializes buttons and edit texts
        emailEditText = (EditText) view.findViewById(R.id.login_email);
        passwordEditText = (EditText) view.findViewById(R.id.login_password);
        loginButton = (Button) view.findViewById(R.id.login_submit);
        backButton = (Button) view.findViewById(R.id.back_button_login);
        resetPass = (Button) view.findViewById(R.id.reset_password);

        // initializes firebase auth instance and firestore database instance
        fba = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // if email and password are filled
                if (!email.isEmpty() && !password.isEmpty()) {

                    // firebase logic to get user from
                    // tries to sign in with email and password
                    fba.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) { // if log in was successful
                                // get the current user
                                FirebaseUser firebaseUser = fba.getCurrentUser();

                                if (firebaseUser != null) {
                                    String uid = firebaseUser.getUid(); // gets user id
                                    // Fetch user object from Firestore collection user
                                    db.collection("users").document(uid)
                                            .get()
                                            .addOnSuccessListener(documentSnapshot -> { // if it succeeds, pass snapshot of current user/document
                                                if (documentSnapshot.exists()) { // checks if user actually exists
                                                    User user = documentSnapshot.toObject(User.class); // converts it back to user object

                                                    // login successful
                                                    Toast.makeText(getActivity(), "Logging in with: " + email,
                                                            Toast.LENGTH_SHORT).show();

                                                    // this is where we would navigate to the main feed
                                                    Intent i = new Intent(getActivity(), MainActivity.class);

                                                    // sets the userId and username
                                                    UserSessionManager.getInstance().setUserId(user.getUserId());
                                                    UserSessionManager.getInstance().setUsername(user.getUsername());

                                                    // loads itinerary from firebase
                                                    loadUserItinerary(user.getUserId());

                                                    startActivity(i);
                                                } else {
                                                    Toast.makeText(getActivity(), "Logging in with: " + email,
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getActivity(), "Error fetching user info: database error", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(getActivity(), "Login Failed: User not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Login failed: email or password was wrong.", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else {
                    Toast.makeText(getActivity(), "Please enter email and password",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // returns to parent i.e. the login activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
            }
        });

        // sends email to reset password
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(getActivity(),
                            "Enter your email in the field first", Toast.LENGTH_LONG).show();
                    return;
                }

                sendPasswordReset(email);
            }
        });

        return view;
    }

    // loads itinerary from the firebase
    private void loadUserItinerary(String userId) {
        UserSessionManager.getInstance().setUserId(userId); // sets the userId
        // gets username and sets it
        UserSessionManager.getInstance().setUsername(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("itineraries") // gets itinerary collection
                .document(userId)// gets the itinerary by the userId
                .get() // call to get
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) { // if it exists
                        ArrayList<String> savedItinerary = (ArrayList<String>) documentSnapshot.get("items");
                        if (savedItinerary != null) {
                            // Merge Firebase itinerary with local (anonymous) itinerary
                            UserSessionManager.getInstance().setItineraryList(savedItinerary);
                        }
                    } else {
                        // No itinerary exists yet for this user; create empty
                        saveItineraryToFirestore();
                    }
                });
    }

    // save the itinerary to the fire store
    private void saveItineraryToFirestore() {
        if (!UserSessionManager.getInstance().isLoggedIn()) return; // only save if logged in

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = UserSessionManager.getInstance().getUserId();

        // creates the fields for the document
        Map<String, Object> data = new HashMap<>();
        data.put("items", UserSessionManager.getInstance().getItineraryList());
        data.put("timestamp", System.currentTimeMillis());

        db.collection("itineraries")
                .document(userId)
                .set(data, SetOptions.merge())// sets the data, merges with the current document
                .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "Itinerary saved successfully"))
                .addOnFailureListener(e -> Log.e("FIRESTORE", "Error saving itinerary", e));
    }

    // sends password reset email
    private void sendPasswordReset(String email) {
        fba.sendPasswordResetEmail(email) // sends to the provided email the password reset
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(),
                            "Reset email sent!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(),
                            "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                }
            });
    }
}