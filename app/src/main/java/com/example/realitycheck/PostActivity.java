package com.example.realitycheck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.bean.PostBean;
import com.example.realitycheck.databinding.ActivityPostBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

public class PostActivity extends AppCompatActivity {

    public static ActivityPostBinding binding;
    public static PostAdapter postAdapter;

    public PostAdapter getPostAdapter(){
        return this.postAdapter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UltimateBarX.statusBar(this)
                .fitWindow(true)
                .light(false)
                .color(Color.BLACK)
                .apply();
        getSupportActionBar().hide();
        initData();
    }

    private void initData() {
        postAdapter = new PostAdapter(this);
        StringBuilder title = new StringBuilder();
        StringBuilder content = new StringBuilder();
        binding.getRoot().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post p = new TextPost();
                p.createPost();
            }
        });
        binding.rlPostBox.setAdapter(postAdapter);
        binding.rlPostBox.setLayoutManager(new LinearLayoutManager(this));
        binding.rlPostBox.setHasFixedSize(true);
        binding.rlPostBox.addItemDecoration(new LinearLayoutDivider(this, LinearLayoutManager.VERTICAL));
        // add test data

       // addTempData(title.toString(), content.toString());
       /* for (int i = 1; i < 11; i++) {
            StringBuilder title = new StringBuilder();
            StringBuilder content = new StringBuilder();
            for (int j = 1; j < 4; j++) {
                title.append(i);
            }
            for (int k = 1; k < 50; k++) {
                content.append(i);
            }
            addTempData(title.toString(), content.toString());
        }

        */


    }

    private void addTempData(String title, String content) {
        PostBean postBean = new PostBean();
        postBean.setTitle(title);
        postBean.setContent(content);
        postBean.setDescription(title + "re-post");
        postAdapter.addData(postBean);
    }


}