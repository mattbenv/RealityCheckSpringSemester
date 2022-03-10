package com.example.realitycheck;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.realitycheck.databinding.WelcomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Map;

public class WelcomePage extends Fragment {
    //List of Animations objects used whenever you want to add a new animation
    Animation rotateAnimation;
    Animation slideinAnimation ;
    Animation fadeinAnimation;
    Animation slideupAnimation;

    private WelcomeBinding binding; // binds the welcome page class with the welcome page xml file (layout)
    View view = getView(); //This variable allows for you to collect an id from the fragment page.
    ImageView realitychecklogo;
    ImageView fakenews;
    ImageView computerwebpic;


    //List of functions used to animate this fragment
    private void slideupAnimation(){
        slideupAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.slideup);
        binding.buttonSignup.startAnimation(slideupAnimation);
        binding.buttonSignin.startAnimation(slideupAnimation);
    }

    private void rotateAnimation(){
        rotateAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.rotate);
        realitychecklogo.startAnimation(rotateAnimation);
    }

    public void fadeinAnimation(){
        fadeinAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.fadein);
        getView().findViewById(R.id.slogin).startAnimation(fadeinAnimation);
    }

    public void translateAnimation(){
        slideinAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in);
        fakenews.startAnimation(slideinAnimation);
        computerwebpic.startAnimation(slideinAnimation);
        realitychecklogo.startAnimation(slideinAnimation);
        getView().findViewById(R.id.slogin).startAnimation(slideinAnimation);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        //This allows to bypass the Login process if a user has already been signed in
        //basically saves the logged in user when app is closed
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(user.getDisplayName());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot value) {
                        Map<String, Object> userMap = value.getData();
                        String uid = userMap.get("uid").toString();
                        String email = userMap.get("email").toString();
                        String username = userMap.get("username").toString();
                        String name = userMap.get("name").toString();
                        String bio = userMap.get("bio").toString();
                        String birthday = userMap.get("birthday").toString();
                        String profileImagePath = userMap.get("profileImagePath").toString();
                        ArrayList<String> posts = (ArrayList<String>) userMap.get("posts");
                        ArrayList<String> followers = (ArrayList<String>) userMap.get("followers");
                        ArrayList<String> following = (ArrayList<String>) userMap.get("following");
                        ArrayList<String> friends = (ArrayList<String>) userMap.get("friends");
                        Boolean privateMode = (Boolean) userMap.get("private");
                        Boolean notificationsEnabled = (Boolean) userMap.get("notificationsEnabled");

                        ArrayList<String> taggedIn = (ArrayList<String>) userMap.get("taggedIn");
                        ArrayList<String> reposted = (ArrayList<String>) userMap.get("reposted");
                        LoginPage.currUser = new User(uid, email, username, name, bio, birthday, profileImagePath, posts, followers, following, friends, privateMode, notificationsEnabled, taggedIn, reposted);

                        // Reference to an image file in Cloud Storage
                        FirebaseStorage.getInstance().getReference().child("images/" + profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                LoginPage.storageProfilePictureReference = uri;
                            }
                        });

                        NavHostFragment.findNavController(WelcomePage.this)
                                .navigate(R.id.action_to_PostActivity);

                    }

                });

        }
        binding = WelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //List of images collected from the xml and stored in the respected variables.
        fakenews = (ImageView) view.findViewById(R.id.fakenews);
        realitychecklogo=(ImageView) view.findViewById(R.id.realityCheckLogo);
        computerwebpic = (ImageView) view.findViewById(R.id.computerweb);
        //binding.buttonSignin.animate().translationY(0).translationX(0).alpha(1).setDuration(1000).setStartDelay(1600).start();
        //binding.buttonSignup.animate().translationY(0).translationX(0).alpha(1).setDuration(1000).setStartDelay(1600).start();
        translateAnimation();
        //rotateAnimation();
        fadeinAnimation();
        slideupAnimation();
        getView().findViewById(R.id.slogin).animate().translationY(0).translationX(10000).alpha(1).setDuration(10000).setStartDelay(2000).start();
        //Animation shifts for when the page is opened and a rotate animation for the welcomepage

        //realitychecklogo.animate().translationY(0).translationX(10).alpha(1).setDuration(1000).setStartDelay(400).start();
        //fakenews.animate().translationY(0).translationX(10).alpha(1).setDuration(1000).setStartDelay(600).start();
        //computerwebpic.animate().translationY(0).translationX(10).alpha(1).setDuration(1000).setStartDelay(600).start();

        //button that navigates to the sign up page
        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(WelcomePage.this)
                        .navigate(R.id.action_WelcomePage_to_SignUpPage);
            }
        });

        //Button that navigates to the administrator page
        binding.adminBotton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(WelcomePage.this)
                        .navigate(R.id.action_LogInPage_to_AdminPage);
            }
        });
        //button that navigates to login page
        binding.buttonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(WelcomePage.this).navigate(R.id.action_WelcomePage_to_LogInPage);
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