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
import com.example.realitycheck.databinding.ActivityCreatePostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends Fragment {
    private ActivityCreatePostBinding binding;
    public String postId;
    public static Post newPost;

    private String imagePath;
    private Uri image;
    private static final int PICK_IMAGE_REQUEST = 22;




    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  ActivityCreatePostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvTitle.setText(LoginPage.currUser.username);

        binding.ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(CreatePostActivity.this)
                        .navigate(R.id.action_CreatePostActivity_to_PostActivity);

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

                int numposts = LoginPage.currUser.posts.size();
                if(numposts == 0) {
                    postId = LoginPage.currUser.username + "_Post_" + numposts;
                }
                else {
                    String lastNumberedPost = LoginPage.currUser.posts.get(numposts - 1);
                    String numberOfNewPost = lastNumberedPost.substring(lastNumberedPost.length() - 1);
                    postId = LoginPage.currUser.username + "_Post_" + (Integer.parseInt(numberOfNewPost) + 1);

                }
                uploadImage(postId);
                createPost(postId);
                NavHostFragment.findNavController(CreatePostActivity.this)
                        .navigate(R.id.action_CreatePostActivity_to_PostActivity);


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
        ArrayList<String> allUsers = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot snapshot:task.getResult()){
                    allUsers.add(snapshot.get("username").toString());
                    for(String user:allUsers){
                        if(post.getContent().contains("@"+user)){
                            ArrayList<String> taggedIn = new ArrayList<>();
                            FirebaseFirestore.getInstance().collection("Users").document(user).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    taggedIn.addAll((Collection<? extends String>) documentSnapshot.get("taggedIn"));
                                }
                            });
                            taggedIn.add(post.getPostId());
                            FirebaseFirestore.getInstance().collection("Users").document(user).update("taggedIn",taggedIn);

                        }

                    }
                }
            }
        });



        ArrayList<String> likeList = new ArrayList<>();
        post.setLikedBy(likeList);
        ArrayList<String> repostList = new ArrayList<>();
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
        LoginPage.currUser.posts.add(postId);
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).update("posts", LoginPage.currUser.posts).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        imagePath = postId + "_image" ;
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
                            Toast.makeText(CreatePostActivity.this.getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CreatePostActivity.this.getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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