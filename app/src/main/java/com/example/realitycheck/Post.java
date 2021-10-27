package com.example.realitycheck;

import android.media.Image;
import android.util.Log;

import com.example.realitycheck.bean.PostBean;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public abstract class Post {
    private Date postDate;
    private User postAuthor;
    public abstract void createPost();
    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    public Post(){

        mAuth = FirebaseAuth.getInstance();



        PostBean postBean = new PostBean();
        //Using getEmail() because cant figure out how to access username
        postBean.setTitle(mAuth.getCurrentUser().getEmail());
        postBean.setContent("this is content of the post");
        postBean.setDescription(mAuth.getCurrentUser().getEmail()+ "re-post");


        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        postBean.setCurrentDate(currentDateTimeString);
        PostActivity.postAdapter.addData(postBean);
        //Have to update list of posts for current user as well

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