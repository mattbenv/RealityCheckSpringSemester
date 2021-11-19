package com.example.realitycheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PostActivity extends Fragment {

    public static ActivityPostBinding binding;
    public static PostAdapter postAdapter;
    public String postId;
    public static ArrayList<String> postIDFeed;

    public RecyclerView recyclerView;
    ActionBarDrawerToggle toggle;

    public ArrayList<Post> unsortedList;
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

        postIDFeed = new ArrayList<>();
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


        ImageView imageView = this.getView().findViewById(R.id.shapeableImageView);
        Glide.with(this.getContext())
                .load(LoginPage.storageProfilePictureReference)
                .into(imageView);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }


    public void setPosts(){

        ArrayList<String> postTrack = new ArrayList<>();
        list = new ArrayList<Post>();
        PostAdapter postAdapter = new PostAdapter(this.getContext(),list);
        ArrayList<String> followersAndCurrent = new ArrayList<>();
        followersAndCurrent.addAll(LoginPage.currUser.following);
        followersAndCurrent.add(LoginPage.currUser.username);

        if(LoginPage.currUser.following.size()>0) {
            for (int i = 0;i< followersAndCurrent.size();i++) {
                Query nameQuery = FirebaseFirestore.getInstance().collection("Posts").whereEqualTo("postAuthor", followersAndCurrent.get(i));
                nameQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot) {
                            Post post = new TextPost();
                            post.setPostAuthor(documentSnapshot.get("postAuthor").toString());
                            String date = documentSnapshot.get("postDate").toString();
                            post.setPostDate(documentSnapshot.get("postDate").toString());
                            post.setContent(documentSnapshot.get("content").toString());
                            post.setLikeCount(Integer.parseInt(documentSnapshot.get("likeCount").toString()));
                            post.setPostId(documentSnapshot.get("postId").toString());
                            post.setRepostCount(Integer.parseInt(documentSnapshot.get("repostCount").toString()));
                            post.setRepostedBy((ArrayList<String>) documentSnapshot.get("repostedBy"));
                            post.setLikedBy((ArrayList<String>) documentSnapshot.get("likedBy"));
                            post.setComments((ArrayList<Comment>) documentSnapshot.get("comments"));
                            post.setCommentCount(Integer.parseInt(documentSnapshot.get("commentCount").toString()));
                            list.add(0, post);
                            postAdapter.notifyDataSetChanged();
                        }

                    }

                });
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(postAdapter);
        //this should scroll to saved position on post activity after returning from veiwng the post
        //the savedPosition value is correct but not scrolling to the index in tnhe recycleview for some reason
        recyclerView.getLayoutManager().scrollToPosition(ViewPostActivity.savedPosition);
        System.out.println(ViewPostActivity.savedPosition);




    }




    public void bottomNavigate(ActivityPostBinding binding){
        BottomNavigationView bottomNavigationView = binding.getRoot().findViewById(R.id.bnav_post_bottom);
        bottomNavigationView.getMenu().findItem(R.id.post_home).setChecked(true);
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



}