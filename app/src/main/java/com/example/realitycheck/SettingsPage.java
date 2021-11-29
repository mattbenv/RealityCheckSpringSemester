package com.example.realitycheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.realitycheck.adapter.SearchAdapter;
import com.example.realitycheck.databinding.SettingsPageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsPage extends Fragment {
    private SettingsPageBinding binding;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  SettingsPageBinding.inflate(inflater, container, false);

        //creates dialog to confirm users wants to logout and logs the user out if they click yes
        binding.logoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to log out of your account?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(getContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                                NavHostFragment.findNavController(SettingsPage.this)
                                        .navigate(R.id.action_SettingsPage_to_WelcomePage);
                            }
                        }).setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        binding.resetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SettingsPage.this)
                        .navigate(R.id.action_SettingsPage_to_ResetPasswordActivity);
            }
        });

        binding.EditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make a new layout to navigate to to edit profile
                Toast.makeText(getContext(), "Edit Profile page to be implemented", Toast.LENGTH_SHORT).show();
            }
        });



        //sets the private mode switch to the correct value
        if(LoginPage.currUser.privateMode == true){
            binding.switch1.setChecked(true);
            binding.switch1.setText("Private Mode On");
        }
        else if(LoginPage.currUser.privateMode == false){
            binding.switch1.setChecked(false);
            binding.switch1.setText("Private Mode Off");
        }
        //switches the user between private mode on and off
        binding.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext(), "Private mode enabled", Toast.LENGTH_SHORT).show();
                    LoginPage.currUser.privateMode = true;
                    binding.switch1.setText("Private Mode On");
                    FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username).update("private", LoginPage.currUser.privateMode);
                } else {
                    Toast.makeText(getContext(), "Private mode disabled. Profile is now public.", Toast.LENGTH_SHORT).show();
                    LoginPage.currUser.privateMode = false;
                    binding.switch1.setText("Private Mode Off");
                    FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username).update("private", LoginPage.currUser.privateMode);

                }
            }
        });
        //sets the notifications switch to the correct value
        if(LoginPage.currUser.notificationsEnabled == true){
            binding.switchNotification.setChecked(true);
        }
        else if(LoginPage.currUser.notificationsEnabled == false){
            binding.switchNotification.setChecked(false);
        }
        //switches the users notifications between on and off
        binding.switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext(), "Notifications Enabled", Toast.LENGTH_SHORT).show();
                    LoginPage.currUser.notificationsEnabled =true;
                    FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username).update("notificationsEnabled", LoginPage.currUser.notificationsEnabled);
                } else {
                    Toast.makeText(getContext(), "Notifications Disabled", Toast.LENGTH_SHORT).show();
                    LoginPage.currUser.notificationsEnabled =false;
                    FirebaseFirestore.getInstance().collection("Users").document(LoginPage.currUser.username).update("notificationsEnabled", LoginPage.currUser.notificationsEnabled);

                }
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
