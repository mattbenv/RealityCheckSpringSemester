package com.example.realitycheck;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ViewPostActivity  extends Fragment {


    public static ActivityViewPostBinding binding;
    public ArrayList<Comment> list;
    public CommentAdapter commentAdapter;
    public static Post currPost;
    public static int savedPosition;
    public int reposts;
    public static String previous;
    public static Post savedPost;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  ActivityViewPostBinding.inflate(inflater, container, false);
        if(previous == "otherprofile"){
            currPost = savedPost;
        }
        else {
            currPost = PostAdapter.post;
        }
        binding.username.setText(currPost.getPostAuthor());
        binding.date.setText(currPost.getPostDate());
        binding.tvContent.setText(currPost.getContent());
        binding.commentCount.setText(String.valueOf(currPost.getCommentCount()));
        binding.likeCount.setText(String.valueOf(currPost.getLikeCount()));
        binding.repostCount.setText(String.valueOf(currPost.getRepostCount()));

        //if the post has an image or gif than it gets loaded here
        if(currPost.getPhoto()!=null){
            FirebaseStorage.getInstance().getReference().child("images/" + currPost.getPhoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    ImageView imageView = binding.imageView;
                    imageView.setVisibility(View.VISIBLE);
                    Glide.with(getContext())
                            .load(uri)
                            .into(imageView);
                }
            });
        }


        list = new ArrayList<Comment>();
        commentAdapter = new CommentAdapter(this.getContext(),list);

        setComments();
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ViewPostActivity.this)
                        .navigate(R.id.action_ViewPostActivity_to_PostActivity);
            }
        });

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
                binding.createComment.setText("");
                binding.comment.scrollToPosition(0);
                commentNotifications(currPost.getPostId());



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
                String realName = userMap.get("name").toString();
                binding.realName.setText(realName);
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
                    list.add(0, eachComment);
                }
                commentAdapter.notifyDataSetChanged();
            }

        });
        binding.comment.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.comment.setHasFixedSize(true);
        binding.comment.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));
        binding.comment.setAdapter(commentAdapter);


    }


    public void handleRepost(Post post){

        DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(post.getPostId().toString());

        ArrayList<String> repostNames = new ArrayList<>();
        for(HashMap a :post.getRepostedBy()){
            repostNames.add((String) a.get("username"));
        }
        System.out.println(repostNames);
        if (!repostNames.contains(LoginPage.currUser.username)){
            reposts = post.getRepostCount()+1;
            post.setRepostCount(reposts);
            document.update("repostCount",reposts);
            if(LoginPage.currUser.reposted != null){
                LoginPage.currUser.reposted.add(post.getPostId());

            }
            FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username).update("reposted",LoginPage.currUser.reposted);

            binding.repostCount.setText(Integer.toString(reposts));
            HashMap<String,String> repost = new HashMap();
            repost.put("username",LoginPage.currUser.username);
            repost.put("time",java.text.DateFormat.getDateTimeInstance().format(new Date()));
            post.addToRepostedBy(repost);
            document.update("repostedBy",post.getRepostedBy());
            repostNotifications(post.getPostId());


        }
        if(repostNames.contains(LoginPage.currUser.username)){
            reposts = post.getRepostCount()-1;
            if(reposts<=0){
                reposts = 0;
                post.setRepostedBy(new ArrayList<HashMap<String,String>>());
            }
            post.setRepostCount(reposts);
            post.removeFromRepostedBy(LoginPage.currUser.username);
            LoginPage.currUser.reposted.remove(post.getPostId());
            FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username).update("reposted",LoginPage.currUser.reposted);
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
            likeNotifications(post.getPostId());
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


    public void likeNotifications(String postID){
        Intent intent = new Intent(getContext(),ViewPostActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
        if(LoginPage.currUser.posts.contains(postID)){
            DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(postID.toString());
            document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ArrayList<String> likedBy = (ArrayList<String>) documentSnapshot.get("likedBy");
                    String likee = likedBy.get(likedBy.size()-1);
                    Notification likeNotification = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        LocalTime now = LocalTime.now();

                        DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("hh:mm:ss a");
                        likeNotification = new Notification.Builder(getContext(),"my_channel_01")
                                .setContentTitle(likee+ " liked your post at "+ now.format(dtf3))
                                .setContentText("Like Received")
                                .setContentIntent(pendingIntent)
                                .setSmallIcon(R.drawable.ic_like).build();
                    }

                    PostActivity.mNotificationManager.notify(getNotificationID(),likeNotification);
                }
            });

        }
    }

    public int getNotificationID(){
        return ThreadLocalRandom.current().nextInt();
    }


    public void repostNotifications(String postID){
        if(LoginPage.currUser.posts.contains(postID)){
            DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(postID.toString());
            document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ArrayList<String> repostedBy = (ArrayList<String>) documentSnapshot.get("repostedBy");
                    String repostee = repostedBy.get(repostedBy.size()-1);
                    Notification repostNotification = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        LocalTime now = LocalTime.now();
                        DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("hh:mm:ss a");
                        repostNotification = new Notification.Builder(getContext(),"my_channel_01")
                                .setContentTitle(repostee+ " reposted your post at "+ now.format(dtf3))
                                .setContentText("Repost Received")
                                .setSmallIcon(R.drawable.ic_cycle).build();
                    }
                    PostActivity.mNotificationManager.notify(getNotificationID(),repostNotification);
                }
            });

        }
    }
    public void commentNotifications(String postID){
        if(LoginPage.currUser.posts.contains(postID)){
            DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(postID.toString());
            document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ArrayList<HashMap<String,Object>> comments = (ArrayList<HashMap<String,Object>>) documentSnapshot.get("comments");
                    HashMap mostRecentComment = comments.get(comments.size()-1);
                    Notification repostNotification = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        LocalTime now = LocalTime.now();
                        DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("hh:mm:ss a");
                        repostNotification = new Notification.Builder(getContext(),"my_channel_01")
                                .setContentTitle(mostRecentComment.get("commentAuthor") + " commented: "+ mostRecentComment.get("commentContent") + " at "+ mostRecentComment.get("commentDate"))
                                .setContentText("Comment Received")
                                .setSmallIcon(R.drawable.ic_review).build();
                    }
                    PostActivity.mNotificationManager.notify(getNotificationID(),repostNotification);
                }
            });

        }
    }




    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedPost = currPost;
        binding = null;
    }



}



