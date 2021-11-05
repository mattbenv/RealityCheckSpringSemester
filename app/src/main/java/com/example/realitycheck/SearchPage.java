

package com.example.realitycheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.realitycheck.adapter.SearchAdapter;
import com.example.realitycheck.databinding.ActivityPostBinding;
import com.example.realitycheck.databinding.ActivitySearchBinding;
import com.example.realitycheck.util.LinearLayoutDivider;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;


public class SearchPage extends Fragment {
    private ActivitySearchBinding binding;
    public static SearchAdapter searchAdapter;
    private RecyclerView recyclerView;
    private DocumentReference usersDatabase;
    CollectionReference database;
    ArrayList<User> list;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  ActivitySearchBinding.inflate(inflater, container, false);
        SearchView simpleSearchView =  binding.getRoot().findViewById(R.id.simpleSearchView);

        bottomNavigate(binding);

        recyclerView = binding.getRoot().findViewById(R.id.rl_search_box);
        //searchAdapter = new SearchAdapter(this.getContext());

        //recyclerView.setAdapter(searchAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));

        database = FirebaseFirestore.getInstance().collection("Users");
        list = new ArrayList<User>();
        searchAdapter = new SearchAdapter(this.getContext(),list);
        recyclerView.setAdapter(searchAdapter);


        database.addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot val : value.getDocuments()){

                    User user = new User();
                    user.username = val.get("username").toString();
                    user.name = val.get("name").toString();
                    if(!(val.get("profileImagePath") == null)) {
                        user.profileImagePath = val.get("profileImagePath").toString();
                    }
                    //User user = val.toObject(User.class);
                    list.add(user);


                }
                searchAdapter.notifyDataSetChanged();
            }
        });

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



        return binding.getRoot();
    }

    public void bottomNavigate(ActivitySearchBinding binding){
        BottomNavigationView bottomNavigationView = binding.getRoot().findViewById(R.id.bnav_post_bottom);
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