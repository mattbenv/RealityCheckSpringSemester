package com.example.realitycheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.databinding.ActivityTaggedInBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;

public class TaggedInView extends Fragment {
    private ActivityTaggedInBinding binding;
    private RecyclerView recyclerView;
    Query database;
    public static PostAdapter postAdapter;
    ArrayList<Post> list;
    public static boolean taggedInType;
    public static String previousActivity;
    public User userToUse;


    //set to true means view followers false means view following
    public static boolean followersViewType;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  ActivityTaggedInBinding.inflate(inflater, container, false);

        PostAdapter.postPage = "taggedIn";
        otherUserProfileActivity.previousActivty =  "taggedIn";
        recyclerView = binding.rlSearchBox;

        list = new ArrayList<Post>();

        postAdapter = new PostAdapter(this.getContext(),list);


        if(taggedInType == true){
            userToUse = LoginPage.currUser;
        }
        if(taggedInType == false){
            userToUse = otherUserProfileActivity.thisUser;
        }
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(userToUse.username);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userToUse.taggedIn = (ArrayList<String>) documentSnapshot.get("taggedIn");
                    if (userToUse.taggedIn != null) {
                        for (int i = 0; i <userToUse.taggedIn.size(); i++) {
                            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Posts").document(userToUse.taggedIn.get(i));
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

                        binding.rlSearchBox.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.rlSearchBox.setHasFixedSize(true);
                        binding.rlSearchBox.addItemDecoration(new LinearLayoutDivider(getContext(), LinearLayoutManager.VERTICAL));
                        binding.rlSearchBox.setAdapter(postAdapter);

                    }
                }
            });




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