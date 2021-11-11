package com.example.realitycheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.databinding.ActivityPostBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends Fragment {

    public static ActivityPostBinding binding;
    public static PostAdapter postAdapter;
    public String postId;

    public RecyclerView recyclerView;
    ActionBarDrawerToggle toggle;

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
        recyclerView = binding.rlPostBox;

        toggle = new ActionBarDrawerToggle(this.getActivity(),binding.drawerLayout,R.string.Open,R.string.Close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast.makeText(getContext(),item.toString(),Toast.LENGTH_SHORT).show();
                if(item.toString().equals("Profile")){
                    NavHostFragment.findNavController(PostActivity.this)
                    .navigate(R.id.action_PostActivity_to_ProfileActivity);
                }
                return true;

            }
        });

        list = new ArrayList<Post>();
        MainActivity.toolbar.hide();
        FloatingActionButton myFab = binding.getRoot().findViewById(R.id.fab);
        myFab.show();
        fStorage = FirebaseFirestore.getInstance();
        //initData();
        setPosts();
        bottomNavigate(binding);
        ImageView imageView = binding.getRoot().findViewById(R.id.shapeableImageView);
        Glide.with(this.getContext())
                .load(LoginPage.storageProfilePictureReference)
                .into(imageView);

        binding.getRoot().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                //Post p = new TextPost(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),currentDateTimeString);
                NavHostFragment.findNavController(PostActivity.this).navigate(R.id.action_PostActivity_to_CreatePostActivity);
            }
        });
        binding.getRoot().findViewById(R.id.shapeableImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(PostActivity.this)
                        .navigate(R.id.action_PostActivity_to_ProfileActivity);

            }
        });
        return binding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       if(toggle.onOptionsItemSelected(item)){
           return true;
       }
        return super.onOptionsItemSelected(item);
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


    public void setPosts(){
        //setup new postadapter on profile page

        list = new ArrayList<Post>();
        PostAdapter postAdapter = new PostAdapter(this.getContext(),list);

        for(int i= 0;i<LoginPage.currUser.posts.size();i++){
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Posts").document(LoginPage.currUser.posts.get(i));
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Post post = new TextPost();
                    post.setPostAuthor(documentSnapshot.get("postAuthor").toString());
                    post.setPostDate(documentSnapshot.get("postDate").toString());
                    post.setContent(documentSnapshot.get("content").toString());
                    post.setLikeCount(Integer.parseInt(documentSnapshot.get("likeCount").toString()));
                    post.setPostId(documentSnapshot.get("postId").toString());
                    post.setRepostCount(Integer.parseInt(documentSnapshot.get("repostCount").toString()));
                    post.setRepostedBy((ArrayList<String>) documentSnapshot.get("repostedBy"));
                    post.setLikedBy((ArrayList<String>) documentSnapshot.get("likedBy"));
                   // post.setComments((ArrayList<HashMap<String,Object>>) documentSnapshot.get("comments"));
                    //post.setCommentCount(Integer.parseInt(documentSnapshot.get("commentCount").toString()));
                    list.add(0,post);
                    postAdapter.notifyDataSetChanged();

                }

            });

        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(postAdapter);



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


    /*

     public void setPosts(){

        ArrayList<String> postIDFeed = new ArrayList<>();
        for(int i=0;i<LoginPage.currUser.following.size();i++){


            //get each user document for who your following
            DocumentReference documentReferenceUser = FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.following.get(i));
            documentReferenceUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    //get list of posts for each user
                    ArrayList<String> userPosts = (ArrayList<String>) documentSnapshot.get("posts");
                    //add posts from lists to master list for post feed
                    postIDFeed.addAll(userPosts);
                }
            });
        }
        //now postIDFeed contains all the postnames we need to generate on our post activity
        for(String eachPost:postIDFeed){
            DocumentReference documentReferencePost = FirebaseFirestore.getInstance().collection("Posts").document(eachPost);
            documentReferencePost.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Post post = new TextPost();
                    post.setPostAuthor(documentSnapshot.get("postAuthor").toString());
                    post.setPostDate(documentSnapshot.get("postDate").toString());
                    post.setContent(documentSnapshot.get("content").toString());
                    post.setLikeCount(Integer.parseInt(documentSnapshot.get("likeCount").toString()));
                    post.setPostId(documentSnapshot.get("postId").toString());
                    //need to fix convert to
                    post.setLikedBy((ArrayList<String>) documentSnapshot.get("likedBy"));
                    list.add(0,post);
                    postAdapter.notifyDataSetChanged();
                }
            });
        }
        binding.rlPostBox.setAdapter(postAdapter);
        binding.rlPostBox.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.rlPostBox.setHasFixedSize(true);
        binding.rlPostBox.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));


    }
     */
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

        ArrayList<String> likeList = new ArrayList<>();
        post.setLikedBy(likeList);
        ArrayList<String> repostList = new ArrayList<>();
        post.setRepostedBy(repostList);
        PostActivity.postAdapter.addData(post);

        //create post
        DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(String.valueOf(postId));
        Map<String, Object> currPost = new HashMap<>();
        currPost.put("postAuthor", LoginPage.currUser.username);
        currPost.put("postId", postId);
        currPost.put("postDate", java.text.DateFormat.getDateTimeInstance().format(new Date()));
        currPost.put("likeCount", 0);
        currPost.put("likedBy", post.getLikedBy());
        currPost.put("repostCount", 0);
        currPost.put("repostedBy",post.getRepostedBy());
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