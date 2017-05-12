package edu.sdsu.androidfinal.androidfinalproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class SignIn extends Fragment {

    private static final String TAG = "AM: ";
    private EditText email, password;
    private Button signin;
    private TextView pageTitle, signup;
    private String userLevel;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ProgressBar progressBar;

    private Spinner userRole;

    public SignIn() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View userView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //databaseReference = firebaseDatabase.getReference("role");

        email = (EditText) userView.findViewById(R.id.sign_in_email);
        password = (EditText) userView.findViewById(R.id.sign_in_password);

        pageTitle = (TextView) userView.findViewById(R.id.user_sign_in_page_title);
        signup = (TextView) userView.findViewById(R.id.new_user);

        progressBar = (ProgressBar) userView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        signin = (Button) userView.findViewById(R.id.sign_in_button);

        signup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Fragment signUpFragment = new RegisterUser();
                Bundle userInfo = new Bundle();
                if ((!email.getText().toString().isEmpty()) ||
                        (!password.getText().toString().isEmpty())) {
                    userInfo.putString("email", email.getText().toString());
                    userInfo.putString("password", password.getText().toString());
                    signUpFragment.setArguments(userInfo);
                }
                getFragmentManager().beginTransaction().
                        replace(R.id.content_frame, signUpFragment)
                        .addToBackStack(null).commit();
                return false;
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                validateUserData();
                String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();

                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if(!task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Sign Up Failed, Try Again", Toast.LENGTH_SHORT).show();
                                } else {
                                    databaseReference = firebaseDatabase.getReference("users");
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            User user = dataSnapshot.getValue(User.class);
                                            System.out.println(user);
                                            String userRole = user.role;
                                            loadCityMap(userRole);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            System.out.println("read failed");
                                            Log.e(TAG, "Read Failed");

                                        }
                                    });
                                }
                            }
                        });

            }
        }) ;
        return userView;
    }

    private void validateUserData() {
        if (email.getText().toString().isEmpty() || email == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
        }
        if (password.getText().toString().isEmpty() || password == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
        }
    }
    public void loadCityMap(String userLevel) {
        Fragment fragment = new MapForRegularUser();

        if (userLevel == "Veteran") {
            fragment = new MapForVeterans();
        } else if (userLevel == "Volunteer") {
            fragment = new MapForVolunteers();
        }
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment)
                .addToBackStack(null).commit();
    }
}
