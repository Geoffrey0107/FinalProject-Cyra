package edu.fandm.enovak.finalproject_cyra;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the @link RegisterFragment#newInstance factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText usernameEditText;
    private Button regButton;
    private Button backButton;
    private FirebaseAuth fba; // firebase auth
    private FirebaseFirestore db; // firebase database

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * param param1 Parameter 1.
     * param param2 Parameter 2.
     * return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static RegisterFragment newInstance(String param1, String param2) {
//        RegisterFragment fragment = new RegisterFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        emailEditText = (EditText) view.findViewById(R.id.reg_email);
        passwordEditText = (EditText) view.findViewById(R.id.reg_password);
        usernameEditText = (EditText) view.findViewById(R.id.reg_username);
        regButton = (Button) view.findViewById(R.id.reg_submit);
        backButton = (Button) view.findViewById(R.id.back_button_register);

        fba = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String username = usernameEditText.getText().toString();

                if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    // ADD FIREBASE REGISTER LOGIC HERE

                    // first check if password follows rules

                    // is it more than 10
                    if (password.length() < 10) {
                        Toast.makeText(getActivity(), "password is less than 10 characters",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // does it have any capitals
                    if (!containsCapital(password)) {
                        Toast.makeText(getActivity(), "password does not contain one or more capitals",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // does it have any lowercase letters
                    if (!containsLower(password)) {
                        Toast.makeText(getActivity(), "password does not contain one or more lowercase letters",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // does it have a number
                    if (!containsNum(password)) {
                        Toast.makeText(getActivity(), "password does not contain one or more numbers",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // does it have a special character
                    if (!containsSpecial(password)) {
                        Toast.makeText(getActivity(), "password does not contain one or more special characters (!@#$%^&*)",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // checks if username already exists
                    db.collection("users")
                            .whereEqualTo("username", username)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) { // username exists
                                        Toast.makeText(getActivity(), "Username already exists", Toast.LENGTH_SHORT).show();
                                    } else { // username does not exist
                                        createFirebaseUser(email, password, username); //creates user
                                    }
                                } else { // an internal error happened
                                    Toast.makeText(getActivity(), "Error checking username", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "A field was left blank or invalid",
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

        return view;
    }

    // checks if string contains a capital
    private boolean containsCapital(String pass) {
        for (char c : pass.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    // checks if string contains a lowercase
    private boolean containsLower(String pass) {
        for (char c : pass.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    // checks if string contains a number
    private boolean containsNum(String pass) {
        for (char c : pass.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    // checks if string contains a special character
    private boolean containsSpecial(String pass) {
        for (char c : pass.toCharArray()) {
            if ("!@#$%^&*".indexOf(c) >= 0) { // will return greater than 0 if c is a spec char
                return true;
            }
        }
        return false;
    }

    // creates user in firebase with the given email, password
    // creates user object in firebase with username, uid from user account from auth, and timestamp
    private void createFirebaseUser(String email, String password, String username) {
        fba.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) { // if creation was successful

                        FirebaseUser firebaseUser = fba.getCurrentUser();

                        if (firebaseUser != null) { // create firebase user here

                            // send verification email
                            firebaseUser.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                                if (verifyTask.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Verification email sent to " +
                                            firebaseUser.getEmail(), Toast.LENGTH_LONG).show();

                                    // go to pending verification activity for verification
                                    Intent i = new Intent(getActivity(), PendingVerificationActivity.class);
                                    i.putExtra("username", username); // sends username
                                    startActivity(i);

                                } else { // failure message
                                    Toast.makeText(getActivity(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error creating account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}