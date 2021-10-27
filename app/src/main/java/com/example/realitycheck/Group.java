package com.example.realitycheck;

import java.util.ArrayList;

public class Group {
    public String name;
    public String bio;
    public String profileImagePath;


    public int nummembers;
    public ArrayList<User> members;
    public ArrayList<Post> posts;
    public ArrayList<Post> postLiked;
    public ArrayList<Post> reposted;

    public Group(String name, String bio, String profileImagePath, ArrayList<Post>  posts, ArrayList<User> members){
        this.name = name;
        this.bio = bio;
        this.profileImagePath = profileImagePath;

        this.members = members;
        this.nummembers = members.size();

        this.posts = posts;

    }
}
