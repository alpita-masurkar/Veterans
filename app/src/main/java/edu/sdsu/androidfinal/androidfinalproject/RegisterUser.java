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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class RegisterUser extends Fragment {

    private static final String TAG = "AM: ";
    private EditText email, password, nickName;
    private Button signup;
    private TextView pageTitle, signin;
    private String userLevel;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ProgressBar progressBar;

    private Spinner userRole;

    public RegisterUser() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View userView = inflater.inflate(R.layout.fragment_register_user, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("role");


        email = (EditText) userView.findViewById(R.id.user_email);
        password = (EditText) userView.findViewById(R.id.user_password);
        nickName = (EditText) userView.findViewById(R.id.user_nickname);

        pageTitle = (TextView) userView.findViewById(R.id.user_registration_text);
        signin = (TextView) userView.findViewById(R.id.already_registered);

        progressBar = (ProgressBar) userView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        signup = (Button) userView.findViewById(R.id.sign_up_button);

        userRole = (Spinner) userView.findViewById(R.id.user_role);
        final List<String> userRoles = addItemsToSpinner("user_roles");
        if (userRoles.isEmpty()) {
            Log.e(TAG, "No user roles to select from");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, userRoles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userRole.setAdapter(adapter);

        userRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> userList, View view, int pos, long id) {
                userLevel = userList.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                userLevel = userRoles.get(1);
            }
        });

        signin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent arg1) {
                Fragment signInFragment = new SignIn();
                Bundle userInfo = new Bundle();
                if ((!email.getText().toString().isEmpty()) ||
                        (!password.getText().toString().isEmpty())) {
                    userInfo.putString("email", email.getText().toString());
                    userInfo.putString("password", password.getText().toString());
                    signInFragment.setArguments(userInfo);
                }
                getFragmentManager().beginTransaction().
                        replace(R.id.content_frame, signInFragment)
                        .addToBackStack(null).commit();
                return false;
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (validateUserData()) {
                    String userNickname = nickName.getText().toString().trim();
                    String userEmail = email.getText().toString().trim();
                    String userPassword = password.getText().toString().trim();

                    progressBar.setVisibility(View.VISIBLE);

                    firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Log In Failed. Try again", Toast.LENGTH_SHORT).show();
                                    } else {
                                        loadCityMap(userLevel, userRoles);
                                    }

                                }
                            });
                    User newUser = new User(userNickname, userEmail, userPassword, userLevel);
                    Map<String, Object> userValue = newUser.toMap();
                    Map<String, Object> newChild = new HashMap<>();

                    String regString = "[@.+$#//[//]]+";

                    newChild.put("user" + userEmail.replaceAll(regString,""), userValue);

                    databaseReference.push().child("users").setValue(newChild);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Invalid Data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return userView;
    }

    private List<String> addItemsToSpinner(String fileName) {
        List<String> userRoles = new ArrayList<>();
        BufferedReader reader = null;

        try {
            InputStream userFile = getActivity().getAssets().open(fileName);
            reader = new BufferedReader(new InputStreamReader(userFile));

            String role = reader.readLine();
            while(role != null) {
                userRoles.add(role);
                role = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Could not read file:" + e.getMessage());
            }
        }

        return userRoles;
    }

    private boolean validateUserData() {
        if (email.getText().toString().isEmpty() || email == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.getText().toString().isEmpty() || password == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (nickName.getText().toString().isEmpty() || nickName == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Enter nickname", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void loadCityMap(String userLevel, List<String> userRoles) {
        Fragment fragment = new MapForRegularUser();

        if (userLevel == userRoles.get(2)) {
            fragment = new MapForVeterans();
        } else if (userLevel == userRoles.get(3)) {
            fragment = new MapForVolunteers();
        }

        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment)
                .addToBackStack(null).commit();
    }
}
