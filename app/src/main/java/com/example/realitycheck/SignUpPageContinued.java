
package com.example.realitycheck;


import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.realitycheck.databinding.SignupcontinuedBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class SignUpPageContinued extends Fragment{

    private static final int PICK_IMAGE_REQUEST = 22;
    private SignupcontinuedBinding binding;
    private String emailValue;
    private String usernameValue;
    private String nameValue;
    private String bioValue;
    private String birthdateValue;
    private String profileImagePath;
    private Uri profileImage;
    private FirebaseFirestore fStorage;


    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        //initialize storage reference to firebase
        storageReference = FirebaseStorage.getInstance().getReference(); //where we store images
        fStorage = FirebaseFirestore.getInstance(); //where we store users and posts
        binding = SignupcontinuedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //gets values from the signuppage/continuedpage
        emailValue = SignUpPage.email.getText().toString().trim();
        usernameValue = SignUpPage.username.getText().toString().trim();
        nameValue = binding.name.getText().toString().trim();
        bioValue = binding.UserBio.getText().toString().trim();
        birthdateValue = binding.dateOfBirth.getText().toString().trim();


        //allows for selection of picture from device
        binding.selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //this calls the method for selecting pics
                selectImage();

            }
        });
        //allows for upload of picture using camera
        binding.takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImage();
            }
        }); //this calls the method for taking pics
        //stores user uploaded image and creates new account when done is clicked
        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //this uploads the pic we selected/took and create profile
                uploadImage();
                createdProfile();
            }
        });



    }

    public void createdProfile(){

        // will use buttons to get photo input and save image path for now is left empty
        //profileImagePath = "empty profile image path";

        ArrayList<String> followers = new ArrayList<>();
        ArrayList<String> following = new ArrayList<>();


        followers.add("T12345 username");
        followers.add("Testing123");
        followers.add("something");
        followers.add("TestUser22");
        followers.add("TestUser");

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        User user = new User(uid,emailValue,usernameValue,nameValue,bioValue,birthdateValue,profileImagePath,new ArrayList<String>(),followers , following,new ArrayList<String>());
        //test puposes adding foloowers and following

        //ran
        //this adds user to the database in the user collection
        DocumentReference document = fStorage.collection("Users").document(usernameValue);
        Map<String,Object> currUser = new HashMap<>(); //corresponding each value to the actual value
        currUser.put("email",user.email);
        currUser.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        currUser.put("username",user.username);
        currUser.put("name",user.name);
        currUser.put("bio",user.bio);
        currUser.put("birthday",user.birthday);
        currUser.put("profileImagePath",user.profileImagePath);
        currUser.put("posts",user.posts);
        currUser.put("followers",user.followers);
        currUser.put("following",user.following);
        currUser.put("friends",user.friends);
        // on success of user added to database: message to take you to login page appears
        document.set(currUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Profile setup is completed!")
                        .setMessage("Please login to your account")
                        .setCancelable(false)
                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavHostFragment.findNavController(SignUpPageContinued.this)
                                        .navigate(R.id.action_SignUpPageContinued_to_LogInPage);
                            }
                        }).show();
            }
        });

    }


    //todo: allow for users to use camera to take a picture
    public void takeImage(){

    }





    //allows user to select an image from their device
    public void selectImage()
    {
        Intent intent = new Intent(); // intent = screen
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    //this is called everytime when selectImage() is called
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            profileImage = filePath;
            profileImagePath = filePath.toString();
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContext().getContentResolver(),
                                filePath);
                binding.imageView.setImageBitmap(bitmap);            }

            // when image selction fails
            //todo: write an exception that is printed when user denies access to gallery
            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // this uploads image to the database
    private void uploadImage() {
        //creates a profile image path for the newly uploaded image
        profileImagePath = usernameValue+ "_profile_picture" ;
        final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setTitle("Uploading...");
        if(profileImage!= null) {
            progressDialog.show();
            //adds image to the database
            StorageReference ref = storageReference.child("images/"+ profileImagePath);
            ref.putFile(profileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpPageContinued.this.getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpPageContinued.this.getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
