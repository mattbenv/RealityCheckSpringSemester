

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

import com.example.realitycheck.adapter.GroupAdapter;
import com.example.realitycheck.adapter.SearchAdapter;
import com.example.realitycheck.databinding.ActivitySearchBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class SearchPage extends Fragment {
    private ActivitySearchBinding binding;
    public static SearchAdapter searchAdapter;
    public static GroupAdapter groupAdapter;
    private RecyclerView recyclerView;
    public static TabLayout topTab;
    private DocumentReference usersDatabase;
    CollectionReference database;
    ArrayList<User> list;
    public static String previous;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  ActivitySearchBinding.inflate(inflater, container, false);
        SearchView simpleSearchView =  binding.getRoot().findViewById(R.id.simpleSearchView);
        GroupAdapter.thisPage = "search";

        bottomNavigate(binding);

        recyclerView = binding.getRoot().findViewById(R.id.rl_search_box);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));

        if(previous == "profile"){
            binding.toptab.getTabAt(1).select();
            previous = "";
        }

        binding.toptab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(binding.toptab.getTabAt(1).isSelected()) {
                    binding.updateMessage.setVisibility(View.GONE);
                    binding.rlSearchBox.setVisibility(View.VISIBLE);
                    database = FirebaseFirestore.getInstance().collection("Groups");
                    ArrayList<Group> groupList = new ArrayList<Group>();
                    groupAdapter = new GroupAdapter(getContext(),groupList);
                    recyclerView.setAdapter(groupAdapter);
                    database.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            for(DocumentSnapshot documentSnapshot:documentSnapshots){
                                Group group = new Group();
                                group.groupName = (String) documentSnapshot.get("groupName");
                                group.bio = (String) documentSnapshot.get("bio");
                                group.members = (ArrayList<String>)documentSnapshot.get("members");
                                group.size = group.members.size();
                                group.privacy = (boolean) documentSnapshot.get("privacy");
                                group.profileImagePath = (String) documentSnapshot.get("profileImagePath");
                                group.posts = (ArrayList<String>)documentSnapshot.get("posts");
                                groupList.add(0,group);
                            }
                            groupAdapter.notifyDataSetChanged();
                        }
                    });
                }

                if(binding.toptab.getTabAt(0).isSelected()){
                    binding.updateMessage.setVisibility(View.GONE);
                    binding.rlSearchBox.setVisibility(View.VISIBLE);
                    database = FirebaseFirestore.getInstance().collection("Users");
                    list = new ArrayList<User>();
                    searchAdapter = new SearchAdapter(getContext(),list);
                    recyclerView.setAdapter(searchAdapter);

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
                            searchAdapter.notifyDataSetChanged();

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
                            searchAdapter.getFilter().filter(newText);
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
                                    searchAdapter.notifyDataSetChanged();

                                }
                            });
                            return false;
                        }
                    });
                }
                if(binding.toptab.getTabAt(2).isSelected()){
                    recyclerView.setVisibility(View.GONE);
                    binding.updateMessage.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if(binding.toptab.getTabAt(1).isSelected()) {
            binding.updateMessage.setVisibility(View.GONE);
            binding.rlSearchBox.setVisibility(View.VISIBLE);
            database = FirebaseFirestore.getInstance().collection("Groups");
            ArrayList<Group> groupList = new ArrayList<Group>();
            groupAdapter = new GroupAdapter(this.getContext(),groupList);
            recyclerView.setAdapter(groupAdapter);
            database.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    for(DocumentSnapshot documentSnapshot:documentSnapshots){
                        Group group = new Group();
                        group.groupName = (String) documentSnapshot.get("groupName");
                        group.bio = (String) documentSnapshot.get("bio");
                        group.members = (ArrayList<String>)documentSnapshot.get("members");
                        group.size = group.members.size();
                        group.privacy = (boolean) documentSnapshot.get("privacy");
                        group.profileImagePath = (String) documentSnapshot.get("profileImagePath");
                        group.posts = (ArrayList<String>)documentSnapshot.get("posts");
                        groupList.add(0,group);
                    }
                    groupAdapter.notifyDataSetChanged();
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
                    groupAdapter.getFilter().filter(newText);
                    return false;
                }
            });
            simpleSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    database.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            for(DocumentSnapshot documentSnapshot:documentSnapshots){
                                Group group = new Group();
                                group.groupName = (String) documentSnapshot.get("groupName");
                                group.bio = (String) documentSnapshot.get("bio");
                                group.members = (ArrayList<String>)documentSnapshot.get("members");
                                group.size = group.members.size();
                                group.privacy = (boolean) documentSnapshot.get("privacy");
                                group.profileImagePath = (String) documentSnapshot.get("profileImagePath");
                                group.posts = (ArrayList<String>)documentSnapshot.get("posts");
                                groupList.add(0,group);
                            }
                            groupAdapter.notifyDataSetChanged();
                        }
                    });
                    return false;
                }
            });
        }

        if(binding.toptab.getTabAt(0).isSelected()){
            database = FirebaseFirestore.getInstance().collection("Users");
            list = new ArrayList<User>();
            searchAdapter = new SearchAdapter(this.getContext(),list);
            recyclerView.setAdapter(searchAdapter);

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
                    searchAdapter.notifyDataSetChanged();

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
                    searchAdapter.getFilter().filter(newText);
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
                            searchAdapter.notifyDataSetChanged();

                        }
                    });
                    return false;
                }
            });
        }











        return binding.getRoot();
    }


    public void bottomNavigate(ActivitySearchBinding binding){
        BottomNavigationView bottomNavigationView = binding.getRoot().findViewById(R.id.bnav_post_bottom);
        bottomNavigationView.getMenu().findItem(R.id.post_search).setChecked(true);
        //need to fix to recognize what screen
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.post_search:
                        //
                        break;
                    case R.id.post_home:
                        NavHostFragment.findNavController(SearchPage.this)
                                .navigate(R.id.action_SearchPage_to_PostActivity);
                        break;
                    case R.id.post_notification:
                        //
                        break;
                    case R.id.post_message:
                        //
                        break;
                }
                return true;
            }
        });
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