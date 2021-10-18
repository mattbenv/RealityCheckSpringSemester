package com.example.realitycheck;


import java.util.ArrayList;

public class User {
    public String email;
    public String username;



    public int numfollowers;
    public int numfollowing;
    public int numfriends;
    public ArrayList<User> followers;
    public ArrayList<User> following;
    public ArrayList<Post> posts;
    public ArrayList<User> friends;
    public ArrayList<Post> postLiked;
    public ArrayList<Post> reposted;

    //more information to track

    public User(String email, String username, ArrayList<Post>  posts, ArrayList<User> followers, ArrayList<User> following, ArrayList<User> friends ){
        this.email=email;
        this.username  = username;

        this.followers = followers;
        this.numfollowers = followers.size();

        this.following = following;
        this.numfollowing = following.size();

        this.friends = friends;
        this.numfriends = returnNumfriends(followers, following);

        this.posts = posts;

    }

    //This function compares the followers to following and returns the number of Friends
    public int returnNumfriends(ArrayList<User> followers, ArrayList<User>  following){
        int count=0;
        for (int i = 0;i < followers.size()-1; i++) {
            for(int j = 1; j <following.size()-1; j++) {
                if (followers.get(i)==following.get(j)){
                        count++;
                }
            }
            }
        return count;
    }
    //This function compares the followers to following and returns the number of Friends
    public ArrayList<User>  returnfriends(ArrayList<User> followers, ArrayList<User> following){
        int count=0;
        ArrayList<User>  friends = new ArrayList<User>(); //allocated 100 friends to be the capacity if this reaches above 100 reallocate
        for (int i = 0;i < followers.size()-1; i++) {
            for(int j = 1; j <following.size()-1; j++) {
                if (followers.get(i)==following.get(j)){
                    friends.add(followers.get(i));
                    count++;
                }
            }
        }
        return friends;
    }
}
