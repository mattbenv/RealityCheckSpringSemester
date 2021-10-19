package com.example.realitycheck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Color;
import android.os.Bundle;

import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.bean.PostBean;
import com.example.realitycheck.databinding.ActivityPostBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

public class PostActivity extends AppCompatActivity {

    private ActivityPostBinding binding;
    private PostAdapter postAdapter;

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
        binding.rlPostBox.setAdapter(postAdapter);
        binding.rlPostBox.setLayoutManager(new LinearLayoutManager(this));
        binding.rlPostBox.setHasFixedSize(true);
        binding.rlPostBox.addItemDecoration(new LinearLayoutDivider(this, LinearLayoutManager.VERTICAL));
        // add test data
        for (int i = 1; i < 11; i++) {
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
    }

    private void addTempData(String title, String content) {
        PostBean postBean = new PostBean();
        postBean.setTitle(title);
        postBean.setContent(content);
        postBean.setDescription(title + "转推了");
        postAdapter.addData(postBean);
    }


}