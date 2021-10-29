package com.example.realitycheck;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.databinding.ActivityProfileBinding;
import com.example.realitycheck.util.LinearLayoutDivider;

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
        binding.username.setText(LoginPage.currUser.username);
        binding.UserBio.setText(LoginPage.currUser.bio);
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileActivity.this)
                        .navigate(R.id.action_ProfileActivity_to_PostActivity);
            }

            
        });

        //sets profile picture
        /*
        ImageView imageView = this.getView().findViewById(R.id.profilePic);

        Glide.with(this.getContext())
                .load(LoginPage.storageProfilePictureReference)
                .into(imageView);

         */

        binding.following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("List of people you follow")
                        .setMessage(LoginPage.currUser.following.toString())
                        .setCancelable(true)
                        .setPositiveButton("close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
        binding.followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("List of your followers")
                        .setMessage(LoginPage.currUser.followers.toString())
                        .setCancelable(true)
                        .setPositiveButton("close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });




    }

    public void setPosts(){
        postAdapter = new PostAdapter(this.getContext());
        binding.rlPostBox.setAdapter(PostActivity.postAdapter);
        binding.rlPostBox.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.rlPostBox.setHasFixedSize(true);
        binding.rlPostBox.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));
        //for posts is user post list generate each post


        /*
        for(int i = 0;i<5;i++){
            PostBean postBean = new PostBean();
            postBean.setTitle("post " + i);
            postBean.setContent("content of post number "+ i) ;
            postBean.setDescription("post " + i +" re-post");
            postAdapter.addData(postBean);

        }

         */

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
