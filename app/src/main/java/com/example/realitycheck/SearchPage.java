

package com.example.realitycheck;

import android.net.Uri;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

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




        //System.out.println(binding.toptab.getTabAt(1));

        
        System.out.println(binding.toptab.getSelectedTabPosition());
       // binding.toptab.getSelectedTabPosition()

        //recyclerView.setAdapter(searchAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));

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
                    //User user = val.toObject(User.class);
                    list.add(selectedUser);


                }
                searchAdapter.notifyDataSetChanged();

            }
        });
        /*ArrayList<String> sortUsernamesOrder = new ArrayList<>();
        for(User user:list){
            sortUsernamesOrder.add(user.username);
        }
        //sortStrings(sortUsernamesOrder,sortUsernamesOrder);


         */

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



        return binding.getRoot();
    }


   /* public static void sortStrings(Arra arr, int n)
    {
        String storage;

        // Sorting strings using bubble sort
        for (int j = 0; j < n - 1; j++)
        {
            for (int i = j + 1; i < n; i++)
            {
                if (arr[j].compareTo(arr[i]) > 0)
                {
                    storage = arr[j];
                    arr[j] = arr[i];
                    arr[i] = storage;
                }
            }
        }
    }

    */


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