

package com.example.realitycheck;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realitycheck.adapter.FollowerAdapter;
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
    public static FollowerAdapter followerAdapter;
    ArrayList<User> list;
    public static String previousActivity;
    public static boolean currUserProfile;
    public static User userToUse;
    public static User previousUser;


    //set to true means view followers false means view following
    public static boolean followersViewType;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  ActivityFollowersViewBinding.inflate(inflater, container, false);


        recyclerView = binding.rlSearchBox;

        list = new ArrayList<User>();

        followerAdapter = new FollowerAdapter(this.getContext(),list);
        setUserList(followersViewType,binding);


        return binding.getRoot();
    }

    public void setUserList(Boolean type,ActivityFollowersViewBinding binding){
        if(currUserProfile == true){
            userToUse = LoginPage.currUser;
        }
        if(currUserProfile == false){
            //this is what causes the issue with going back
            //the selected is getting updated to the last user to appear int he follower view
            //could create own adapt for followers and then use Followadapter.selecteduser
            //userToUse = SearchAdapter.selectedUser;
            if(previousActivity == "otherprofile"){
                userToUse = otherUserProfileActivity.thisUser;
            }
            else {
                userToUse = FollowerAdapter.selectedUser;
            }
        }

        if(type== true) {
            binding.title.setText("Followers");
            if (userToUse.followers != null) {
                for (int i = 0; i < userToUse.followers.size(); i++) {
                    database = FirebaseFirestore.getInstance().collection("Users").whereEqualTo("username", userToUse.followers.get(i));
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
                                selectedUser.taggedIn = (ArrayList<String>) val.get("taggedIn");
                                selectedUser.reposted = (ArrayList<String>) val.get("reposted");
                                //User user = val.toObject(User.class);
                                list.add(selectedUser);


                            }
                            followerAdapter.notifyDataSetChanged();

                        }
                    });
                }
            }
        }
        if(type== false) {
            binding.title.setText("Following");
            if (userToUse.following.size() > 0) {
                for (int i = 0; i < userToUse.following.size(); i++) {
                    database = FirebaseFirestore.getInstance().collection("Users").whereEqualTo("username",userToUse.following.get(i));
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
                                selectedUser.taggedIn = (ArrayList<String>) val.get("taggedIn");
                                selectedUser.reposted = (ArrayList<String>) val.get("reposted");
                                //User user = val.toObject(User.class);
                                list.add(selectedUser);


                            }
                            followerAdapter.notifyDataSetChanged();

                        }
                    });
                }
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(followerAdapter);



    }




    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        previousUser = userToUse;
        binding = null;
    }
}