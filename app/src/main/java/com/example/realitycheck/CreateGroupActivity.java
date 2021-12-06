package com.example.realitycheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.realitycheck.databinding.CreateGroupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends Fragment {

    private FirebaseAuth mAuth;
    //public static EditText groupName, addMembers;

    public String groupName, bio, profileImagePath;
    public boolean privacy; //true is private, false is public
    public int size; //number of members
    public ArrayList<String> members = new ArrayList<>();
    public ArrayList<String> posts = new ArrayList<>();
    public ArrayList<Post> postLiked = new ArrayList<>();
    public ArrayList<Post> reposted = new ArrayList<>();

    private CreateGroupBinding binding;
    public static String newGroupName;
    private StorageReference storageReference;
    private FirebaseFirestore fStorage;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        //initialize storage reference to firebase
        storageReference = FirebaseStorage.getInstance().getReference(); //where we store images
        fStorage = FirebaseFirestore.getInstance(); //where we store group attributes
        binding = CreateGroupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // binding connects the view to the class, and stores group-inputted information
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // this is to create a group
        binding.CreateGroupActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupName = binding.groupName.getText().toString().trim();
                bio = binding.addBio.getText().toString().trim();
                newGroupName = groupName;
             //   members.add(binding.addMembers.getText().toString().trim()); // need to fix
                //profileImagePath =;
                privacy = binding.setSecurity.isChecked();
                if(groupName.isEmpty()){
                    binding.groupName.setError("Group name cannot be empty");
                    binding.groupName.requestFocus();
                }
                if(bio.isEmpty()){
                    binding.addBio.setError("Group bio cannot be empty");
                    binding.groupName.requestFocus();
                }
                if (!groupName.isEmpty() && !bio.isEmpty()) {
                    Group group = new Group(groupName, bio, profileImagePath, privacy, posts, members);
                    DocumentReference document = fStorage.collection("Groups").document(groupName);
                    Map<String, Object> currGroup = new HashMap<>();
                    currGroup.put("bio", group.bio);
                    currGroup.put("groupName", group.groupName);
                    currGroup.put("members", group.members);
                    currGroup.put("posts",group.posts);
                    currGroup.put("privacy", group.privacy);
                    currGroup.put("profileImagePath", group.profileImagePath);
                    currGroup.put("size", 0);
                    document.set(currGroup);
                    NavHostFragment.findNavController(CreateGroupActivity.this)
                            .navigate(R.id.action_CreateGroupActivity_to_AddGroupMembers);
                }

              
            }
        });

        binding.setSecurity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.setSecurity.setText("Private Group On");
                } else {
                    binding.setSecurity.setText("Private Group Off");

                }
            }
        });


    }

    // this functions tests the validity of the credentials: including password length etc...
    /*public boolean testInformation(){
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