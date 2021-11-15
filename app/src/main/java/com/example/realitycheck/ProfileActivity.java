

package com.example.realitycheck;

import android.content.DialogInterface;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realitycheck.adapter.PostAdapter;
import com.example.realitycheck.databinding.ActivityProfileBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

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

        ImageView imageView = this.getView().findViewById(R.id.profilePic);
        Glide.with(this.getContext())
                .load(LoginPage.storageProfilePictureReference)
                .into(imageView);



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
        //setup new postadapter on profile page

        database = FirebaseFirestore.getInstance().collection("Posts");
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
                    post.setComments((ArrayList<HashMap<String,Object>>) documentSnapshot.get("comments"));
                    post.setCommentCount(Integer.parseInt(documentSnapshot.get("commentCount").toString()));
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




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
