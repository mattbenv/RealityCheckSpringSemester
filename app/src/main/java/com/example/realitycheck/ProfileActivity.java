package com.example.realitycheck;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.bean.PostBean;
import com.example.realitycheck.databinding.ActivityProfileBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        MainActivity.toolbar.hide();
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
        binding.rlPostBox.setAdapter(postAdapter);
        binding.rlPostBox.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.rlPostBox.setHasFixedSize(true);
        binding.rlPostBox.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));
        //for posts is user post list generate each post

        ArrayList<String> userPosts = LoginPage.currUser.posts;
        Map<String, Object> postBeanValues =  new HashMap<String, Object>();
        for (String post : userPosts) {
            int[] numLikes = new int[1];
            Task<DocumentSnapshot> document = FirebaseFirestore.getInstance().collection("Posts").document(post).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            Map<String, Object> postMap = document.getData();
                            String likes = postMap.get("likeCount").toString();
                            String postAuthor = postMap.get("postAuthor").toString();
                            String postDate = postMap.get("postDate").toString();
                            String content = postMap.get("content").toString();

                            postBeanValues.put("title", postAuthor);
                            postBeanValues.put("content", content);
                            postBeanValues.put("postDate", postDate);
                            postBeanValues.put("likeCount", likes);


                        }
                    }
                }

            });
            PostBean postBean = new PostBean();
            postBean.setTitle(String.valueOf(postBeanValues.get("title")));
            postBean.setContent((String) postBeanValues.get("content"));
            postBean.setDescription(postBeanValues.get("title") + "re-post");
            postBean.setCurrentDate((String) postBeanValues.get("postDate"));
            postAdapter.addData(postBean);


        }

        }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
