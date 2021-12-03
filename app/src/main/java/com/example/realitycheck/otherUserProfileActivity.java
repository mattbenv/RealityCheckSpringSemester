package com.example.realitycheck;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realitycheck.adapter.CommentAdapter;
import com.example.realitycheck.adapter.FollowerAdapter;
import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.adapter.SearchAdapter;
import com.example.realitycheck.databinding.ActivityProfileBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class otherUserProfileActivity extends Fragment {
    private ActivityProfileBinding binding;
    public static PostAdapter postAdapter;
    public ListenerRegistration listenerRegistration;
    private RecyclerView recyclerView;
    public static User thisUser;
    public static User previousUser;
    public CollectionReference database;
    public ArrayList<Post> list;
    public static String previousActivty;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        if(previousActivty == "post" || previousActivty == "taggedIn"){
            thisUser = PostAdapter.userToNavTo;
        }
        if(previousActivty == "search"){
            thisUser = SearchAdapter.selectedUser;
        }
        if(previousActivty == "comment"){
            thisUser = CommentAdapter.userToNavTo;
        }
        if(previousActivty == "follower"){
            thisUser = FollowerAdapter.selectedUser;
        }
        if(previousActivty == "back"){
            thisUser = previousUser;
        }


        PostAdapter.postPage = "otherProfile";
        FollowersView.previousActivity = "otherprofile";

        binding  =  ActivityProfileBinding.inflate(inflater, container, false);
        recyclerView = binding.getRoot().findViewById(R.id.rl_post_box);
        setPosts();
        FollowersView.currUserProfile = false;
        TaggedInView.taggedInType = false;
        MainActivity.toolbar.hide();
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.username.setText(thisUser.username);
        binding.UserBio.setText(thisUser.bio);
        binding.realName.setText(thisUser.name);
        int numFollowers = thisUser.followers.size();
        int numFollowing = thisUser.following.size();

        binding.imageViewBack.setVisibility(View.GONE);


        binding.followerCount.setText(String.valueOf(numFollowers));
        binding.followingCount.setText(String.valueOf(numFollowing));
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(otherUserProfileActivity.this)
                        .navigate(R.id.action_OtherUserProfileActivity_to_SearchActivity);

            }


        });

        //sets profile picture
        ImageView imageView = this.getView().findViewById(R.id.profilePic);

        FirebaseStorage.getInstance().getReference().child("images/"+thisUser.profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(binding.getRoot().getContext())
                        .load(uri)
                        .into(imageView);
            }
        });


        if(thisUser.taggedIn!=null) {
            binding.tagCount.setText(String.valueOf(thisUser.taggedIn.size()));


        }
        binding.taggedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(otherUserProfileActivity.this)
                        .navigate(R.id.action_otherUserProfileActivity_to_TaggedIn);

            }
        });


        binding.following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowersView.previousActivity = "";
                FollowersView.followersViewType = false;
                NavHostFragment.findNavController(otherUserProfileActivity.this)
                        .navigate(R.id.action_otherUserProfileActivity_to_ViewFollowersActivity);

            }
        });
        binding.followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FollowersView.previousActivity = "";
                FollowersView.followersViewType = true;
                NavHostFragment.findNavController(otherUserProfileActivity.this)
                        .navigate(R.id.action_otherUserProfileActivity_to_ViewFollowersActivity);

            }
        });

        binding.buttonfollow.setVisibility(View.VISIBLE);
        binding.buttonfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFollow();
            }
        });

        if(LoginPage.currUser.following.contains(thisUser.username)){
            binding.buttonfollow.setText("Unfollow");
        }
        else{
            binding.buttonfollow.setText("Follow");
            if(thisUser.privateMode == true){
                binding.personalinfo.setVisibility(LinearLayout.GONE);
                binding.rlPostBox.setVisibility(RecyclerView.GONE);
                binding.privateMessage.setVisibility(TextView.VISIBLE);
            }
        }

    }

    public void handleFollow(){
        if (!LoginPage.currUser.following.contains(thisUser.username)) {
            LoginPage.currUser.following.add(thisUser.username);
            FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username).update("following", LoginPage.currUser.following);
            thisUser.followers.add(LoginPage.currUser.username);
            FirebaseFirestore.getInstance().collection("Users").document(thisUser.username).update("followers", thisUser.followers);
            int numFollowers = thisUser.followers.size();
            binding.followerCount.setText(String.valueOf(numFollowers));
            binding.buttonfollow.setText("Unfollow");
            if(thisUser.privateMode == true){
                binding.personalinfo.setVisibility(View.VISIBLE);
                binding.rlPostBox.setVisibility(View.VISIBLE);
                binding.privateMessage.setVisibility(View.GONE);
            }
        }
        else if(LoginPage.currUser.following.contains(thisUser.username)){
            LoginPage.currUser.following.remove(thisUser.username);
            FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username).update("following", LoginPage.currUser.following);
            thisUser.followers.remove(LoginPage.currUser.username);
            FirebaseFirestore.getInstance().collection("Users").document(thisUser.username).update("followers",thisUser.followers);
            int numFollowers = thisUser.followers.size();
            binding.followerCount.setText(String.valueOf(numFollowers));
            binding.buttonfollow.setText("Follow");
            if(thisUser.privateMode == true){
                binding.personalinfo.setVisibility(View.GONE);
                binding.rlPostBox.setVisibility(View.GONE);
                binding.privateMessage.setVisibility(View.VISIBLE);
            }
        }
    }


    public void setPosts(){
        //setup new postadapter on profile page

        database = FirebaseFirestore.getInstance().collection("Posts");
        list = new ArrayList<Post>();
        PostAdapter postAdapter = new PostAdapter(this.getContext(),list);

        for(int i= 0;i<thisUser.posts.size();i++){
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Posts").document(thisUser.posts.get(i));
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Post post = new TextPost();
                    post.setPostAuthor(documentSnapshot.get("postAuthor").toString());
                    post.setPostDate(documentSnapshot.get("postDate").toString());
                    post.setContent(documentSnapshot.get("content").toString());
                    post.setLikeCount(Integer.parseInt(documentSnapshot.get("likeCount").toString()));
                    post.setPostId(documentSnapshot.get("postId").toString());
                    //need to fix
                    post.setLikedBy((ArrayList<String>) documentSnapshot.get("likedBy"));
                    post.setRepostedBy((ArrayList<String>) documentSnapshot.get("repostedBy"));
                    post.setRepostCount(Integer.parseInt(documentSnapshot.get("repostCount").toString()));
                    post.setComments((ArrayList<Comment>) documentSnapshot.get("comments"));
                    post.setCommentCount(Integer.parseInt(documentSnapshot.get("commentCount").toString()));
                    if(documentSnapshot.get("photo")!=null){
                        post.setPhoto(documentSnapshot.get("photo").toString());
                    }
                    list.add(0,post);
                    postAdapter.notifyDataSetChanged();

                }

            });

        }

        if(thisUser.reposted!=null) {
            for (int j = 0; j < thisUser.reposted.size(); j++) {
                DocumentReference docReference = FirebaseFirestore.getInstance().collection("Posts").document(thisUser.reposted.get(j));
                docReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                        post.setComments((ArrayList<Comment>) documentSnapshot.get("comments"));
                        post.setCommentCount(Integer.parseInt(documentSnapshot.get("commentCount").toString()));
                        if (documentSnapshot.get("photo") != null) {
                            post.setPhoto(documentSnapshot.get("photo").toString());
                        }
                        list.add(0, post);
                        postAdapter.notifyDataSetChanged();
                    }

                });
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(postAdapter);



    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        previousUser = thisUser;
        binding = null;
    }


}



