package edu.fandm.enovak.finalproject_cyra;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    Toast.makeText(getActivity(), "Account Successfully Created!",
                            Toast.LENGTH_SHORT).show();
                    // ADD FIREBASE REGISTER LOGIC HERE
                } else {
                    // password and username logic
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