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
        FloatingActionButton myFab = binding.getRoot().findViewById(R.id.fab);
        myFab.show();
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
        //layout post adapter
        binding.rlPostBox.setAdapter(postAdapter);
        binding.rlPostBox.setLayoutManager(new LinearLayoutManager(this));
        binding.rlPostBox.setHasFixedSize(true);
        binding.rlPostBox.addItemDecoration(new LinearLayoutDivider(this, LinearLayoutManager.VERTICAL));


    }

    private void addTempData(String title, String content) {
        PostBean postBean = new PostBean();
        postBean.setTitle(title);
        postBean.setContent(content);
        postBean.setDescription(title + "re-post");
        postAdapter.addData(postBean);
    }


}