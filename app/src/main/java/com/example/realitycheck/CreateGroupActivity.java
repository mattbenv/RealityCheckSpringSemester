package com.example.realitycheck;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.realitycheck.databinding.CreateGroupBinding;
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

public class CreateGroupActivity extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 22;
    private FirebaseAuth mAuth;
    //public static EditText groupName, addMembers;

    public String groupName, bio, profileImagePath;
    private Uri profileImage;
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
        // this is to create a group
        binding.CreateGroupActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupName = binding.groupName.getText().toString().trim();
                bio = binding.addBio.getText().toString().trim();
                newGroupName = groupName;
                uploadImage();
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
        profileImagePath = groupName+ "_profile_picture" ;
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
                            Toast.makeText(CreateGroupActivity.this.getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateGroupActivity.this.getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}