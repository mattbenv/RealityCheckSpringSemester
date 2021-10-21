package com.example.realitycheck;

import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.bean.PostBean;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class TextPost extends Post{



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void createPost() {


      /*  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        TextView currDate = PostActivity.binding.getRoot().findViewById(R.id.date);
        currDate.setText("ASDFSD");

       */



    }
}
