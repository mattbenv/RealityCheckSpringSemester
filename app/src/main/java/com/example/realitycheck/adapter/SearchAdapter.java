

package com.example.realitycheck.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realitycheck.LoginPage;
import com.example.realitycheck.PostActivity;
import com.example.realitycheck.R;
import com.example.realitycheck.SearchPage;
import com.example.realitycheck.bean.PostBean;
import com.example.realitycheck.databinding.ItemSearchUserBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
//todo: understand how homepage works which is related to post adapter/viewholder/activity. if we can, design new one
public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> implements Filterable {

    private Context context;
    public List<String> userList;
    public List<String> allUsers;
    public List<String> names;
    public int a;

    public List<String> getUserList(){
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
    public SearchAdapter(Context context) {
        this.context = context;
        userList = getUserList();
        allUsers = getUserList();

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
        String user = userList.get(position);
        holder.binding.tvTitle.setText(user);
        String name = names.get(position);
        holder.binding.tvDescription.setText(name);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addData(String newItem) {
        userList.add(newItem);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    //sets up filter for filtering lists that gets displayed
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<String> resultList = new ArrayList<>();
            if(!charSequence.toString().isEmpty()){
                for(String user: allUsers){
                    if(user.toLowerCase().contains(charSequence.toString().toLowerCase())){
                        resultList.add(user);
                    }
                }
            }
            else{
                resultList.addAll(allUsers);
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = resultList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            userList.clear();
            userList.addAll((Collection<? extends String>) filterResults.values);
            notifyDataSetChanged();

        }
    };
}

class SearchViewHolder extends RecyclerView.ViewHolder {
    ItemSearchUserBinding binding;
    public SearchViewHolder(ItemSearchUserBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}