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
    public List<String> userList;
    public List<String> allUsers;
    public List<String> names;
    public ArrayList<User> users;
    public static User selectedUser;
    public static Uri storageProfilePictureReference;

    public List<String> getUserList() {
        //test values
        userList = new ArrayList<>();
        userList.add("AlexUsername");
        userList.add("AdamUser");
        userList.add("Brian123123");
        userList.add("Tomtom123");
        userList.add("UserMatt");
        userList.add("EmilytheOG");
        userList.add("Sam1");
        userList.add("Samantha2");
        userList.add("Alexander_the_great");
        userList.add("Ralph344");
        userList.add("Noah1222");
        userList.add("Xaiver");
        userList.add("Ryan333");
        userList.add("James");
        userList.add("GusBus11");
        names = new ArrayList<>();
        names.add("Alex");
        names.add("Adam");
        names.add("Brian");
        names.add("Tom");
        names.add("Matt");
        names.add("Emily");
        names.add("Sam");
        names.add("Samantha");
        names.add("Alexander");
        names.add("Ralph");
        names.add("Noah");
        names.add("Xaiver");
        names.add("Ryan");
        names.add("James");
        names.add("Gus");
        return userList;
    }

    public SearchAdapter(Context context, ArrayList<User> user) {
        this.context = context;
        userList = getUserList();
        allUsers = getUserList();
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

        selectedUser = users.get(position);


        holder.binding.sivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedUser = users.get(position);
                otherUserProfileActivity.previousActivty= "search";
                Navigation.createNavigateOnClickListener(R.id.to_OtherUserProfileActivity).onClick(holder.binding.sivAvatar);
            }
        });
        holder.userName.setText(selectedUser.username);
        holder.realName.setText(selectedUser.name);

        //follow user button
        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LottieAnimationView animationView  = holder.binding.blackcheck;
                if (!LoginPage.currUser.following.contains(selectedUser.username)) {
                    LoginPage.currUser.following.add(selectedUser.username);
                    FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username).update("following", LoginPage.currUser.following);
                    selectedUser.followers.add(LoginPage.currUser.username);
                    FirebaseFirestore.getInstance().collection("Users").document(selectedUser.username).update("followers", selectedUser.followers);
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
                    FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username).update("following", LoginPage.currUser.following);
                    selectedUser.followers.remove(LoginPage.currUser.username);
                    FirebaseFirestore.getInstance().collection("Users").document(selectedUser.username).update("followers", selectedUser.followers);
                    Toast.makeText(SearchAdapter.this.context, ("Unfollowed " + selectedUser.username), Toast.LENGTH_SHORT).show();
                }
            }
        });
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