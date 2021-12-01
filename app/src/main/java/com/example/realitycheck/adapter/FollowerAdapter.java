package com.example.realitycheck.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.realitycheck.LoginPage;
import com.example.realitycheck.R;
import com.example.realitycheck.User;
import com.example.realitycheck.databinding.ItemSearchUserBinding;
import com.example.realitycheck.otherUserProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.FollowerViewHolder>{

    private Context context;
    public ArrayList<User> users;
    public static User selectedUser;
    public FirebaseFirestore fStore;


    public FollowerAdapter(Context context, ArrayList<User> user) {
        this.context = context;
        users = user;

    }

    @NonNull
    @Override
    public FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchUserBinding binding = ItemSearchUserBinding.inflate(LayoutInflater.from(context), parent, false);
        FollowerViewHolder followerViewHolder = new FollowerViewHolder(binding);
        return followerViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull FollowerViewHolder holder, int position) {

        //sets this variable up for navigation purposes on otherUserProfileActivity
        otherUserProfileActivity.previousActivty = "back";

       //click profile photo to navigate to the user's profile
        holder.binding.sivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedUser = users.get(position);
                otherUserProfileActivity.previousActivty = ("follower");
                Navigation.createNavigateOnClickListener(R.id.to_OtherUserProfileActivity).onClick(holder.binding.sivAvatar);
            }
        });

        //set username and real name
        holder.userName.setText(users.get(position).username);
        holder.realName.setText(users.get(position).name);

        //follow user button
        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedUser = users.get(position);
                handleFollow(holder);

            }
        });
        ImageView imageView = holder.profilePicture;

        FirebaseStorage.getInstance().getReference().child("images/"+users.get(position).profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.binding.getRoot().getContext())
                        .load(uri)
                        .into(imageView);
            }
        });


    }

    //follows or unfollows the user and updates the relevant fields in the database
    public void handleFollow(FollowerViewHolder holder){
        fStore = FirebaseFirestore.getInstance();
        LottieAnimationView animationView  = holder.binding.blackcheck;
        if (!LoginPage.currUser.following.contains(selectedUser.username)) {
            LoginPage.currUser.following.add(selectedUser.username);
            fStore.collection("Users").document(LoginPage.currUser.username).update("following", LoginPage.currUser.following);
            selectedUser.followers.add(LoginPage.currUser.username);
            fStore.collection("Users").document(selectedUser.username).update("followers", selectedUser.followers);
            // Declaring the animation view
            animationView
                    .addAnimatorUpdateListener(
                            (animation) -> {
                                // Do something.
                            });
            animationView
                    .playAnimation();
            if (animationView.isAnimating()) {
                // Do something.
            }
            Toast.makeText(FollowerAdapter.this.context, ("Followed " + selectedUser.username), Toast.LENGTH_SHORT).show();
        }
        else if(LoginPage.currUser.following.contains(selectedUser.username)){
            LoginPage.currUser.following.remove(selectedUser.username);
            fStore.collection("Users").document(LoginPage.currUser.username).update("following", LoginPage.currUser.following);
            selectedUser.followers.remove(LoginPage.currUser.username);
            fStore.collection("Users").document(selectedUser.username).update("followers", selectedUser.followers);
            Toast.makeText(FollowerAdapter.this.context, ("Unfollowed " + selectedUser.username), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class FollowerViewHolder extends RecyclerView.ViewHolder {
        ItemSearchUserBinding binding;
        TextView userName;
        TextView realName;
        ImageView profilePicture;
        public FollowerViewHolder(ItemSearchUserBinding binding){
            super(binding.getRoot());
            this.binding = binding;
            userName = this.binding.tvTitle;
            realName = this.binding.tvDescription;
            profilePicture = this.binding.sivAvatar;
        }

    }
}