package com.example.realitycheck.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import java.util.Collection;
import java.util.List;

//todo: understand how homepage works which is related to post adapter/viewholder/activity. if we can, design new one
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> implements Filterable {

    private Context context;
    public List<String> names;
    public ArrayList<User> users;
    public static User selectedUser;
    public FirebaseFirestore fStore;
    public static Uri storageProfilePictureReference;



    public SearchAdapter(Context context, ArrayList<User> user) {
        this.context = context;
        users = user;

    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchUserBinding binding = ItemSearchUserBinding.inflate(LayoutInflater.from(context), parent, false);
        SearchViewHolder searchViewHolder = new SearchViewHolder(binding);
        return searchViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        /*String user = userList.get(position);
        holder.binding.tvTitle.setText(user);
        String name = names.get(position);
        holder.binding.tvDescription.setText(name);

         */
        //initialize database
        fStore = FirebaseFirestore.getInstance();

        //initialize selected user
        selectedUser = users.get(position);



        //on click of user profile photo navigates to their profile page
        holder.binding.sivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedUser = users.get(position);
                otherUserProfileActivity.previousActivty= "search";
                Navigation.createNavigateOnClickListener(R.id.to_OtherUserProfileActivity).onClick(holder.binding.sivAvatar);
            }
        });

        //sets real name and username
        holder.userName.setText(selectedUser.username);
        holder.realName.setText(selectedUser.name);

        //follow user button
        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFollow(holder);
            }
        });
        //loads profile photos
        ImageView imageView = holder.profilePicture;
        FirebaseStorage.getInstance().getReference().child("images/"+selectedUser.profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.binding.getRoot().getContext())
                        .load(uri)
                        .into(imageView);
            }
        });






    }

    //follows or unfollows the user and updates the relevant fields in the database
    public void handleFollow(SearchAdapter.SearchViewHolder holder){
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
            Toast.makeText(SearchAdapter.this.context, ("Followed " + selectedUser.username), Toast.LENGTH_SHORT).show();
        }
        else if(LoginPage.currUser.following.contains(selectedUser.username)){
            LoginPage.currUser.following.remove(selectedUser.username);
            fStore.collection("Users").document(LoginPage.currUser.username).update("following", LoginPage.currUser.following);
            selectedUser.followers.remove(LoginPage.currUser.username);
            fStore.collection("Users").document(selectedUser.username).update("followers", selectedUser.followers);
            Toast.makeText(SearchAdapter.this.context, ("Unfollowed " + selectedUser.username), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addData(User newItem) {
        users.add(newItem);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    //sets up filter for filtering lists that gets displayed
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<User> resultList = new ArrayList<>();
            if (!charSequence.toString().isEmpty()) {
                for (User user : users) {
                    if (user.username.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        resultList.add(user);
                    }
                }
            } else {
                //resultList.addAll(allUsers);
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = resultList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            users.clear();
            users.addAll((Collection<? extends User>) filterResults.values);
            notifyDataSetChanged();

        }
    };


    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        ItemSearchUserBinding binding;
        TextView userName;
        TextView realName;
        ImageView profilePicture;
        public SearchViewHolder(ItemSearchUserBinding binding){
            super(binding.getRoot());
            this.binding = binding;
            userName = this.binding.tvTitle;
            realName = this.binding.tvDescription;
            profilePicture = this.binding.sivAvatar;
        }

    }
}