package com.example.realitycheck.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realitycheck.Group;
import com.example.realitycheck.LoginPage;
import com.example.realitycheck.R;
import com.example.realitycheck.User;
import com.example.realitycheck.ViewPostActivity;
import com.example.realitycheck.databinding.ItemGroupBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> implements Filterable {

    private Context context;

    public Uri postAuthorProfilePhoto;

    public ArrayList<Group> groupList;
    public static Group group;
    public String author;
    public String content;
    public static String thisPage;
    public String date;
    public FirebaseFirestore fStore;

    public static User userToNavTo;


    public GroupAdapter(Context context, ArrayList<Group> group) {
        this.context = context;
        this.groupList = group;
    }

    @NonNull
    @Override
    public GroupAdapter.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroupBinding binding = ItemGroupBinding.inflate(LayoutInflater.from(context), parent, false);
        GroupAdapter.GroupViewHolder groupViewHolder = new GroupAdapter.GroupViewHolder(binding);
        return groupViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.GroupViewHolder holder, int position) {

        //sorts list of post based on post date
        //need to convert String postDate to dateTime in order to compare accurately
        Collections.sort(groupList, new Comparator<Group>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public int compare(Group group, Group t1) {
                return(group.groupName.compareTo(t1.groupName));

            }
        });
        //sets in order of most recent
        Collections.reverse(groupList);

        //sets up filter for filtering lists that gets displayed
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Group> resultList = new ArrayList<>();
                if (!charSequence.toString().isEmpty()) {
                    for (Group group : groupList) {
                        if (group.groupName.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            resultList.add(group);
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
                groupList.clear();
                groupList.addAll((Collection<? extends Group>) filterResults.values);
                notifyDataSetChanged();

            }
        };



        //on clicking the posts it moves to a more in depth view of just that post
        holder.binding.groupitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                group = groupList.get(position);
                Navigation.createNavigateOnClickListener(R.id.to_ViewGroupActivity).onClick(holder.binding.groupitem);
            }
        });




        //loads group photo if exists if not it hides the imageview
        group = groupList.get(position);
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Groups").document(group.groupName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> userMap = documentSnapshot.getData();
                if(userMap.get("profileImagePath")!= null){
                    String profileImagePath = userMap.get("profileImagePath").toString();
                    holder.binding.sivAvatar.setVisibility(View.VISIBLE);
                    holder.binding.avatarCard.setVisibility(View.VISIBLE);
                    // Reference to an image file in Cloud Storage
                    FirebaseStorage.getInstance().getReference().child("images/"+profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ImageView imageView = holder.binding.sivAvatar;
                            Glide.with(context)
                                    .load(uri)
                                    .into(imageView);
                        }
                    });
                }
                if(userMap.get("profileImagePath")== null){
                    holder.binding.sivAvatar.setVisibility(View.GONE);
                    holder.binding.avatarCard.setVisibility(View.GONE);
                }
            }
        });

        //gets current post from the recycler view list based its position
        group = groupList.get(position);
        holder.binding.tvTitle.setText(group.groupName);
        holder.binding.tvDescription.setText(group.bio);
        holder.binding.ivMore.setText(group.size + " members");
        if(group.members.contains(LoginPage.currUser.username)){
            holder.binding.buttonJoin.setText("Leave");
        }
        if(!group.members.contains(LoginPage.currUser.username)){
            holder.binding.buttonJoin.setText("Join");


        }
        if(group.members.contains(LoginPage.currUser.username)){
            holder.binding.buttonJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    group = groupList.get(position);
                    new AlertDialog.Builder(context)
                            .setTitle("Leave Group")
                            .setMessage("Are you sure you want to leave the group: "+ group.groupName+"?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    group.members.remove(LoginPage.currUser.username);
                                    group.size = group.members.size();
                                    holder.binding.ivMore.setText(group.size + " members");
                                    holder.binding.buttonJoin.setText("Join");
                                    Toast.makeText(GroupAdapter.this.context, ("Left " + group.groupName), Toast.LENGTH_SHORT).show();
                                    FirebaseFirestore.getInstance().collection("Groups").document(group.groupName).update("members", group.members);
                                    FirebaseFirestore.getInstance().collection("Groups").document(group.groupName).update("size", group.size);

                                    if(thisPage == "profile"){
                                        groupList.remove(group);
                                    }
                                    notifyDataSetChanged();
                                }
                            }).setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
        }
        else if(!group.members.contains(LoginPage.currUser.username)){
            holder.binding.buttonJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    group = groupList.get(position);
                    group.members.add(LoginPage.currUser.username);
                    group.size = group.members.size();
                    holder.binding.ivMore.setText(group.size + " members");
                    holder.binding.buttonJoin.setText("Leave");
                    Toast.makeText(GroupAdapter.this.context, ("Joined" + group.groupName), Toast.LENGTH_SHORT).show();
                    FirebaseFirestore.getInstance().collection("Groups").document(group.groupName).update("members", group.members);
                    FirebaseFirestore.getInstance().collection("Groups").document(group.groupName).update("size", group.size);
                    notifyDataSetChanged();
                }
            });

        }




    }





    //adds new comment
    public void addData(Group newItem) {
        groupList.add(0,newItem);
        notifyItemInserted(0);
    }







    @Override
    public int getItemCount() {
        return groupList.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }


    class GroupViewHolder extends RecyclerView.ViewHolder {
        ItemGroupBinding binding;



        public GroupViewHolder (ItemGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }
    }
}