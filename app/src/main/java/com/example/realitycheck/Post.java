package com.example.realitycheck;

import android.media.Image;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public abstract class Post {
    public abstract void createPost();
    public Post(){

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
