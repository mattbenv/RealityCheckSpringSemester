package com.example.realitycheck.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realitycheck.User;
import java.util.ArrayList;
public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.AddUserHolder>{

    Context context;
    ArrayList<User> user_list;

    //Constructor for this class has been set
    public AdminUserAdapter(Context context, ArrayList<User> user_list) {
        this.context = context;
        this.user_list = user_list;
    }

    //Implement All of the methods
    @NonNull
    @Override
    public AddUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new AddUserHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AddUserHolder holder, int position) {
        User user = user_list.get(position);
        holder.information1.setText(user.getusername()); //We have not made any getters or setters for the user class
        holder.extended_information.setText(user.get()); //We have not made any getters or setters for the user class
    }

    @Override
    public int getItemCount() {
        return user_list.size();
    }


    //The inner class has been created
    public static class AddUserHolder extends RecyclerView.ViewHolder{
        //We will define some textviews that we want to be displayed on the AdminUserList Page
        TextView username, information1, extended_information;

        public AddUserHolder(@NonNull View itemView) {
            super(itemView);

            //You can find the R.id for the Text you want in the item_admin_card.xml file
            username = itemView.findViewById(R.tv_username);
            information1 = itemView.findViewById(R.tv_info);
            extended_information = itemView.findViewById(R.tv_additional_info);

        }
    }
}
