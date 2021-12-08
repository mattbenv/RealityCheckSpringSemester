

package com.example.realitycheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realitycheck.adapter.GroupAdapter;
import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.databinding.ActivityProfileBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends Fragment {
    private ActivityProfileBinding binding;
    public static PostAdapter postAdapter;

    public ListenerRegistration listenerRegistration;
    private RecyclerView recyclerView;
    public CollectionReference database;
    public ArrayList<Post> list;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding  =  ActivityProfileBinding.inflate(inflater, container, false);
        recyclerView = binding.getRoot().findViewById(R.id.rl_post_box);
        PostAdapter.postPage = "profile";
        setPosts();
        setGroups();
        MainActivity.toolbar.hide();
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.username.setText(LoginPage.currUser.username);
        binding.UserBio.setText(LoginPage.currUser.bio);
        binding.buttonfollow.setVisibility(View.GONE);
        binding.realName.setText(LoginPage.currUser.name);
        int numFollowers = LoginPage.currUser.followers.size();
        int numFollowing = LoginPage.currUser.following.size();
        binding.followingCount.setText(String.valueOf(numFollowing));
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileActivity.this)
                        .navigate(R.id.action_ProfileActivity_to_PostActivity);

            }


        });


        FollowersView.currUserProfile = true;
        TaggedInView.taggedInType = true;
        GroupAdapter.thisPage = "profile";

        DocumentReference docReff = FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username);
        docReff.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                LoginPage.currUser.followers = (ArrayList<String>) documentSnapshot.get("followers");
            }

        });
        binding.followerCount.setText(String.valueOf(LoginPage.currUser.followers.size()));



        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                LoginPage.currUser.taggedIn = (ArrayList<String>) documentSnapshot.get("taggedIn");
            }
        });

        binding.tagCount.setText(String.valueOf(LoginPage.currUser.taggedIn.size()));



        //sets profile picture

        /*
        ImageView imageView = this.getView().findViewById(R.id.profilePic);
        Glide.with(this.getContext())
                .load(LoginPage.storageProfilePictureReference)
                .into(imageView);

         */

        DocumentReference dRef = FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username);
        dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> userMap = documentSnapshot.getData();
                String profileImagePath = userMap.get("profileImagePath").toString();
                // Reference to an image file in Cloud Storage
                FirebaseStorage.getInstance().getReference().child("images/"+profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageView imageView = binding.profilePic;
                        Glide.with(getContext())
                                .load(uri)
                                .into(imageView);
                    }
                });
            }
        });

        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileActivity.this)
                        .navigate(R.id.action_ProfileActivity_to_SettingsPage);
            }
        });

        binding.taggedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileActivity.this)
                        .navigate(R.id.action_ProfileActivity_to_TaggedIn);

            }
        });

        binding.following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowersView.followersViewType = false;
                NavHostFragment.findNavController(ProfileActivity.this)
                        .navigate(R.id.action_ProfileActivity_to_ViewFollowersActivity);

            }
        });
        binding.followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowersView.followersViewType = true;
                NavHostFragment.findNavController(ProfileActivity.this)
                        .navigate(R.id.action_ProfileActivity_to_ViewFollowersActivity);

            }
        });
        binding.addgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Add group")
                        .setMessage("Create new group or join existing group?")
                        .setPositiveButton("Create New Group", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                NavHostFragment.findNavController(ProfileActivity.this)
                                        .navigate(R.id.action_ProfileActivity_to_CreateGroupActivity);
                            }
                        }).setNegativeButton("Join A Group", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SearchPage.previous = "profile";
                        NavHostFragment.findNavController(ProfileActivity.this)
                                .navigate(R.id.action_ProfileActivity_to_SearchPage);

                    }
                }).setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });


    }

    public void setGroups(){
        ArrayList<Group> groupList= new ArrayList<Group>();
        GroupAdapter groupAdapter = new GroupAdapter(this.getContext(),groupList);
        FirebaseFirestore.getInstance().collection("Groups").whereArrayContains("members",LoginPage.currUser.username).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot:documentSnapshots){
                    Group group = new Group();
                    group.groupName = (String) documentSnapshot.get("groupName");
                    group.bio = (String) documentSnapshot.get("bio");
                    group.members = (ArrayList<String>)documentSnapshot.get("members");
                    group.size = group.members.size();
                    group.privacy = (boolean) documentSnapshot.get("privacy");
                    group.profileImagePath = (String) documentSnapshot.get("profileImagePath");
                    group.posts = (ArrayList<String>)documentSnapshot.get("posts");
                    groupList.add(0,group);
                    groupAdapter.notifyDataSetChanged();


                }
            }
        });
        binding.rlGroupBox.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.rlGroupBox.setHasFixedSize(true);
        binding.rlGroupBox.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));
        binding.rlGroupBox.setAdapter(groupAdapter);

    }

    public void setPosts(){
        //setup new postadapter on profile page

        database = FirebaseFirestore.getInstance().collection("Posts");
        list = new ArrayList<Post>();
        PostAdapter postAdapter = new PostAdapter(this.getContext(),list);


        for(int i= 0;i<LoginPage.currUser.posts.size();i++) {
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
                    post.setRepostedBy((ArrayList<HashMap<String, String>>) documentSnapshot.get("repostedBy"));
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
        if(LoginPage.currUser.reposted!=null) {
            for (int j = 0; j < LoginPage.currUser.reposted.size(); j++) {
                DocumentReference docReference = FirebaseFirestore.getInstance().collection("Posts").document(LoginPage.currUser.reposted.get(j));
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
                        post.setRepostedBy((ArrayList<HashMap<String, String>>)documentSnapshot.get("repostedBy"));
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
        GroupAdapter.thisPage = "";
        binding = null;
    }


}

