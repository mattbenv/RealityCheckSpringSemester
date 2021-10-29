package com.example.realitycheck;

import com.example.realitycheck.bean.PostBean;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// [START post_class]
@IgnoreExtraProperties
public abstract class Post {
    private String postDate;
    private String postAuthor;
    private int likeCount;
    private UUID postId;

    public abstract void createPost();
    public FirebaseAuth mAuth;
    public DatabaseReference ref;
    public FirebaseFirestore fStorage;

    public Post(String postAuthor, String postDate) {
        //initialize post fields
        this.postAuthor = postAuthor;
        this.postDate = postDate;
        addPostToDatabase();
        createPostBean();


    }

    public void createPostBean(){
        PostBean postBean = new PostBean();
        postBean.setPostId(postId);
        //Login.currUser stores the current user logged in
        postBean.setTitle(LoginPage.currUser.username);
        postBean.setContent("This is the content of the post created by: " + LoginPage.currUser.username + " the bio of this user is " + LoginPage.currUser.bio);
        postBean.setDescription(LoginPage.currUser.username + "re-post");
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        postBean.setCurrentDate(currentDateTimeString);
        PostActivity.postAdapter.addData(postBean);
    }

    public void addPostToDatabase(){
        //create post
        postId = UUID.randomUUID();
        LoginPage.currUser.posts.add(postId.toString());
        DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(String.valueOf(postId));
        Map<String, Object> currPost = new HashMap<>();
        currPost.put("postAuthor", LoginPage.currUser.username);
        currPost.put("postDate", java.text.DateFormat.getDateTimeInstance().format(new Date()));
        currPost.put("likeCount", 0);
        document.set(currPost);
        //add post id to user posts feild
        LoginPage.currUser.posts.add(postId.toString());
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("posts", LoginPage.currUser.posts).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });

    }


    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate){
        this.postDate = postDate;
    }

    public String getPostAuthor() {
        return postAuthor;
    }

    public void setPostAuthor(String postAuthor) {
        this.postAuthor = postAuthor;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
   /* public String news_post;
    public Image image;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
    //TODO: WE need to make a method called createPost that whenever a post calls it, it knows where to be directed
    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }
    public Post(String news_post, Image image) {
        this.news_post = news_post;
        this.image = image;
    }
    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("news_post", news_post);
        result.put("image", image);
        result.put("starCount", starCount);
        result.put("stars", stars);
        return result;
    }
    // [END post_to_map]*/
}