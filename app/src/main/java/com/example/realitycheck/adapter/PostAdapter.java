package com.example.realitycheck.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.realitycheck.Post;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realitycheck.LoginPage;
import com.example.realitycheck.R;
import com.example.realitycheck.User;
import com.example.realitycheck.Post;
import com.example.realitycheck.databinding.ItemPostBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
//todo: understand how homepage works which is related to post adapter/viewholder/activity. if we can, design new one
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;

    public Uri postAuthorProfilePhoto;

    private ArrayList<Post> postList;
    int[] likeCount;
    public int likes;
    public int reposts;
    public int comments;

    public PostAdapter(Context context, ArrayList<Post> post) {
        this.context = context;

        postList = post;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false);
        PostViewHolder postViewHolder = new PostViewHolder(binding);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        //gets current post from the recycler view list based its position
        Post post = postList.get(position);

        //gets post values
        String content = post.getContent();
        String postID = post.getPostId();
        String currentDate = post.getPostDate();
        String author = post.getPostAuthor();
        likes = post.getLikeCount();
        ArrayList<String> likedBy = post.getLikedBy();
        //initialize
        likeCount = new int[1];
        likeCount[0] = likes;


        //displays values using item_post layout
        holder.binding.tvLike.setText(String.valueOf(likes));
        holder.binding.tvTitle.setText(author);
        holder.binding.tvContent.setText(content);
        holder.binding.date.setText(currentDate);


        //gets the post authors profile photo and displays it on the posts
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(author);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> userMap = documentSnapshot.getData();
                String profileImagePath = userMap.get("profileImagePath").toString();
                // Reference to an image file in Cloud Storage
                FirebaseStorage.getInstance().getReference().child("images/"+profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        postAuthorProfilePhoto = uri;
                        ImageView imageView = holder.binding.sivAvatar;
                        Glide.with(context)
                                .load(postAuthorProfilePhoto)
                                .into(imageView);
                    }
                });
            }
        });
        holder.binding.ivCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        holder.binding.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLike(postID,post,holder);
            }
        });
        holder.binding.ivCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRepost(postID,post,holder);
            }
        });

        holder.binding.ivReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleComment(postID,post,holder);
            }
        });


    }


    @Override
    public int getItemCount() {
        return postList.size();
    }


    public void handleRepost(String postID,Post post, PostViewHolder holder){

        DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(postID.toString());
        if(!post.getRepostedBy().contains(LoginPage.currUser.username)){
            reposts = post.getRepostCount()+1;
            post.setRepostCount(reposts);
            document.update("repostCount",reposts);

            holder.binding.tvCycle.setText(Integer.toString(reposts));
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
            holder.binding.tvCycle.setText(Integer.toString(reposts));
        }

    }



    public void handleComment(String postID, Post post, PostViewHolder holder){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Comment");
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Post Comment", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(postID.toString());
                comments = post.getCommentCount()+1;
                post.setCommentCount(comments);
                document.update("commentCount",comments);
                holder.binding.tvReview.setText(Integer.toString(comments));
                HashMap<String,Object> newComment = new HashMap<>();
                newComment.put("commentAuthor",LoginPage.currUser.username.toString());
                newComment.put("comment",input.getText().toString());
                newComment.put("commentTime", java.text.DateFormat.getDateTimeInstance().format(new Date()));
                post.addComment(newComment);
                document.update("comments",post.getComments());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();


    }
    
    
    
    //adds like to post in database and on the view
    public void handleLike(String postID,Post post, PostViewHolder holder){
        DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(postID.toString());
        //commented out for now but limits to one like per user
        if(!post.getLikedBy().contains(LoginPage.currUser.username)){
            likes = post.getLikeCount()+1;
            post.setLikeCount(likes);
            document.update("likeCount",likes);
            //Somewehere in here i want to use this animation when a post is liked.

            // Declaring the animation view
            LottieAnimationView animationView  = holder.binding.heart1;
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
            holder.binding.tvLike.setText(Integer.toString(likes));
            post.addToLikedBy(LoginPage.currUser.username);
            document.update("likedBy",post.getLikedBy());
        }
        //unlike
        else if(post.getLikedBy().contains(LoginPage.currUser.username)){
            likes = post.getLikeCount()-1;
            post.setLikeCount(likes);
            post.removeFromLikedBy(LoginPage.currUser.username);
            document.update("likedBy",post.getLikedBy());
            document.update("likeCount",likes);
            holder.binding.tvLike.setText(Integer.toString(likes));
        }
    }
    public void addData(Post newItem) {
        postList.add(0, newItem);
        notifyItemInserted(0);
        notifyItemInserted(postList.size());
        notifyItemChanged(postList.size());
    }


    class PostViewHolder extends RecyclerView.ViewHolder {
        ItemPostBinding binding;
        TextView postAuthor;
        TextView postContent;
        TextView postDate;
        TextView likeCount;
        ImageView postAuhtorProfileImage;



        public PostViewHolder(ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            postAuthor = this.binding.tvTitle;
            postContent = this.binding.tvContent;
            postDate = this.binding.date;
            likeCount = this.binding.tvLike;
            postAuhtorProfileImage = this.binding.sivAvatar;


        }
    }
}
