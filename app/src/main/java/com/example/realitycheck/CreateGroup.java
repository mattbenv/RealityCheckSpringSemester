package com.example.realitycheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.realitycheck.databinding.CreategroupBinding;
import com.example.realitycheck.databinding.SignupcontinuedBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;

public class CreateGroup extends Fragment {

    private FirebaseAuth mAuth;
    public static EditText groupname, addmembers;
    private CreategroupBinding binding;
    private StorageReference storageReference;
    private FirebaseFirestore fStorage;

   /*@Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_create_group);
   }*/

    /*@Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        //initialize storage reference to firebase
        storageReference = FirebaseStorage.getInstance().getReference(); //where we store images
        fStorage = FirebaseFirestore.getInstance(); //where we store group attributes
        binding = CreategroupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // binding connects the view to the class, and stores group-inputed information
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        groupname = binding.groupname;
        addmembers = binding.addmembers;

        super.onViewCreated(view, savedInstanceState);
        // this is to create an account
        binding.createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testInformation();
                if (testInformation() == false) {
                    testInformation();
                }
                if (testInformation() == true) {
                    registerUser();
                }
            }
        });


    }

    // this functions tests the validity of the credentials: including password length etc...
    public boolean testInformation(){
        //gets string values
        String groupnameValue = groupname.getText().toString().trim();
        String addmembersValue = addmembers.getText().toString().trim();

        //test format of information
        if(groupnameValue.isEmpty()){
            email.setError("Group Name is required");
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

    public void registerGroup(){

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

    }*/
}