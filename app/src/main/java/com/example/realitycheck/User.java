package com.example.realitycheck;


import java.util.ArrayList;

public class User {
    public String email;
    public String username;



    public int numfollowers;
    public int numfollowing;
    public int numfriends;
    public User[] followers;
    public User[] following;
    public Post[] posts;
    public User[] friends;

    //more information to track

    public User(String email, String username, Post[] posts, User[] followers, User[] following, User[] friends ){
        this.email=email;
        this.username  = username;

        this.followers = followers;
        this.numfollowers = followers.length;

        this.following = following;
        this.numfollowing = following.length;

        this.friends = friends;
        this.numfriends = returnNumfriends(followers, following);

        this.posts = posts;

    }

    //This function compares the followers to following and returns the number of Friends
    public int returnNumfriends(User[] followers, User[] following){
        int count=0;
        for (int i = 0;i < followers.length-1; i++) {
            for(int j = 1; j <following.length-1; j++) {
                if (followers[i]==following[j]){
                        count++;
                }
            }
            }
        return count;
    }
    //This function compares the followers to following and returns the number of Friends
    public User[] returnfriends(User[] followers, User[] following){
        int count=0;
        User[] friends = new User[100]; //allocated 100 friends to be the capacity if this reaches above 100 reallocate
        for (int i = 0;i < followers.length-1; i++) {
            for(int j = 1; j <following.length-1; j++) {
                if (followers[i]==following[j]){
                    friends[count]=followers[i];
                    count++;
                }
            }
        }
        return friends;
    }
}
