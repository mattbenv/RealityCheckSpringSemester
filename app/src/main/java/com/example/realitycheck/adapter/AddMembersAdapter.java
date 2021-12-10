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
import com.example.realitycheck.CreateGroupActivity;
import com.example.realitycheck.Group;
import com.example.realitycheck.R;
import com.example.realitycheck.User;
import com.example.realitycheck.databinding.ItemSearchUserBinding;
import com.example.realitycheck.otherUserProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AddMembersAdapter extends RecyclerView.Adapter<AddMembersAdapter.AddMembersHolder> implements Filterable {

    private Context context;
    public List<String> names;
    public ArrayList<User> users;
    public static Group currGroup;
    public static User selectedUser;
    public FirebaseFirestore fStore;
    public static Uri storageProfilePictureReference;



    public AddMembersAdapter(Context context, ArrayList<User> user) {
        this.context = context;
        users = user;

    }

    @NonNull
    @Override
    public AddMembersAdapter.AddMembersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchUserBinding binding = ItemSearchUserBinding.inflate(LayoutInflater.from(context), parent, false);
        AddMembersAdapter.AddMembersHolder addMembersHolder = new AddMembersAdapter.AddMembersHolder(binding);
        return addMembersHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull AddMembersAdapter.AddMembersHolder holder, int position) {
        /*String user = userList.get(position);
        holder.binding.tvTitle.setText(user);
        String name = names.get(position);
        holder.binding.tvDescription.setText(name);

         */
        //initialize database
        fStore = FirebaseFirestore.getInstance();

        //initialize selected user
        selectedUser = users.get(position);

        if (!CreateGroupActivity.newGroup.members.contains(selectedUser.username)) {
            holder.binding.ivMore.setBackgroundResource(R.drawable.ic_add_24);
        }
        else if(CreateGroupActivity.newGroup.members.contains(selectedUser.username)){
            holder.binding.ivMore.setBackgroundResource(R.drawable.fui_ic_check_circle_black_128dp);
        }



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
                selectedUser = users.get(position);
               addMember(holder);
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
    public void addMember(AddMembersHolder holder){
        LottieAnimationView animationView  = holder.binding.blackcheck;
        FirebaseFirestore.getInstance().collection("Groups").document(CreateGroupActivity.newGroupName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String owner = (String) documentSnapshot.get("owner");
                String groupName = (String) documentSnapshot.get("groupName");
                String bio = (String) documentSnapshot.get("bio");
                ArrayList<String> members = (ArrayList<String>)documentSnapshot.get("members");
                Boolean privacy = (boolean) documentSnapshot.get("privacy");
                String profileImagePath = (String) documentSnapshot.get("profileImagePath");
                ArrayList<String> posts = (ArrayList<String>)documentSnapshot.get("posts");
                currGroup = new Group(owner,groupName,bio,profileImagePath,privacy,posts,members);

                if(currGroup.members == null){
                    currGroup.members = new ArrayList<String>();
                }

                /*if(currGroup.members.contains(selectedUser.username)){
                    currGroup.members.remove(selectedUser.username);


                    Toast.makeText(AddMembersAdapter.this.context, ("Removed " + selectedUser.username +" from your group"), Toast.LENGTH_SHORT).show();

                }

                 */
                if(!currGroup.members.contains(selectedUser.username)){
                    animationView.setVisibility(View.VISIBLE);
                    currGroup.members.add(selectedUser.username);
                    holder.binding.ivMore.setBackgroundResource(R.drawable.fui_ic_check_circle_black_128dp);

                    FirebaseFirestore.getInstance().collection("Groups").document(CreateGroupActivity.newGroupName).update("members",AddMembersAdapter.currGroup.members);
                    FirebaseFirestore.getInstance().collection("Groups").document(CreateGroupActivity.newGroupName).update("size",AddMembersAdapter.currGroup.members.size());
                    Toast.makeText(AddMembersAdapter.this.context, ("Added " + selectedUser.username +" to your group"), Toast.LENGTH_SHORT).show();
                }
                else if(currGroup.members.contains(selectedUser.username)){
                    animationView.setVisibility(View.GONE);
                    holder.binding.ivMore.setBackgroundResource(R.drawable.ic_add_24);
                    currGroup.members.remove(selectedUser.username);
                    FirebaseFirestore.getInstance().collection("Groups").document(CreateGroupActivity.newGroupName).update("members",AddMembersAdapter.currGroup.members);
                    FirebaseFirestore.getInstance().collection("Groups").document(CreateGroupActivity.newGroupName).update("size",AddMembersAdapter.currGroup.members.size());
                    Toast.makeText(AddMembersAdapter.this.context, ("Removed " + selectedUser.username +" from your group"), Toast.LENGTH_SHORT).show();

                }


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


    public static class AddMembersHolder extends RecyclerView.ViewHolder {
        ItemSearchUserBinding binding;
        TextView userName;
        TextView realName;
        ImageView profilePicture;
        public AddMembersHolder(ItemSearchUserBinding binding){
            super(binding.getRoot());
            this.binding = binding;
            userName = this.binding.tvTitle;
            realName = this.binding.tvDescription;
            profilePicture = this.binding.sivAvatar;
        }

    }
}