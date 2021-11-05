package com.example.realitycheck.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.realitycheck.Post;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realitycheck.LoginPage;
import com.example.realitycheck.R;
import com.example.realitycheck.User;
import com.example.realitycheck.Post;
import com.example.realitycheck.databinding.ItemPostBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
//todo: understand how homepage works which is related to post adapter/viewholder/activity. if we can, design new one
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;

    public Uri postAuthorProfilePhoto;

    private ArrayList<Post> postList;
    int[] likeCount;
    public int likes;

    public PostAdapter(Context context, ArrayList<Post> post) {
        this.context = context;
        postList = post;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false);
        PostViewHolder postViewHolder = new PostViewHolder(binding);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        //gets current post from the recycler view list based its position
        Post post = postList.get(position);

        //gets post values
        String content = post.getContent();
        String postID = post.getPostId();
        String currentDate = post.getPostDate();
        String author = post.getPostAuthor();
        likes = post.getLikeCount();
        ArrayList<String> likedBy = post.getLikedBy();
        //initialize
        likeCount = new int[1];
        likeCount[0] = likes;


        //displays values using item_post layout
        holder.binding.tvLike.setText(String.valueOf(likes));
        holder.binding.tvTitle.setText(author);
        holder.binding.tvContent.setText(content);
        holder.binding.date.setText(currentDate);


        //gets the post authors profile photo and displays it on the posts
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(author);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Map<String,Object> userMap = value.getData();
                String profileImagePath = userMap.get("profileImagePath").toString();
                // Reference to an image file in Cloud Storage
                FirebaseStorage.getInstance().getReference().child("images/"+profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        postAuthorProfilePhoto = uri;
                        ImageView imageView = holder.binding.sivAvatar;
                        Glide.with(context)
                                .load(postAuthorProfilePhoto)
                                .into(imageView);
                    }
                });
            }
        });

        holder.binding.ivCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        holder.binding.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLike(postID,post,holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //adds like to post in database and on the view
    public void handleLike(String postID,Post post, PostViewHolder holder){
        DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(postID.toString());
        //commented out for now but limits to one like per user
        //if(!post.getLikedBy().contains(LoginPage.currUser.username)){
        likes = post.getLikeCount()+1;
        post.setLikeCount(likes);
        document.update("likeCount",likes);
        holder.binding.tvLike.setText(Integer.toString(likes));
        document.update("likedBy",LoginPage.currUser.username);
        post.addToLikedBy(LoginPage.currUser.username);
        //}
    }
    public void addData(Post newItem) {
        postList.add(0, newItem);
        notifyItemInserted(0);
        notifyItemInserted(postList.size());
        notifyItemChanged(postList.size());
    }


    class PostViewHolder extends RecyclerView.ViewHolder {
        ItemPostBinding binding;
        TextView postAuthor;
        TextView postContent;
        TextView postDate;
        TextView likeCount;
        ImageView postAuhtorProfileImage;



        public PostViewHolder(ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            postAuthor = this.binding.tvTitle;
            postContent = this.binding.tvContent;
            postDate = this.binding.date;
            likeCount = this.binding.tvLike;
            postAuhtorProfileImage = this.binding.sivAvatar;


        }
    }
}
