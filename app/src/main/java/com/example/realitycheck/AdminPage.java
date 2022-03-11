package com.example.realitycheck;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.realitycheck.databinding.AdminPageBinding;
import com.example.realitycheck.databinding.WelcomeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminPage extends Fragment {

    private AdminPageBinding binding; // binds the welcome page class with the welcome page xml file (layout)
    View view = getView(); //This variable allows for you to collect an id from the fragment page.
    public AdminPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =AdminPageBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //button that navigates to the sign up page
        binding.informationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AdminPage.this)
                        .navigate(R.id.action_adminPage_to_userList2); //STILL NEED TO CHANGE THIS TO THE USER LIST
            }
        });

    }


    //every time we leave the page, it destroys the page
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}