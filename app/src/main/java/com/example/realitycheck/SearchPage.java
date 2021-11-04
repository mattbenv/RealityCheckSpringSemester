

package com.example.realitycheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.realitycheck.adapter.SearchAdapter;
import com.example.realitycheck.databinding.ActivitySearchBinding;
import com.example.realitycheck.util.LinearLayoutDivider;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class SearchPage extends Fragment {
    private ActivitySearchBinding binding;
    public static SearchAdapter searchAdapter;
    private RecyclerView recyclerView;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  ActivitySearchBinding.inflate(inflater, container, false);
        SearchView simpleSearchView =  binding.getRoot().findViewById(R.id.simpleSearchView);


        recyclerView = binding.getRoot().findViewById(R.id.rl_search_box);
        searchAdapter = new SearchAdapter(this.getContext());
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LinearLayoutDivider(this.getContext(), LinearLayoutManager.VERTICAL));

        simpleSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            //list isn't being reset when search box is closed
            public boolean onClose() {
                searchAdapter.userList = searchAdapter.getUserList();
                searchAdapter.notifyDataSetChanged();
                return false;

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


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}