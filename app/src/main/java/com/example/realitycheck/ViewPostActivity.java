package com.example.realitycheck;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.realitycheck.adapter.CommentAdapter;
import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.databinding.ActivityViewPostBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ViewPostActivity  extends Fragment {
    private ActivityViewPostBinding binding;
    public ArrayList<Comment> list;
    public CommentAdapter commentAdapter;
    public Post currPost;
    public static int savedPosition;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  ActivityViewPostBinding.inflate(inflater, container, false);
        currPost = PostAdapter.post;
        binding.username.setText(currPost.getPostAuthor());
        binding.date.setText(currPost.getPostDate());
        binding.tvContent.setText(currPost.getContent());
        binding.commentCount.setText(String.valueOf(currPost.getCommentCount()));
        binding.likeCount.setText(String.valueOf(currPost.getLikeCount()));
        binding.repostCount.setText(String.valueOf(currPost.getRepostCount()));

        list = new ArrayList<Comment>();
        commentAdapter = new CommentAdapter(this.getContext(),list);



        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creates new comment
                DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(currPost.getPostId());
                int comments = currPost.getCommentCount()+1;
                currPost.setCommentCount(comments);
                document.update("commentCount",comments);
                Comment newComment = new Comment();
                newComment.setCommentAuthor(LoginPage.currUser.username.toString());
                newComment.setCommentContent(binding.createComment.getText().toString());
                newComment.setCommentDate(java.text.DateFormat.getDateTimeInstance().format(new Date()));
                currPost.addComment(newComment);
                document.update("comments",currPost.getComments());
                commentAdapter.addData(newComment);
                binding.commentCount.setText(String.valueOf(comments));
                binding.comment.scrollToPosition(0);



            }
        });
        binding.heart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLike(currPost);
            }
        });

        binding.ivCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRepost(currPost);
            }
        });


        //sets users profile photo on posts
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(currPost.getPostAuthor());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> userMap = documentSnapshot.getData();
                String profileImagePath = userMap.get("profileImagePath").toString();
                // Reference to an image file in Cloud Storage
                FirebaseStorage.getInstance().getReference().child("images/"+profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri postAuthorProfilePhoto = uri;
                        ImageView imageView = binding.sivAvatar;
                        Glide.with(getContext())
                                .load(postAuthorProfilePhoto)
                                .into(imageView);
                    }
                });
            }
        });
        setComments();

        return binding.getRoot();
    }



    public void setComments(){
        DocumentReference postDoc = FirebaseFirestore.getInstance().collection("Posts").document(currPost.getPostId());
        postDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot querySnapshot) {
                Post post = new TextPost();
                ArrayList<HashMap<String,Object>> commentsMap = (ArrayList<HashMap<String, Object>>) querySnapshot.get("comments");
                ArrayList<Comment> comments = new ArrayList<>();
                for(HashMap comment :commentsMap){
                    Comment eachComment = new Comment(comment.get("commentAuthor").toString(),comment.get("commentContent").toString(),comment.get("commentDate").toString());
                    comments.add(eachComment);
                }
                list.addAll(comments);
                commentAdapter.notifyDataSetChanged();
            }

        });
        binding.comment.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.comment.setHasFixedSize(true);
        binding.comment.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));
        binding.comment.setAdapter(commentAdapter);


    }


    public void handleRepost(Post post){

        DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(post.getPostId());
        int reposts;
        if(!post.getRepostedBy().contains(LoginPage.currUser.username)){
            reposts = post.getRepostCount()+1;
            post.setRepostCount(reposts);
            document.update("repostCount",reposts);

            binding.repostCount.setText(Integer.toString(reposts));
            post.addToRepostedBy(LoginPage.currUser.username);
            document.update("repostedBy",post.getRepostedBy());
        }
        //unlike
        else if(post.getRepostedBy().contains(LoginPage.currUser.username)){
            reposts = post.getRepostCount()-1;
            post.setRepostCount(reposts);
            post.removeFromRepostedBy(LoginPage.currUser.username);
            document.update("repostedBy",post.getRepostedBy());
            document.update("repostCount",reposts);
            binding.repostCount.setText(Integer.toString(reposts));
        }

    }





    //adds like to post in database and on the view
    public void handleLike(Post post){
        LottieAnimationView animationView  = binding.heart1;
        DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(post.getPostId());
        //commented out for now but limits to one like per user
        int likes;
        if(!post.getLikedBy().contains(LoginPage.currUser.username)){
            likes = post.getLikeCount()+1;
            post.setLikeCount(likes);
            document.update("likeCount",likes);
            //Somewehere in here i want to use this animation when a post is liked.

            // Declaring the animation view
            animationView
                    .addAnimatorUpdateListener(
                            (animation) -> {
                                // Do something.
                            });
            animationView
                    .playAnimation();

            if (animationView.isAnimating()) {
                // Do something.
            }
            binding.likeCount.setText(Integer.toString(likes));
            post.addToLikedBy(LoginPage.currUser.username);
            document.update("likedBy",post.getLikedBy());
        }
        //unlike
        else if(post.getLikedBy().contains(LoginPage.currUser.username)){
            //need to remove like animation in here
            likes = post.getLikeCount()-1;
            post.setLikeCount(likes);
            post.removeFromLikedBy(LoginPage.currUser.username);
            document.update("likedBy",post.getLikedBy());
            document.update("likeCount",likes);
            binding.likeCount.setText(Integer.toString(likes));
        }
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}







