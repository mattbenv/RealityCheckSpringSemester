package com.example.realitycheck;
//https://firebase.google.com/docs/auth/android/google-signin?utm_source=studio
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.realitycheck.databinding.WelcomeBinding;
import com.example.realitycheck.databinding.SignupBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpPage extends Fragment {

    private SignupBinding binding;
    private FirebaseAuth mAuth;
    private EditText email, username, password, confirmpassword;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        mAuth = FirebaseAuth.getInstance();
        binding = SignupBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        email = binding.email;
        username = binding.username;
        password = binding.password;
        confirmpassword = binding.confirmpassword;

        super.onViewCreated(view, savedInstanceState);

        binding.createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();

            }
        });

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SignUpPage.this)
                        .navigate(R.id.action_SignUpPage_to_WelcomePage);
            }
        });


    }

    public void registerUser(){
        //gets string values
        String emailValue = email.getText().toString().trim();
        String usernameValue =username.getText().toString().trim();
        String passwordValue =password.getText().toString().trim();
        String confirmedpasswordValue = confirmpassword.getText().toString().trim();

        //test format of information
        if(emailValue.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
            return;
        }
        if(usernameValue.isEmpty()){
            username.setError("Username is required");
            username.requestFocus();
            return;
        }
        if(passwordValue.length()<6){
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }
        if(confirmedpasswordValue.isEmpty()){
            confirmpassword.setError("Please confirm your password");
            confirmpassword.requestFocus();
            return;

        }

        if(confirmedpasswordValue.compareTo(passwordValue)!=0){
            confirmpassword.setError("Passwords do not match");
            confirmpassword.requestFocus();
        }


        //Authenticate and add user to database

        mAuth.createUserWithEmailAndPassword(emailValue,passwordValue)
                .addOnCompleteListener(SignUpPage.this.getActivity(),new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(emailValue,usernameValue,new ArrayList<Post>(),new ArrayList<User>() , new ArrayList<User>(),new ArrayList<User>());
                            FirebaseDatabase.getInstance().getReference()
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user);


                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Account created successfully!")
                                    .setMessage("Please login")
                                    .setCancelable(false)
                                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            NavHostFragment.findNavController(SignUpPage.this)
                                                    .navigate(R.id.action_SignUpPage_to_WelcomePage);
                                        }
                                    }).show();


                                }
                            }

                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}