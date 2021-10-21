
package com.example.realitycheck;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.realitycheck.databinding.SignupcontinuedBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class SignUpPageContinued extends Fragment {

    private SignupcontinuedBinding binding;
    private String emailValue;
    private String usernameValue;
    private String nameValue;
    private String bioValue;
    private String birthdateValue;
    private String profileImagePath;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = SignupcontinuedBinding.inflate(inflater, container, false);

        FloatingActionButton aFab = container.getRootView().findViewById(R.id.fab);
        aFab.hide();

        return binding.getRoot();


    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);


        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailValue = SignUpPage.email.getText().toString().trim();
                usernameValue = SignUpPage.username.getText().toString().trim();
                nameValue = binding.name.getText().toString().trim();
                bioValue = binding.UserBio.getText().toString().trim();
                birthdateValue = binding.dateOfBirth.getText().toString().trim();

                // will use buttons to get photo input and save image path for now is left empty
                profileImagePath = "empty profile image path";

                User user = new User(emailValue,usernameValue,nameValue,bioValue,birthdateValue,profileImagePath,new ArrayList<Post>(),new ArrayList<User>() , new ArrayList<User>(),new ArrayList<User>());
                FirebaseDatabase.getInstance().getReference()
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Profile setup is completed!")
                                .setMessage("Please login to your account")
                                .setCancelable(false)
                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        NavHostFragment.findNavController(SignUpPageContinued.this)
                                                .navigate(R.id.action_SignUpPageContinued_to_LogInPage);
                                    }
                                }).show();
                    }
                });
                }
            });
        }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
