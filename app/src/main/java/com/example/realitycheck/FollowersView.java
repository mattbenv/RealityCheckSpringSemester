

package com.example.realitycheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realitycheck.adapter.SearchAdapter;
import com.example.realitycheck.databinding.ActivityFollowersViewBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class FollowersView extends Fragment {
    private ActivityFollowersViewBinding binding;
    private RecyclerView recyclerView;
    Query database;
    public static SearchAdapter searchAdapter;
    ArrayList<User> list;

    //set to true means view followers false means view following
    public static boolean followersViewType;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  ActivityFollowersViewBinding.inflate(inflater, container, false);


        recyclerView = binding.rlSearchBox;

        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FollowersView.this)
                        .navigate(R.id.action_ViewFollowersActivity_to_ProfileActivity);
            }
        });

        list = new ArrayList<User>();
        searchAdapter = new SearchAdapter(this.getContext(),list);
        setUserList(followersViewType);

        return binding.getRoot();
    }

    public void setUserList(Boolean type){
        if(type== true) {
            binding.title.setText("Followers");
            if (LoginPage.currUser.followers.size() > 0) {
                for (int i = 0; i < LoginPage.currUser.followers.size(); i++) {
                    database = FirebaseFirestore.getInstance().collection("Users").whereEqualTo("username", LoginPage.currUser.followers.get(i));
                    database.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot val : queryDocumentSnapshots.getDocuments()) {

                                User selectedUser = new User();
                                selectedUser.email = val.get("email").toString();
                                selectedUser.username = val.get("username").toString();
                                selectedUser.name = val.get("name").toString();
                                selectedUser.bio = val.get("bio").toString();
                                selectedUser.birthday = val.get("birthday").toString();
                                if (!(val.get("profileImagePath") == null)) {
                                    selectedUser.profileImagePath = val.get("profileImagePath").toString();
                                }
                                selectedUser.posts = (ArrayList<String>) val.get("posts");
                                selectedUser.following = (ArrayList<String>) val.get("following");
                                selectedUser.followers = (ArrayList<String>) val.get("followers");
                                selectedUser.friends = (ArrayList<String>) val.get("friends");
                                //User user = val.toObject(User.class);
                                list.add(selectedUser);


                            }
                            searchAdapter.notifyDataSetChanged();

                        }
                    });
                }
            }
        }
        if(type== false) {
            binding.title.setText("Following");
            if (LoginPage.currUser.following.size() > 0) {
                for (int i = 0; i < LoginPage.currUser.following.size(); i++) {
                    database = FirebaseFirestore.getInstance().collection("Users").whereEqualTo("username", LoginPage.currUser.following.get(i));
                    database.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot val : queryDocumentSnapshots.getDocuments()) {

                                User selectedUser = new User();
                                selectedUser.email = val.get("email").toString();
                                selectedUser.username = val.get("username").toString();
                                selectedUser.name = val.get("name").toString();
                                selectedUser.bio = val.get("bio").toString();
                                selectedUser.birthday = val.get("birthday").toString();
                                if (!(val.get("profileImagePath") == null)) {
                                    selectedUser.profileImagePath = val.get("profileImagePath").toString();
                                }
                                selectedUser.posts = (ArrayList<String>) val.get("posts");
                                selectedUser.following = (ArrayList<String>) val.get("following");
                                selectedUser.followers = (ArrayList<String>) val.get("followers");
                                selectedUser.friends = (ArrayList<String>) val.get("friends");
                                //User user = val.toObject(User.class);
                                list.add(selectedUser);


                            }
                            searchAdapter.notifyDataSetChanged();

                        }
                    });
                }
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(searchAdapter);

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