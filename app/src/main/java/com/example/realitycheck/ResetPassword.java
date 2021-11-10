
package com.example.realitycheck;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.realitycheck.databinding.ActivityResetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPassword extends Fragment {
    private ActivityResetPasswordBinding binding;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);




    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding  =  ActivityResetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Matcher m = VALID_EMAIL_ADDRESS_REGEX.matcher(binding.username.getText());
                if(m.find()==false) {
                    binding.username.setError("Invalid email address");
                    binding.username.requestFocus();

                }
                else{
                    resetPassword();
                    //todo: somewhere in here I would like to implement the animation used for lock button to load:
                   /* LottieAnimationView animationView  = binding.lock;
                    animationView
                            .addAnimatorUpdateListener(
                                    (animation) -> {
                                        // Do something.
                                    });
                    animationView
                            .playAnimation();

                    if (animationView.isAnimating()) {
                        // Do something.
                        resetPassword();
                    }*/
                }
            }
        });


    }
    public void resetPassword(){
        FirebaseAuth.getInstance().sendPasswordResetEmail(binding.username.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Check email for new password\n", Toast.LENGTH_LONG).show();
                            NavHostFragment.findNavController(ResetPassword.this)
                                    .navigate(R.id.action_ResetPasswordActivity_to_LoginPage);
                        }
                        if (!task.isSuccessful()) {
                            Toast.makeText(getContext(), "Reset password failed\n", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
