package com.example.realitycheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.realitycheck.databinding.ActivityCreatePostBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends Fragment {
    private ActivityCreatePostBinding binding;
    public String postId;
    public static Post newPost;




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

        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPost();
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


    public void createPost(){
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        Post post = new TextPost(LoginPage.currUser.username,currentDateTimeString);
        int numposts = LoginPage.currUser.posts.size();
        postId = LoginPage.currUser.username + "_Post_" + numposts;
        post.setPostId(postId);
        //Login.currUser stores the current user logged in
        post.setPostAuthor(LoginPage.currUser.username);
        post.setContent(binding.tvContent.getText().toString());


        post.setPostDate(currentDateTimeString);

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
        document.set(currPost);

        //add post id to user posts feild
        LoginPage.currUser.posts.add(postId);
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).update("posts", LoginPage.currUser.posts).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}