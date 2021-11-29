package com.example.realitycheck;

import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.realitycheck.databinding.LoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginPage extends Fragment {


    private LoginBinding binding;
    private FirebaseFirestore fStorage;
    private FirebaseStorage fImageStorage;
    private FirebaseAuth mAuth;
    public static User currUser;
    public static Uri storageProfilePictureReference;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    Animation slideinAnimation;
    Animation fadeinAnimation;
    Animation slideupAnimation;
    ImageView realitycheckbox;

    //List of functions of Animations
    private void slideupAnimation(){
        slideupAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.slideup);
        binding.login.startAnimation(slideupAnimation);

    }
    public void fadeinAnimation(){
        fadeinAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.fadein);
        getView().findViewById(R.id.resetPassword).startAnimation(fadeinAnimation);
    }
    public void translateAnimation(){
        slideinAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in);
        realitycheckbox.startAnimation(slideinAnimation);
        binding.resetPassword.startAnimation(slideinAnimation);
        binding.login.startAnimation(slideinAnimation);
        binding.showPassowrd.startAnimation(slideinAnimation);
        getView().findViewById(R.id.username).startAnimation(slideinAnimation);
        getView().findViewById(R.id.password).startAnimation(slideinAnimation);
        //binding.login.startAnimation(slideinAnimation);
        getView().findViewById(R.id.resetPassword).startAnimation(slideinAnimation);
        getView().findViewById(R.id.showPassowrd).startAnimation(slideinAnimation);
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        //connection to firebase authentication
        mAuth = FirebaseAuth.getInstance();

        fStorage = FirebaseFirestore.getInstance();
        fImageStorage = FirebaseStorage.getInstance();
        //binding class with view
        binding = LoginBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        realitycheckbox = (ImageView) view.findViewById(R.id.realitycheckLOGOBOX);
        translateAnimation();
        fadeinAnimation();
        slideupAnimation();
        //click login to call userLogin()
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserLogin();
            }
        });

        binding.resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginPage.this)
                        .navigate(R.id.action_LogInPage_to_ResetPasswordActivity);
            }
        });

        binding.showPassowrd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    binding.password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                if(b){
                    binding.password.setInputType(129);
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void UserLogin() {
        String email = binding.username.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        Matcher m = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        //tests format of user login information
        if (email.isEmpty()) {
            binding.username.setError("Please enter your username or email");
            binding.username.requestFocus();
            return;
        }
        if (m.find() == false) {
            binding.username.setError("Invalid email address");
            binding.username.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            binding.password.setError("Please enter your password");
            binding.password.requestFocus();
            return;
        }

        //Authenticates with firebase and signs in user

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    createUserFromFireBase();
                    // go to post page
                } else {
                    Toast.makeText(getContext(), "Failed to login please try again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    // this creates the user from the database
    public void createUserFromFireBase() {
        DocumentReference docRef = fStorage.collection("Users").document(mAuth.getCurrentUser().getDisplayName());
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
                currUser = new User(uid, email, username, name, bio, birthday, profileImagePath, posts, followers, following, friends,privateMode,notificationsEnabled,taggedIn);
                // Reference to an image file in Cloud Storage
                FirebaseStorage.getInstance().getReference().child("images/" + profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        storageProfilePictureReference = uri;
                    }
                });

                NavHostFragment.findNavController(LoginPage.this)
                        .navigate(R.id.action_LogInPage_to_PostActivity);
                Toast.makeText(getContext(), "Successful login to account \n", Toast.LENGTH_LONG).show();

            }

        });
    }
}

