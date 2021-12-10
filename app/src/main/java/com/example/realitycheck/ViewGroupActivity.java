package com.example.realitycheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realitycheck.adapter.GroupAdapter;
import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.adapter.SearchAdapter;
import com.example.realitycheck.databinding.ActivityViewGroupBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewGroupActivity extends Fragment {
    private ActivityViewGroupBinding binding;

    private RecyclerView recyclerView;
    public static SearchAdapter searchAdapter;
    ArrayList<User> userList;
    ArrayList<Post> postList;
    public static PostAdapter postAdapter;
    public static Group group;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = ActivityViewGroupBinding.inflate(inflater, container, false);

        PostAdapter.postPage = "groupView";
        group = GroupAdapter.group;

        binding.groupname.setText(group.groupName);
        binding.members.setText(group.size + " members");
        binding.tvContent.setText(group.bio);

        if(group.members.contains(LoginPage.currUser.username)){
            binding.buttonJoin.setText("Leave");
        }
        if(!group.members.contains(LoginPage.currUser.username)){
            binding.buttonJoin.setText("Join");
            if(group.privacy == true){
                binding.tvContent.setVisibility(View.GONE);
                binding.uline.setVisibility(View.GONE);
                binding.pline.setVisibility(View.GONE);
                binding.posts.setVisibility(View.GONE);
                binding.users.setVisibility(View.GONE);
                binding.privateMessage.setVisibility(TextView.VISIBLE);
            }


        }
        binding.addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateGroupPostActivity.thisGroup = group;
                NavHostFragment.findNavController(ViewGroupActivity.this).navigate(R.id.action_ViewGroupActivity_to_CreateGroupPost);
            }
        });

        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(group.owner.compareTo(LoginPage.currUser.username)==0){
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete Group")
                            .setMessage("Are you sure you want to delete this group?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    for(String post:group.posts){
                                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Posts").document(post);
                                        documentReference.delete();
                                    }
                                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Groups").document(group.groupName);
                                    documentReference.delete();
                                    NavHostFragment.findNavController(ViewGroupActivity.this)
                                            .navigate(R.id.action_ViewGroupActivity_to_ProfileActivity);

                                }
                            }).setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

        binding.buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (group.members.contains(LoginPage.currUser.username)) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Leave Group")
                            .setMessage("Are you sure you want to leave " + group.groupName + "?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    group.members.remove(LoginPage.currUser.username);
                                    group.size = group.members.size();
                                    binding.members.setText(group.size + " members");
                                    binding.buttonJoin.setText("Join");
                                    if(group.privacy){
                                        binding.tvContent.setVisibility(View.GONE);
                                        binding.uline.setVisibility(View.GONE);
                                        binding.pline.setVisibility(View.GONE);
                                        binding.posts.setVisibility(View.GONE);
                                        binding.users.setVisibility(View.GONE);
                                        binding.privateMessage.setVisibility(TextView.VISIBLE);
                                    }
                                    Toast.makeText(getContext(), ("Left " + group.groupName), Toast.LENGTH_SHORT).show();
                                    FirebaseFirestore.getInstance().collection("Groups").document(group.groupName).update("members", group.members);
                                    FirebaseFirestore.getInstance().collection("Groups").document(group.groupName).update("size", group.size);
                                }
                            }).setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else if (!group.members.contains(LoginPage.currUser.username)) {

                    group.members.add(LoginPage.currUser.username);
                    group.size = group.members.size();
                    binding.members.setText(group.size + " members");
                    binding.buttonJoin.setText("Leave");
                    if(group.privacy){
                        binding.tvContent.setVisibility(View.VISIBLE);
                        binding.uline.setVisibility(View.VISIBLE);
                        binding.pline.setVisibility(View.VISIBLE);
                        binding.posts.setVisibility(View.VISIBLE);
                        binding.users.setVisibility(View.VISIBLE);
                        binding.privateMessage.setVisibility(TextView.GONE);
                    }

                    Toast.makeText(getContext(), ("Joined " + group.groupName), Toast.LENGTH_SHORT).show();
                    FirebaseFirestore.getInstance().collection("Groups").document(group.groupName).update("members", group.members);
                    FirebaseFirestore.getInstance().collection("Groups").document(group.groupName).update("size", group.size);

                }
            }
        });


        //loads group photo if exists if not it hides the imageview
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Groups").document(group.groupName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> userMap = documentSnapshot.getData();
                if(userMap.get("profileImagePath")!= null){
                    String profileImagePath = userMap.get("profileImagePath").toString();
                   binding.sivAvatar.setVisibility(View.VISIBLE);
                    binding.avatarCard.setVisibility(View.VISIBLE);
                    // Reference to an image file in Cloud Storage
                    FirebaseStorage.getInstance().getReference().child("images/"+profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ImageView imageView = binding.sivAvatar;
                            Glide.with(getContext())
                                    .load(uri)
                                    .into(imageView);
                        }
                    });
                }
                if(userMap.get("profileImagePath")== null){
                    binding.sivAvatar.setVisibility(View.GONE);
                    binding.avatarCard.setVisibility(View.GONE);
                }
            }
        });


        recyclerView = binding.users;

        userList = new ArrayList<User>();

        searchAdapter = new SearchAdapter(this.getContext(),userList);

        if (group.members.size() > 0) {
            for (int i = 0; i < group.members.size(); i++) {
                FirebaseFirestore.getInstance().collection("Users").whereEqualTo("username",group.members.get(i)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot val : queryDocumentSnapshots.getDocuments()) {

                            User selectedUser = new User();
                            selectedUser.email = val.get("email").toString();
                            selectedUser.username = val.get("username").toString();
                            selectedUser.name = val.get("name").toString();
                            selectedUser.bio = val.get("bio").toString();
                            selectedUser.birthday = val.get("birthday").toString();
                            if (!(val.get("profileImagePath") == null)) {
                                selectedUser.profileImagePath = val.get("profileImagePath").toString();
                            }
                            selectedUser.posts = (ArrayList<String>) val.get("posts");
                            selectedUser.following = (ArrayList<String>) val.get("following");
                            selectedUser.followers = (ArrayList<String>) val.get("followers");
                            selectedUser.friends = (ArrayList<String>) val.get("friends");
                            selectedUser.taggedIn = (ArrayList<String>) val.get("taggedIn");
                            selectedUser.reposted = (ArrayList<String>) val.get("reposted");
                            //User user = val.toObject(User.class);
                            userList.add(selectedUser);


                        }
                        searchAdapter.notifyDataSetChanged();
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.addItemDecoration(new LinearLayoutDivider(getContext(), LinearLayoutManager.VERTICAL));

                        recyclerView.setAdapter(searchAdapter);

                    }
                });
            }
        }

        postList = new ArrayList<Post>();

        postAdapter = new PostAdapter(this.getContext(),postList);

        if (group.posts.size() > 0) {
            for (int i = 0; i < group.posts.size(); i++) {
                FirebaseFirestore.getInstance().collection("Posts").whereEqualTo("postId",group.posts.get(i)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
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
                            postList.add(0, post);


                        }
                        postAdapter.notifyDataSetChanged();
                        binding.posts.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.posts.setHasFixedSize(true);
                        binding.posts.addItemDecoration(new LinearLayoutDivider(getContext(), LinearLayoutManager.VERTICAL));

                        binding.posts.setAdapter(postAdapter);

                    }
                });
            }
        }





        return binding.getRoot();
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}