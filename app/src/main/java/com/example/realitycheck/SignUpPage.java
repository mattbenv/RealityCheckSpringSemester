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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// we are using firebase authentication to allow users to signup and collect their email/password
public class SignUpPage extends Fragment {

    private SignupBinding binding;
    private FirebaseAuth mAuth;
    public static EditText email, username, password, confirmpassword;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        mAuth = FirebaseAuth.getInstance();
        binding = SignupBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
    // binding connects the view to the class, and stores user-inputed information
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        email = binding.email;
        username = binding.username;
        password = binding.password;
        confirmpassword = binding.confirmpassword;

        super.onViewCreated(view, savedInstanceState);
        // this is to create an account
        binding.createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testInformation();
                if(testInformation()== false){
                    testInformation();
                }
                if(testInformation()==true){
                    registerUser();
                }
            }
        });



    }
    // this functions tests the validity of the credentials: including password length etc...
    public boolean testInformation(){
        //gets string values
        String emailValue = email.getText().toString().trim();
        String usernameValue =username.getText().toString().trim();
        String passwordValue =password.getText().toString().trim();
        String confirmedpasswordValue = confirmpassword.getText().toString().trim();
        Matcher m = VALID_EMAIL_ADDRESS_REGEX.matcher(emailValue);

        //test format of information
        if(emailValue.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
            return false;
        }
        if(m.find()==false){
            binding.email.setError("Invalid email address");
            binding.email.requestFocus();
            return false;
        }
        if(usernameValue.isEmpty()){
            username.setError("Username is required");
            username.requestFocus();
            return false;
        }
        if(passwordValue.length()<6){
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return false;
        }
        if(confirmedpasswordValue.isEmpty()){
            confirmpassword.setError("Please confirm your password");
            confirmpassword.requestFocus();
            return false;

        }

        if(confirmedpasswordValue.compareTo(passwordValue)!=0){
            confirmpassword.setError("Passwords do not match");
            confirmpassword.requestFocus();
            return false;
        }
        else{
            return true;
        }
    }
    // this gets our instance of the firebase authentication and authenticates user using their email/password
    public void registerUser(){

        //Authenticate and add user to database

        mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim())
                .addOnCompleteListener(SignUpPage.this.getActivity(),new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(binding.username.getText().toString())
                                    .build();

                            mAuth.getCurrentUser().updateProfile(profileUpdates);

                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Account created successfully!")
                                    .setMessage("Please continue building your profile")
                                    .setCancelable(false)
                                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            NavHostFragment.findNavController(SignUpPage.this)
                                                    .navigate(R.id.action_SignUpPage_to_SignUpPageContinued);
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