package com.example.realitycheck.adapter;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.realitycheck.Activitys.LoginPage;
import com.example.realitycheck.Post;
import com.example.realitycheck.Activitys.PostActivity;
import com.example.realitycheck.R;
import com.example.realitycheck.User;
import com.example.realitycheck.Activitys.ViewGroupActivity;
import com.example.realitycheck.Activitys.ViewPostActivity;
import com.example.realitycheck.Activitys.otherUserProfileActivity;
import com.example.realitycheck.databinding.ItemPostBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;

    public Uri postAuthorProfilePhoto;
    public Uri postPhoto;

    private ArrayList<Post> postList;
    int[] likeCount;
    public int likes;
    public int reposts;
    public static Post post;
    public FirebaseFirestore fStore;
    public static String postPage;
    public static User userToNavTo;
    public PostAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        postList = posts;
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
                DateFormat format = DateFormat.getDateTimeInstance();
                try {
                    Date a = format.parse(post.getPostDate());
                    Date b = format.parse(t1.getPostDate());
                    return(a.compareTo(b));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return(post.getPostDate().compareTo(t1.getPostDate()));
            }
        });
        //sets most recent posts to start of list
        Collections.reverse(postList);

        //initialize database
        fStore = FirebaseFirestore.getInstance();

        //gets current post from the recycler view list based its position
        post = postList.get(position);
        //gets post values
        String content = post.getContent();
        String postID = post.getPostId();
        String currentDate = post.getPostDate();
        int commentCount = post.getCommentCount();
        String author = post.getPostAuthor();
        String photo = post.getPhoto();
        //loads photos and gifs on corresponding post

        //gets the post authors profile photo and displays it on the posts
        DocumentReference docRef = fStore.collection("Users").document(author);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> userMap = documentSnapshot.getData();
                String profileImagePath = userMap.get("profileImagePath").toString();
                String realName = userMap.get("name").toString();
                holder.binding.tvDescription.setText(realName);
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

        if(post.getLikedBy().contains(LoginPage.currUser.username)){
            // Declaring the animation view

            holder.binding.heart1.setVisibility(View.VISIBLE);
            holder.binding.heart1
                    .addAnimatorUpdateListener(
                            (animation) -> {
                                // Do something.
                            });
            holder.binding.heart1
                    .playAnimation();


        }
        else if (!post.getLikedBy().contains(LoginPage.currUser.username)){
            // Declaring the animation view
            holder.binding.heart1.setVisibility(View.GONE);
        }


        //loads the pictures/gifs onto posts
        FirebaseStorage.getInstance().getReference().child("images/" + post.getPhoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                postPhoto = uri;
                ImageView iV = holder.binding.imageView;
                iV.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(postPhoto)
                        .into(iV);
            }
        });

        if(postList.get(position).getPhoto()==null){
            holder.binding.imageView.setVisibility(View.GONE);
        }




        //sets up reposts on the users profile page
        if(postPage=="profile") {
            if (!post.getPostAuthor().contains(LoginPage.currUser.username)) {
                holder.binding.repost.setVisibility(View.VISIBLE);
                holder.binding.repostimage.setVisibility(View.VISIBLE);
                holder.binding.repost.setText(LoginPage.currUser.username + " reposted");
            } else if (post.getPostAuthor().contains(LoginPage.currUser.username)) {
                holder.binding.repost.setVisibility(View.GONE);
                holder.binding.repostimage.setVisibility(View.GONE);
            }
        }
        //sets up reposts on other users profile page
        if(postPage=="otherProfile"){
            if (!post.getPostAuthor().contains(otherUserProfileActivity.thisUser.username)) {
                holder.binding.repost.setVisibility(View.VISIBLE);
                holder.binding.repostimage.setVisibility(View.VISIBLE);
                holder.binding.repost.setText(otherUserProfileActivity.thisUser.username+ " reposted");
            } else if (post.getPostAuthor().contains(LoginPage.currUser.username)) {
                holder.binding.repost.setVisibility(View.GONE);
                holder.binding.repostimage.setVisibility(View.GONE);
            }

        }
        if(postPage == "postActivity"){
            // TODO: 11/30/2021 need to implement reposts in postactivity
        }

        if(postPage == "groupView"){

        }
        if(postPage == "taggedIn"){
            holder.binding.repost.setVisibility(View.GONE);
            holder.binding.repostimage.setVisibility(View.GONE);
        }





        //click the users username on a post to navigate to their profile page
        holder.binding.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post = postList.get(position);
                navigateToUserProfile(holder);
            }
        });

        //click the users profile photo on a post to navigate to their profile page
        holder.binding.sivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post = postList.get(position);
                navigateToUserProfile(holder);
            }
        });

        //button to open dialog to delete post
        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post = postList.get(position);
                deletePost(holder,post.getPostId());

            }
        });

        //on clicking the posts it moves to a more in depth view of just that post
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

        //like button clicked on post
        holder.binding.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post = postList.get(position);
                handleLike(postID,post,holder);
            }
        });
        //repost button clicked on post
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


    //removes post from recyclerview, from the current user's list of posts, and from the database
    //removes post from reposts for all users who reposted the post to be deleted
    //removes post image from storage
    public void deletePost(PostViewHolder holder, String postID){
        if(post.getPostAuthor().contains(LoginPage.currUser.username)){
            new AlertDialog.Builder(context)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this post?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(postPage == "groupView"){
                                DocumentReference dRef = fStore.collection("Groups").document(ViewGroupActivity.group.groupName);
                                ViewGroupActivity.group.posts.remove(post.getPostId());
                                dRef.update("posts",ViewGroupActivity.group.posts);
                                DocumentReference docRef = fStore.collection("Posts").document(post.getPostId());
                                Query queryRepost = FirebaseFirestore.getInstance().collection("Users").whereArrayContains("reposted",postID);
                                queryRepost.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot documentSnapshots) {
                                        ArrayList<String> users = new ArrayList<>();
                                        for (DocumentSnapshot d : documentSnapshots) {
                                            users.add((String) d.get("username"));
                                        }
                                        if (users != null) {
                                            for (String s : users) {
                                                DocumentReference doc = fStore.collection("Users").document(s);
                                                doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        ArrayList<String> reposted = (ArrayList<String>) documentSnapshot.get("reposted");
                                                        reposted.remove(postID);
                                                        doc.update("reposted", reposted);

                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                                Query query = FirebaseFirestore.getInstance().collection("Users").whereArrayContains("taggedIn",postID);
                                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot documentSnapshots) {
                                        ArrayList<String> users = new ArrayList<>();
                                        for (DocumentSnapshot d : documentSnapshots) {
                                            users.add((String) d.get("username"));
                                        }
                                        System.out.println(users);
                                        if (users != null) {
                                            for (String s : users) {
                                                DocumentReference doc = fStore.collection("Users").document(s);
                                                doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        ArrayList<String> taggedIn = (ArrayList<String>) documentSnapshot.get("taggedIn");
                                                        taggedIn.remove(postID);
                                                        doc.update("taggedIn", taggedIn);

                                                    }
                                                });

                                            }

                                        }
                                    }
                                });
                                docRef.delete();

                                removeData(post);

                                FirebaseStorage.getInstance().getReference().child("images").child(postID+"_image").delete();
                            }
                            DocumentReference documentReference = fStore.collection("Users").document(post.getPostAuthor());
                            LoginPage.currUser.posts.remove(post.getPostId());
                            documentReference.update("posts", LoginPage.currUser.posts);
                            DocumentReference docRef = fStore.collection("Posts").document(post.getPostId());
                            Query queryRepost = FirebaseFirestore.getInstance().collection("Users").whereArrayContains("reposted",postID);
                            queryRepost.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot documentSnapshots) {
                                    ArrayList<String> users = new ArrayList<>();
                                    for (DocumentSnapshot d : documentSnapshots) {
                                        users.add((String) d.get("username"));
                                    }
                                    if (users != null) {
                                        for (String s : users) {
                                            DocumentReference doc = fStore.collection("Users").document(s);
                                            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    ArrayList<String> reposted = (ArrayList<String>) documentSnapshot.get("reposted");
                                                    reposted.remove(postID);
                                                    doc.update("reposted", reposted);

                                                }
                                            });
                                        }
                                    }
                                }
                            });
                            Query query = FirebaseFirestore.getInstance().collection("Users").whereArrayContains("taggedIn",postID);
                            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot documentSnapshots) {
                                    ArrayList<String> users = new ArrayList<>();
                                    for (DocumentSnapshot d : documentSnapshots) {
                                        users.add((String) d.get("username"));
                                    }
                                    System.out.println(users);
                                    if (users != null) {
                                        for (String s : users) {
                                            DocumentReference doc = fStore.collection("Users").document(s);
                                            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    ArrayList<String> taggedIn = (ArrayList<String>) documentSnapshot.get("taggedIn");
                                                    taggedIn.remove(postID);
                                                    doc.update("taggedIn", taggedIn);

                                                }
                                            });

                                        }

                                    }
                                }
                            });
                            docRef.delete();

                            removeData(post);

                            FirebaseStorage.getInstance().getReference().child("images").child(postID+"_image").delete();
                        }
                    }).setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();



        }
    }

    //get's the users information and navigates to their profile page
    public void navigateToUserProfile(PostViewHolder holder){
        fStore.collection("Users").document(post.getPostAuthor()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                Boolean privateMode = (Boolean) userMap.get("private");
                Boolean notificationsEnabled = (Boolean) userMap.get("notificationsEnabled");
                ArrayList<String> taggedIn = (ArrayList<String>) userMap.get("taggedIn");
                ArrayList<String> reposted = (ArrayList<String>) userMap.get("reposted");
                userToNavTo = new User(uid, email, username, name, bio, birthday, profileImagePath, posts, followers, following, friends,privateMode,notificationsEnabled,taggedIn,reposted);
                otherUserProfileActivity.previousActivty = "post";
                Navigation.createNavigateOnClickListener(R.id.to_OtherUserProfileActivity).onClick(holder.binding.sivAvatar);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    //updates counts and list of reposts and reposted by
    // TODO: include reposts on user's list of posts as a repost
    public void handleRepost(String postID,Post post, PostViewHolder holder){

        DocumentReference document = fStore.collection("Posts").document(postID.toString());

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
            fStore.collection("Users").document(LoginPage.currUser.username).update("reposted",LoginPage.currUser.reposted);

            holder.binding.tvCycle.setText(Integer.toString(reposts));
            HashMap<String,String> repost = new HashMap();
            repost.put("username",LoginPage.currUser.username);
            repost.put("time",java.text.DateFormat.getDateTimeInstance().format(new Date()));
            post.addToRepostedBy(repost);
            document.update("repostedBy",post.getRepostedBy());
            repostNotifications(postID,holder);


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
            fStore.collection("Users").document(LoginPage.currUser.username).update("reposted",LoginPage.currUser.reposted);
            document.update("repostedBy",post.getRepostedBy());
            document.update("repostCount",reposts);
            holder.binding.tvCycle.setText(Integer.toString(reposts));


        }


    }



    //adds like to post in database and on the view
    public void handleLike(String postID,Post post, PostViewHolder holder){
        LottieAnimationView animationView  = holder.binding.heart1;
        animationView.setVisibility(View.VISIBLE);
        DocumentReference document = fStore.collection("Posts").document(postID.toString());
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
            holder.binding.heart1.setVisibility(View.GONE);
            likes = post.getLikeCount()-1;
            post.setLikeCount(likes);
            post.removeFromLikedBy(LoginPage.currUser.username);
            document.update("likedBy",post.getLikedBy());
            document.update("likeCount",likes);
            holder.binding.tvLike.setText(Integer.toString(likes));
        }
    }

    // TODO: 11/30/2021 notifications are only working on each specific device need to research Firebase Cloud Messaging for notifications across devices
    //
    public void likeNotifications(String postID, PostViewHolder holder){
            if(LoginPage.currUser.posts.contains(postID)){
            DocumentReference document = fStore.collection("Posts").document(postID.toString());
            document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ArrayList<String> likedBy = (ArrayList<String>) documentSnapshot.get("likedBy");
                    String likee = likedBy.get(likedBy.size()-1);
                    Notification likeNotification = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalTime now = LocalTime.now();
                        DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("hh:mm:ss a");
                        likeNotification = new Notification.Builder(holder.itemView.getContext(),"my_channel_01")
                                .setContentTitle(likee+ " liked your post at "+ now.format(dtf3))
                                .setContentText("Like Received")
                                .setSmallIcon(R.drawable.ic_cycle).build();
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
            DocumentReference document = fStore.collection("Posts").document(postID.toString());
            document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ArrayList<HashMap<String,String>> repostedBy = (ArrayList<HashMap<String,String>>) documentSnapshot.get("repostedBy");
                    HashMap mostRecent = repostedBy.get(repostedBy.size()-1);
                    String repostee = mostRecent.toString();
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
    //adds post to the recyclerview
    public void addData(Post newItem) {
        postList.add(0, newItem);
        notifyItemInserted(0);
        notifyItemInserted(postList.size());
        notifyItemChanged(postList.size());
    }

    //removes post from the recyclerview
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
        ImageView postPhoto;




        public PostViewHolder(ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            postAuthor = this.binding.tvTitle;
            postContent = this.binding.tvContent;
            postDate = this.binding.date;
            likeCount = this.binding.tvLike;
            postAuhtorProfileImage = this.binding.sivAvatar;
            postPhoto = this.binding.imageView;


        }
    }
}
