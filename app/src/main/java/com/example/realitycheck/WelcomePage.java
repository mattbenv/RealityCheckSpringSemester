package com.example.realitycheck;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.realitycheck.databinding.WelcomeBinding;

public class WelcomePage extends Fragment {
    Animation rotateAnimation;
    ImageView imageView;

    private void rotateAnimation(){
        rotateAnimation= AnimationUtils.loadAnimation(getContext(),R.anim.rotate);
        imageView.startAnimation(rotateAnimation);
    }


    private WelcomeBinding binding; // binds the welcome page class with the welcome page xml file (layout)

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = WelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //working on animating the rotating image reality check logo, and zoom in and out on other images
        //ImageView itemImageView = view.findViewById(R.id.realityCheckLogo);
        //ViewCompat.setTransitionName(itemImageView,"fadeout");
        imageView=(ImageView) view.findViewById(R.id.realityCheckLogo);
        rotateAnimation();
        //button that navigates to the sign up page
        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(WelcomePage.this)
                        .navigate(R.id.action_WelcomePage_to_SignUpPage);
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