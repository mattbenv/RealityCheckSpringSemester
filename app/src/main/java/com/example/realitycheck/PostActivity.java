package com.example.realitycheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.databinding.ActivityPostBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends Fragment {

    public static ActivityPostBinding binding;
    public static PostAdapter postAdapter;
    public String postId;

    public ArrayList<Post> list;
    public FirebaseFirestore fStorage;
    public PostAdapter getPostAdapter(){
        return this.postAdapter;
    }



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  ActivityPostBinding.inflate(inflater, container, false);


        list = new ArrayList<Post>();
        MainActivity.toolbar.hide();
        FloatingActionButton myFab = binding.getRoot().findViewById(R.id.fab);
        myFab.show();
        fStorage = FirebaseFirestore.getInstance();
        initData();
        bottomNavigate(binding);
        ImageView imageView = binding.getRoot().findViewById(R.id.shapeableImageView);
        Glide.with(this.getContext())
                .load(LoginPage.storageProfilePictureReference)
                .into(imageView);

        binding.getRoot().findViewById(R.id.shapeableImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(PostActivity.this)
                        .navigate(R.id.action_PostActivity_to_ProfileActivity);
            }
        });
        return binding.getRoot();
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Operator operator = UltimateBarX.statusBar(this);
        // operator.fitWindow(true);
        // operator.light(false);
        //operator.color(Color.BLACK);
        //operator.apply();



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }

    private void initData() {
        list = new ArrayList<Post>();
        postAdapter = new PostAdapter(this.getContext(),list);
        binding.getRoot().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                //Post p = new TextPost(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),currentDateTimeString);
                createPost();
                binding.rlPostBox.getLayoutManager().scrollToPosition(0);

            }
        });
        //layout post adapter
        binding.rlPostBox.setAdapter(postAdapter);
        binding.rlPostBox.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.rlPostBox.setHasFixedSize(true);
        binding.rlPostBox.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));


    }

    public void createPost(){
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        Post post = new TextPost(LoginPage.currUser.username,currentDateTimeString);
        int numposts = LoginPage.currUser.posts.size();
        postId = LoginPage.currUser.username + "_Post_" + numposts;
        post.setPostId(postId);
        //Login.currUser stores the current user logged in
        post.setPostAuthor(LoginPage.currUser.username);
        post.setContent("This is the content of the post created by: " + LoginPage.currUser.username + " the bio of this user is " + LoginPage.currUser.bio);

        post.setPostDate(currentDateTimeString);

        //Start with 1 in liked by in order to return array of strings instead of string
        ArrayList<String> likeList = new ArrayList<>();
        post.setLikedBy(likeList);
        PostActivity.postAdapter.addData(post);

        //create post
        DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(String.valueOf(postId));
        Map<String, Object> currPost = new HashMap<>();
        currPost.put("postAuthor", LoginPage.currUser.username);
        currPost.put("postId", postId);
        currPost.put("postDate", java.text.DateFormat.getDateTimeInstance().format(new Date()));
        currPost.put("likeCount", 0);
        currPost.put("likedBy", post.getLikedBy());
        currPost.put("content", post.getContent());
        document.set(currPost);

        //add post id to user posts feild
        LoginPage.currUser.posts.add(postId);
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).update("posts", LoginPage.currUser.posts).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    public void bottomNavigate(ActivityPostBinding binding){
        BottomNavigationView bottomNavigationView = binding.getRoot().findViewById(R.id.bnav_post_bottom);
        //need to fix to recognize what screen
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.post_search:
                        NavHostFragment.findNavController(PostActivity.this)
                                .navigate(R.id.action_PostActivity_to_SearchPage);
                        break;
                    case R.id.post_home:
                        //
                        break;
                    case R.id.post_notification:
                        //
                        break;
                    case R.id.post_message:
                        //
                        break;
                }
                return true;
            }
        });
    }



    private void addTempData(String title, String content) {
        Post post = new TextPost(title,java.text.DateFormat.getDateTimeInstance().format(new Date()));
        post.setPostAuthor(title);
        post.setContent(content);
        postAdapter.addData(post);
    }


}