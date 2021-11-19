package com.example.realitycheck;

import java.util.ArrayList;

public class Group {
    public String groupName;
    public String bio;
    public String profileImagePath;

    public boolean privacy; //true is private, false is public

    public int size; //number of members
    public ArrayList<String> members;
    public ArrayList<Post> posts;
    public ArrayList<Post> postLiked;
    public ArrayList<Post> reposted;

    public Group() {
    }

    public Group(String groupName, String bio, String profileImagePath, boolean privacy,
                 ArrayList<Post> posts, ArrayList<String> members) {
        this.groupName = groupName;
        this.bio = bio;
        this.profileImagePath = profileImagePath;

        this.privacy = privacy;

        this.members = members;
        this.size = members.size();

        this.posts = posts;

    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public int getSize() {
        return size;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public ArrayList<Post> getPostLiked() {
        return postLiked;
    }

    public void setPostLiked(ArrayList<Post> postLiked) {
        this.postLiked = postLiked;
    }

    public ArrayList<Post> getReposted() {
        return reposted;
    }

    public void setReposted(ArrayList<Post> reposted) {
        this.reposted = reposted;
    }
}