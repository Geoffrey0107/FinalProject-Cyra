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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private FirebaseAuth fba;
    private FirebaseFirestore db;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * EditText, emailEditText, passswordEditText
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

        emailEditText = (EditText) view.findViewById(R.id.login_email);
        passwordEditText = (EditText) view.findViewById(R.id.login_password);
        loginButton = (Button) view.findViewById(R.id.login_submit);
        backButton = (Button) view.findViewById(R.id.back_button_login);

        fba = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {

                    // firebase logic to get user from database
                    fba.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            FirebaseUser firebaseUser = fba.getCurrentUser();

                                            if (firebaseUser != null) {
                                                String uid = firebaseUser.getUid();
                                                // Fetch user object from Firestore
                                                db.collection("users").document(uid)
                                                        .get()
                                                        .addOnSuccessListener(documentSnapshot -> {
                                                            if (documentSnapshot.exists()) {
                                                                User user = documentSnapshot.toObject(User.class);

                                                                // login successful
                                                                Toast.makeText(getActivity(), "Logging in with: " + email,
                                                                        Toast.LENGTH_SHORT).show();

                                                                // launch test Activity
//
//                                                            Intent i = new Intent(getActivity(), TestActivity.class);
//                                                            i.putExtra(TestActivity.EXTRA_USER_ID, user.getUserId());
//                                                            i.putExtra(TestActivity.EXTRA_USERNAME, user.getUsername());
//                                                            startActivity(i);
                                                            } else {
                                                                Toast.makeText(getActivity(), "Logging in with: " + email,
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(getActivity(), "Error fetching user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        });
                                            } else {
                                                Toast.makeText(getActivity(), "Login Failed: ", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

        return view;
    }
}