package com.example.realitycheck;


import java.util.ArrayList;

public class User {
    public String email;
    public String username;



    public ArrayList<Post> post;
    public int followers;
    public int following;
    public int friends;
    //public ArrayList<User> followers;
    //public ArrayList<User> following;
    //public ArrayList<User> friends;

    //more information to track

    public User(String email, String username, ArrayList<Post> post, int followers, int following, int friends ){
        this.email=email;
        this.username  = username;

        this.post = post;
        this.followers = followers;
        this.following = following;
        this.friends = friends;
    }
}
