
package com.example.realitycheck;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

// [START post_class]
@IgnoreExtraProperties
public abstract class Post {
    private String postDate;
    private String postAuthor;
    private int likeCount;
    private ArrayList<String> likedBy;
    private String postId;
    private String content;
    private String photo;


    private ArrayList<Comment> comments;



    private int commentCount;

    private int repostCount;
    private ArrayList<HashMap<String,String>> repostedBy;
    public FirebaseAuth mAuth;
    public DatabaseReference ref;
    public FirebaseFirestore fStorage;



    public abstract void createPost();


    public Post(){
        this.likedBy = new ArrayList<String>();

    }

    public Post(String postAuthor, String postDate) {
        //initialize post fields
        this.postAuthor = postAuthor;
        this.postDate = postDate;

    }


    public int getRepostCount() {
        return repostCount;
    }

    public void setRepostCount(int repostCount) {
        this.repostCount = repostCount;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    public ArrayList<String> getLikedBy() {
        return this.likedBy;
    }

    public void addToLikedBy(String username){
        this.likedBy.add(username);
    }

    public void removeFromLikedBy(String username){
        this.likedBy.remove(username);
    }
    public void setLikedBy(ArrayList<String> likedBY) {
        this.likedBy = likedBY;
    }




    public void addToRepostedBy(HashMap<String,String> repost){
        this.repostedBy.add(repost);
    }

    public void removeFromRepostedBy(String name) {
        for(HashMap a: this.repostedBy){
            if(a.get("username")==name){
                this.repostedBy.remove(a);
            }
        }
    }

    public ArrayList<HashMap<String,String>> getRepostedBy() {
        return repostedBy;
    }

    public void setRepostedBy(ArrayList<HashMap<String,String>> repostedBy) {
        this.repostedBy = repostedBy;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }


    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
    }
    public void removeComment(Comment comment){
        this.comments.remove(comment);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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



