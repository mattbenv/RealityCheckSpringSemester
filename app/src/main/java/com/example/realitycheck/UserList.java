package com.example.realitycheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.realitycheck.adapter.AdminUserAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserList extends AppCompatActivity {
    RecyclerView recyclerView;
    CollectionReference db;
    public static AdminUserAdapter adminUserAdapter;
    ArrayList<User> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        recyclerView = findViewById(R.id.Userlist_id);



        //There might be a potential error since we switched over to using the firestore instead of the firebase
        db = FirebaseFirestore.getInstance().collection("Users");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        list = new ArrayList<>();
        adminUserAdapter = new AdminUserAdapter(this,list);
        recyclerView.setAdapter(adminUserAdapter);
        db.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                adminUserAdapter.notifyDataSetChanged();

            }
        });
        /*db.addSnapshotListener(new ValueEventListener(){
            @Override
            public void on
        });*/

        /*db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                }
                adminUserAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }
}