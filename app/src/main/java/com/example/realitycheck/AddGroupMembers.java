package com.example.realitycheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realitycheck.adapter.AddMembersAdapter;
import com.example.realitycheck.adapter.SearchAdapter;
import com.example.realitycheck.databinding.ActivitySearchBinding;
import com.example.realitycheck.databinding.AddGroupMembersBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddGroupMembers extends Fragment {
    private AddGroupMembersBinding binding;
    public static AddMembersAdapter addMemberAdapter;
    private RecyclerView recyclerView;
    private DocumentReference usersDatabase;
    CollectionReference database;
    ArrayList<User> list;


    //creates the simple search view page so user can search
    //for members to add
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  AddGroupMembersBinding.inflate(inflater, container, false);
        SearchView simpleSearchView =  binding.getRoot().findViewById(R.id.simpleSearchView);
        //initializes recycler view to include search box
        recyclerView = binding.getRoot().findViewById(R.id.rl_search_box);
        //searchAdapter = new SearchAdapter(this.getContext());




        //System.out.println(binding.toptab.getTabAt(1));


        // binding.toptab.getSelectedTabPosition()

        //recyclerView.setAdapter(searchAdapter);

        //adjusts layout of recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));

        //get instance of Firebase collection of users
        database = FirebaseFirestore.getInstance().collection("Users");
        list = new ArrayList<User>();

        addMemberAdapter = new AddMembersAdapter(this.getContext(),list);
        recyclerView.setAdapter(addMemberAdapter);

        binding.CreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AddGroupMembers.this)
                        .navigate(R.id.action_AddGroupMembers_to_ProfileActivity);
            }
        });




        database.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot val : queryDocumentSnapshots.getDocuments()){

                    User selectedUser = new User();
                    selectedUser.email = val.get("email").toString();
                    selectedUser.username = val.get("username").toString();
                    selectedUser.name = val.get("name").toString();
                    selectedUser.bio = val.get("bio").toString();
                    selectedUser.birthday = val.get("birthday").toString();
                    if(!(val.get("profileImagePath") == null)) {
                        selectedUser.profileImagePath = val.get("profileImagePath").toString();
                    }
                    selectedUser.posts = (ArrayList<String>)  val.get("posts");
                    selectedUser.following = (ArrayList<String>) val.get("following");
                    selectedUser.followers = (ArrayList<String>) val.get("followers");
                    selectedUser.friends  = (ArrayList<String>) val.get("friends");
                    selectedUser.privateMode = (Boolean) val.get("private");
                    selectedUser.taggedIn = (ArrayList<String>) val.get("taggedIn");
                    selectedUser.reposted = (ArrayList<String>) val.get("reposted");
                    //User user = val.toObject(User.class);
                    list.add(selectedUser);


                }
                addMemberAdapter.notifyDataSetChanged();

            }
        });

        //The bar where you can
        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                addMemberAdapter.getFilter().filter(newText);
                return false;
            }
        });
        simpleSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                database.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot val : queryDocumentSnapshots.getDocuments()){

                            User selectedUser = new User();
                            selectedUser.email = val.get("email").toString();
                            selectedUser.username = val.get("username").toString();
                            selectedUser.name = val.get("name").toString();
                            selectedUser.bio = val.get("bio").toString();
                            selectedUser.birthday = val.get("birthday").toString();
                            if(!(val.get("profileImagePath") == null)) {
                                selectedUser.profileImagePath = val.get("profileImagePath").toString();
                            }
                            selectedUser.posts = (ArrayList<String>)  val.get("posts");
                            selectedUser.following = (ArrayList<String>) val.get("following");
                            selectedUser.followers = (ArrayList<String>) val.get("followers");
                            selectedUser.friends  = (ArrayList<String>) val.get("friends");
                            selectedUser.privateMode = (Boolean) val.get("private");
                            //User user = val.toObject(User.class);
                            list.add(selectedUser);


                        }
                       addMemberAdapter.notifyDataSetChanged();

                    }
                });
                return false;
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