package com.example.realitycheck.adapter;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.realitycheck.LoginPage;
import com.example.realitycheck.Post;
import com.example.realitycheck.PostActivity;
import com.example.realitycheck.R;
import com.example.realitycheck.User;
import com.example.realitycheck.ViewPostActivity;
import com.example.realitycheck.databinding.ItemPostBinding;
import com.example.realitycheck.otherUserProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Array;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

//todo: understand how homepage works which is related to post adapter/viewholder/activity. if we can, design new one
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;

    public Uri postAuthorProfilePhoto;

    private ArrayList<Post> postList;
    int[] likeCount;
    public int likes;
    public int reposts;
    public int comments;
    public static Post post;
    public static User userToNavTo;
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

        //sorts list of post based on post date
        //need to convert String postDate to dateTime in order to compare accurately
        Collections.sort(postList, new Comparator<Post>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public int compare(Post post, Post t1) {
                return(post.getPostDate().compareTo(t1.getPostDate()));

            }
        });
        Collections.reverse(postList);


        //gets current post from the recycler view list based its position
        post = postList.get(position);

        //gets post values
        String content = post.getContent();
        String postID = post.getPostId();
        String currentDate = post.getPostDate();
        int commentCount = post.getCommentCount();
        String author = post.getPostAuthor();

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
        likes = post.getLikeCount();
        ArrayList<String> likedBy = post.getLikedBy();
        //initialize
        likeCount = new int[1];
        likeCount[0] = likes;

        //displays values using item_post layout
        holder.binding.tvLike.setText(String.valueOf(likes));
        holder.binding.tvCycle.setText(String.valueOf(post.getRepostCount()));
        holder.binding.tvTitle.setText(author);
        holder.binding.tvContent.setText(content);
        holder.binding.date.setText(currentDate);
        holder.binding.tvReview.setText(String.valueOf(commentCount));

        ViewPostActivity.savedPosition = 0;

        holder.binding.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post = postList.get(position);
                navigateToUserProfile(holder);
            }
        });

        holder.binding.sivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post = postList.get(position);
                navigateToUserProfile(holder);
            }
        });

        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post = postList.get(position);
                if(post.getPostAuthor().contains(LoginPage.currUser.username)){
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Post")
                            .setMessage("Are you sure you want to delete this post?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(post.getPostAuthor());
                                    LoginPage.currUser.posts.remove(post.getPostId());
                                    documentReference.update("posts",LoginPage.currUser.posts);
                                    DocumentReference docRef = FirebaseFirestore.getInstance().collection("Posts").document(post.getPostId());
                                    docRef.delete();
                                    removeData(post);
                                }
                            }).setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }


            }
        });

        holder.binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post = postList.get(position);
                ViewPostActivity.savedPosition = position;
                post.setCommentCount(Integer.parseInt(holder.binding.tvReview.getText().toString()));
                post.setLikeCount(Integer.parseInt(holder.binding.tvLike.getText().toString()));
                post.setRepostCount(Integer.parseInt(holder.binding.tvCycle.getText().toString()));
                Navigation.createNavigateOnClickListener(R.id.to_ViewPostActivity).onClick(holder.binding.post);
            }
        });



        holder.binding.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post = postList.get(position);
                handleLike(postID,post,holder);
            }
        });
        holder.binding.ivCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post = postList.get(position);
                handleRepost(postID,post,holder);
            }
        });

        holder.binding.ivReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post = postList.get(position);
                Navigation.createNavigateOnClickListener(R.id.to_ViewPostActivity).onClick(holder.binding.post);
            }
        });


    }


    public void navigateToUserProfile(PostViewHolder holder){
        FirebaseFirestore.getInstance().collection("Users").document(post.getPostAuthor()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot value) {
                Map<String, Object> userMap = value.getData();
                String uid = userMap.get("uid").toString();
                String email = userMap.get("email").toString();
                String username = userMap.get("username").toString();
                String name = userMap.get("name").toString();
                String bio = userMap.get("bio").toString();
                String birthday = userMap.get("birthday").toString();
                String profileImagePath = userMap.get("profileImagePath").toString();
                ArrayList<String> posts = (ArrayList<String>) userMap.get("posts");
                ArrayList<String> followers = (ArrayList<String>) userMap.get("followers");
                ArrayList<String> following = (ArrayList<String>) userMap.get("following");
                ArrayList<String> friends = (ArrayList<String>) userMap.get("friends");
                userToNavTo = new User(uid, email, username, name, bio, birthday, profileImagePath, posts, followers, following, friends);
                otherUserProfileActivity.previousActivty = "post";
                Navigation.createNavigateOnClickListener(R.id.to_OtherUserProfileActivity).onClick(holder.binding.sivAvatar);
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
            repostNotifications(postID,holder);
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





    //adds like to post in database and on the view
    public void handleLike(String postID,Post post, PostViewHolder holder){
        LottieAnimationView animationView  = holder.binding.heart1;
        DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(postID.toString());
        //commented out for now but limits to one like per user
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
            holder.binding.tvLike.setText(Integer.toString(likes));
            post.addToLikedBy(LoginPage.currUser.username);
            document.update("likedBy",post.getLikedBy());

            likeNotifications(postID,holder);
        }
        //unlike
        else if(post.getLikedBy().contains(LoginPage.currUser.username)){
            //need to remove like animation in here
            likes = post.getLikeCount()-1;
            post.setLikeCount(likes);
            post.removeFromLikedBy(LoginPage.currUser.username);
            document.update("likedBy",post.getLikedBy());
            document.update("likeCount",likes);
            holder.binding.tvLike.setText(Integer.toString(likes));
        }
    }

    public void likeNotifications(String postID, PostViewHolder holder){
        Intent intent = new Intent(context,PostAdapter.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
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
                        likeNotification = new Notification.Builder(holder.itemView.getContext(),"my_channel_01")
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


    public void repostNotifications(String postID, PostViewHolder holder){
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
                        repostNotification = new Notification.Builder(holder.itemView.getContext(),"my_channel_01")
                                .setContentTitle(repostee+ " reposted your post at "+ now.format(dtf3))
                                .setContentText("Repost Received")
                                .setSmallIcon(R.drawable.ic_cycle).build();
                    }
                    PostActivity.mNotificationManager.notify(getNotificationID(),repostNotification);
                }
            });

        }
    }
    public void addData(Post newItem) {
        postList.add(0, newItem);
        notifyItemInserted(0);
        notifyItemInserted(postList.size());
        notifyItemChanged(postList.size());
    }
    public void removeData(Post item){
        int position = postList.indexOf(item);
        postList.remove(item);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());

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
