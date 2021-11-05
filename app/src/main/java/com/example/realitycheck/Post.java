package com.example.realitycheck;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

// [START post_class]
@IgnoreExtraProperties
public abstract class Post {
    private String postDate;
    private String postAuthor;
    private int likeCount;



    public ArrayList<String> getLikedBy() {
        return this.likedBy;
    }

    public void addToLikedBy(String username){
        this.likedBy.add(username);
    }
    public void setLikedBy(ArrayList<String> likedBY) {
        this.likedBy = likedBY;
    }

    private ArrayList<String> likedBy;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    private String postId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;
    public abstract void createPost();
    public FirebaseAuth mAuth;
    public DatabaseReference ref;
    public FirebaseFirestore fStorage;

    public Post(){
        this.likedBy = new ArrayList<String>();

    }

    public Post(String postAuthor, String postDate) {
        //initialize post fields
        this.postAuthor = postAuthor;
        this.postDate = postDate;
        this.likedBy = new ArrayList<String>();

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