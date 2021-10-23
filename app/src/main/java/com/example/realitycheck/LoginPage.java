package com.example.realitycheck;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.realitycheck.databinding.LoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginPage extends Fragment{



    private LoginBinding binding;
    private FirebaseAuth mAuth;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        //connection to firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //binding class with view
        binding = LoginBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //click login to call userLogin()
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserLogin();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void UserLogin(){
        String email = binding.username.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        Matcher m = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        //tests format of user login information
        if(email.isEmpty()){
            binding.username.setError("Please enter your username or email");
            binding.username.requestFocus();
            return;
        }
        if(m.find()==false){
            binding.username.setError("Invalid email address");
            binding.username.requestFocus();
            return;
        }
        if(password.isEmpty()){
            binding.password.setError("Please enter your password");
            binding.password.requestFocus();
            return;
        }

        //Authenticates with firebase and signs in user
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // go to post page
                    startActivity(new Intent(requireActivity(), PostActivity.class));
                    Toast.makeText(getContext(),"Successful login to account \n" , Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(),"Failed to login please try again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}

