package com.example.realitycheck;

import android.provider.ContactsContract;

public class ImagePost extends Post{
    public String picture;
    public ImagePost(String postAuthor, String postDate,String picture) {

        super(postAuthor, postDate);
        this.picture = picture;
    }

    @Override
    public void createPost() {


    }

}
