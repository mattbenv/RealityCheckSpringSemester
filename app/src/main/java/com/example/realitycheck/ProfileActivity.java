package com.example.realitycheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.bean.PostBean;
import com.example.realitycheck.databinding.ActivityProfileBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends Fragment {
    private ActivityProfileBinding binding;
    public static PostAdapter postAdapter;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding  =  ActivityProfileBinding.inflate(inflater, container, false);
        setPosts();
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public void setPosts(){
        postAdapter = new PostAdapter(this.getContext());
        binding.rlPostBox.setAdapter(postAdapter);
        binding.rlPostBox.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.rlPostBox.setHasFixedSize(true);
        binding.rlPostBox.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));
        //for posts is user post list generate each post
        for(int i = 0;i<5;i++){
            PostBean postBean = new PostBean();
            postBean.setTitle("post " + i);
            postBean.setContent("content of post number "+ i) ;
            postBean.setDescription("post " + i +" re-post");
            postAdapter.addData(postBean);

        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
