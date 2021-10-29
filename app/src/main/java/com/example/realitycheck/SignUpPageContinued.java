
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


public class SignUpPageContinued extends Fragment {

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
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fStorage = FirebaseFirestore.getInstance();
        binding = SignupcontinuedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //allows for selection of picture from device
        binding.selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();

            }
        });
        //allows for upload of picture using camera
        binding.takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImage();
            }
        });
        //stores user uploaded image and creates new account when done is clicked
        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
                createdProfile();
            }
        });


    }

    public void createdProfile(){
        emailValue = SignUpPage.email.getText().toString().trim();
        usernameValue = SignUpPage.username.getText().toString().trim();
        nameValue = binding.name.getText().toString().trim();
        bioValue = binding.UserBio.getText().toString().trim();
        birthdateValue = binding.dateOfBirth.getText().toString().trim();

        // will use buttons to get photo input and save image path for now is left empty
        //profileImagePath = "empty profile image path";

        ArrayList<String> followers = new ArrayList<>();
        ArrayList<String> following = new ArrayList<>();
        following.add("TestUser");
        following.add("someusername");
        following.add("Testing123");
        following.add("newUsername");
        following.add("hellouser");
        following.add("name");
        following.add("oooooo");

        followers.add("T12345 username");
        followers.add("Testing123");
        followers.add("something");
        followers.add("TestUser22");
        followers.add("TestUser");

        User user = new User(emailValue,usernameValue,nameValue,bioValue,birthdateValue,profileImagePath,new ArrayList<String>(),followers , following,new ArrayList<User>());
       //test puposes adding foloowers and following

        DocumentReference document = fStorage.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Map<String,Object> currUser = new HashMap<>();
        currUser.put("email",user.email);
        currUser.put("username",user.username);
        currUser.put("name",user.name);
        currUser.put("bio",user.bio);
        currUser.put("birthday",user.birthday);
        currUser.put("profileImagePath",user.profileImagePath);
        currUser.put("posts",user.posts);
        currUser.put("followers",user.followers);
        currUser.put("following",user.following);
        currUser.put("friends",user.friends);
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
        /*
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Account was not created")
                        .setMessage("account creation failed")
                        .setCancelable(false)
                        .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            }
        });*/
    }



    public void takeImage(){


    }






    public void selectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            profileImage = filePath;
            profileImagePath = filePath.toString();
            //ImageView userImage = new ImageView();
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContext().getContentResolver(),
                                filePath);
                binding.imageView.setImageBitmap(bitmap);            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


    private void uploadImage() {
        if(profileImagePath != null)
        {


            UUID imageId = UUID.randomUUID();
            profileImagePath = imageId.toString();
            final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ imageId.toString());
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
                            Toast.makeText(SignUpPageContinued.this.getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
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