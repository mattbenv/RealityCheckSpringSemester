package com.example.realitycheck.Activitys;

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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.realitycheck.R;
import com.example.realitycheck.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Map;

public class EditProfileActivity extends Fragment {
    private ActivityEditProfileBinding binding;
    private String profileImagePath;
    private Uri profileImage;
    private static final int PICK_IMAGE_REQUEST = 22;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  ActivityEditProfileBinding.inflate(inflater, container, false);


        DocumentReference dRef = FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username);
        dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> userMap = documentSnapshot.getData();
                String profileImagePath = userMap.get("profileImagePath").toString();
                // Reference to an image file in Cloud Storage
                FirebaseStorage.getInstance().getReference().child("images/"+profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageView imageView = binding.profilePic;
                        Glide.with(getContext())
                                .load(uri)
                                .into(imageView);
                    }
                });
            }
        });
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(EditProfileActivity.this)
                        .navigate(R.id.action_EditProfile_to_SettingsPage);
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profileImage!=null){
                    uploadImage();
                }
                LoginPage.currUser.bio = binding.userBio.getText().toString();
                LoginPage.currUser.name = binding.realName.getText().toString();
                profileImagePath = LoginPage.currUser.username+ "_profile_picture" ;
                LoginPage.currUser.profileImagePath = profileImagePath;
                FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username).update("profileImagePath", LoginPage.currUser.profileImagePath);
                FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username).update("name", LoginPage.currUser.name);
                FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username).update("bio", LoginPage.currUser.bio);
                Toast.makeText(EditProfileActivity.this.getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(EditProfileActivity.this)
                        .navigate(R.id.action_EditProfile_to_SettingsPage);
                FirebaseStorage.getInstance().getReference().child("images/" + profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        LoginPage.storageProfilePictureReference= uri;
                    }
                });
            }
        });

        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //this calls the method for selecting pics
                selectImage();

            }
        });


        binding.userBio.setText(LoginPage.currUser.bio);
        binding.realName.setText(LoginPage.currUser.name);

        return binding.getRoot();
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



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
                binding.profilePic.setImageBitmap(bitmap);            }

            // when image selction fails
            //todo: write an exception that is printed when user denies access to gallery
            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


    private void uploadImage() {
        //creates a profile image path for the newly uploaded image
        profileImagePath = LoginPage.currUser.username+ "_profile_picture" ;
        FirebaseStorage.getInstance().getReference().child("images/"+ profileImagePath).delete();
        final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setTitle("Uploading...");
                    progressDialog.show();
            //adds image to the database
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/"+ profileImagePath);
            ref.putFile(profileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this.getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this.getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
