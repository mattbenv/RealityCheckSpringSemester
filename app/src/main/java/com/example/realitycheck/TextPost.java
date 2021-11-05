package com.example.realitycheck;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class TextPost extends Post{


    public TextPost(){

    }
    public TextPost(String postAuthor, String postDate) {
        super(postAuthor, postDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void createPost() {




    }
}
