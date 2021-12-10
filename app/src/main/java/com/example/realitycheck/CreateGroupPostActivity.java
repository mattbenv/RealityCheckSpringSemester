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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.realitycheck.databinding.ActivityCreateGroupPostBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateGroupPostActivity extends Fragment {
    private ActivityCreateGroupPostBinding binding;
    public String postId;
    public static Post newPost;
    public static Group thisGroup;

    private String imagePath;
    private Uri image;
    static final int REQUEST_TAKE_PHOTO = 123;
    private static final int CAMERA_PIC_REQUEST = 1337;

    private static final int PICK_IMAGE_REQUEST = 22;




    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  ActivityCreateGroupPostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvTitle.setText(LoginPage.currUser.username);

        binding.ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(CreateGroupPostActivity.this)
                        .navigate(R.id.action_CreateGroupPostActivity_to_ViewGroup);

            }

        });

        //click button to select image or gif to add to post
        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.imageView.setVisibility(View.VISIBLE);
                selectImage();

            }
        });

        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //sets up the name of the new posts to be created

                int numposts = thisGroup.posts.size();
                if(numposts == 0) {
                    postId = thisGroup.groupName + "_Post_" + numposts;
                }
                else {
                    String lastNumberedPost = thisGroup.posts.get(numposts - 1);
                    String numberOfNewPost = lastNumberedPost.substring(lastNumberedPost.length() - 1);
                    postId = thisGroup.groupName + "_Post_" + (Integer.parseInt(numberOfNewPost) + 1);

                }
                uploadImage(postId);
                createPost(postId);
                NavHostFragment.findNavController(CreateGroupPostActivity.this)
                        .navigate(R.id.action_CreateGroupPostActivity_to_ViewGroup);


            }
        });

        binding.takeimageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                binding.imageView.setVisibility(View.VISIBLE);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });

        //sets profile picture

        ImageView imageView = binding.sivAvatar;
        Glide.with(this.getContext())
                .load(LoginPage.storageProfilePictureReference)
                .into(imageView);




    }


    public void createPost(String postId){
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        Post post;
        if(imagePath!=null){
            post = new ImagePost(LoginPage.currUser.username, currentDateTimeString,imagePath);
        }
        else {
            post = new TextPost(LoginPage.currUser.username, currentDateTimeString);

        }
        post.setPostId(postId);
        //Login.currUser stores the current user logged in
        post.setPostAuthor(LoginPage.currUser.username);
        post.setContent(binding.tvContent.getText().toString());
        post.setPostDate(currentDateTimeString);



        ArrayList<String> likeList = new ArrayList<>();
        post.setLikedBy(likeList);
        ArrayList<HashMap<String,String>> repostList = new ArrayList<HashMap<String,String>>();
        post.setRepostedBy(repostList);
        ArrayList<Comment> comments = new ArrayList<>();
        post.setComments(comments);
        //PostActivity.postAdapter.addData(post);

        //create post
        DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(String.valueOf(postId));
        Map<String, Object> currPost = new HashMap<>();
        currPost.put("postAuthor", LoginPage.currUser.username);
        currPost.put("postId", postId);
        currPost.put("postDate", java.text.DateFormat.getDateTimeInstance().format(new Date()));
        currPost.put("likeCount", 0);
        currPost.put("likedBy", post.getLikedBy());
        currPost.put("repostCount", 0);
        currPost.put("repostedBy",post.getRepostedBy());
        currPost.put("content", post.getContent());
        currPost.put("comments",post.getComments());
        currPost.put("commentCount",0);
        currPost.put("photo",imagePath);
        document.set(currPost);

        //add post id to user posts feild
        thisGroup.posts.add(postId);
        FirebaseFirestore.getInstance().collection("Groups").document(thisGroup.groupName).update("posts", thisGroup.posts).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
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

        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Bitmap newImage = (Bitmap) data.getExtras().get("data");
            binding.imageView.setImageBitmap(newImage);
            Uri filePath = data.getData();
            image = filePath;
            imagePath = filePath.toString();



        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            image = filePath;
            imagePath = filePath.toString();
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
    private void uploadImage(String postId) {
        //creates a profile image path for the newly uploaded image
        if(image!=null) {
            imagePath = postId + "_image";
        }
        final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setTitle("Uploading...");
        if(image!= null) {
            progressDialog.show();
            //adds image to the database
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/"+ imagePath);
            ref.putFile(image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateGroupPostActivity.this.getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateGroupPostActivity.this.getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
