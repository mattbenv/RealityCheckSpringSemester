package com.example.realitycheck;


import java.util.ArrayList;

public class User {
    public String email;
    public String username;
    public String name;
    public String bio;
    public String birthday;
    public boolean privateMode;
    public boolean notificationsEnabled;
    public String uid;
    public String profileImagePath;



    public int numfollowers;
    public int numfollowing;
    public int numfriends;


    //change from user list to string for now
    public ArrayList<String> followers;
    public ArrayList<String> following;
    public ArrayList<String> posts;
    public ArrayList<String> taggedIn;
    public ArrayList<String> friends;
    public ArrayList<Post> postLiked;
    public ArrayList<String> reposted;
    public ArrayList<String> saved;

    //more information to track
    public User(){

    }

    public User(String uid,String email, String username, String name, String bio, String birthday, String profileImagePath, ArrayList<String>  posts, ArrayList<String> followers, ArrayList<String> following, ArrayList<String> friends,Boolean privateMode, Boolean notificationsEnabled,ArrayList<String> taggedIn,ArrayList<String> reposted){
        this.uid = uid;
        this.email=email;
        this.username  = username;
        this.name = name;
        this.bio = bio;
        this.birthday = birthday;
        this.profileImagePath = profileImagePath;

        this.followers = followers;
        this.numfollowers = followers.size();

        this.following = following;
        this.numfollowing = following.size();

        this.friends = friends;
        this.numfriends = returnNumfriends(followers, following);

        this.posts = posts;
        this.saved = saved;

        this.privateMode = privateMode;
        this.notificationsEnabled = notificationsEnabled;
        this.taggedIn = taggedIn;
        this.reposted = reposted;

    }

    //This function compares the followers to following and returns the number of Friends
    public int returnNumfriends(ArrayList<String> followers, ArrayList<String>  following){
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

    public void sort( ArrayList<User> users){

    }

    public static void sortStrings(String[] arr, int n)
    {
        String storage;

        // Sorting strings using bubble sort
        for (int j = 0; j < n - 1; j++)
        {
            for (int i = j + 1; i < n; i++)
            {
                if (arr[j].compareTo(arr[i]) > 0)
                {
                    storage = arr[j];
                    arr[j] = arr[i];
                    arr[i] = storage;
                }
            }
        }
    }

    // Driver code
    public static void main(String[] args)
    {
        String[] names = {
                "mike",
                "mike",
                "thomas",
                "jonathan",
                "akon",
                "drake",
                "biggie",
                "jayz",
                "oneil",
                "benvenuto",
                "thommmy",
                "aaron",
                "macmiller",
                "mAC",
                "mac",
                "miller",
                "juicewrld",
                "boogiewitahoodie",
                "jcole",
                "excision",
                "porterrobinson",
                "pporterRobinson",
                "matt",
                "thomas",
                "michael",
                "martin",
                "deb",
                "patrick",
                "max",
                "geroge",
                "carol",
                "cheikna",
                "jonathan",
                "deb",
                "bennydabull"

        };
        int n = names.length;
        sortStrings(names, n);
        System.out.println("Sorted Names: ");
        for (int i = 0; i < n; i++)
            System.out.println("" + names[i]);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public boolean isPrivateMode() {
        return privateMode;
    }

    public void setPrivateMode(boolean privateMode) {
        this.privateMode = privateMode;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public int getNumfollowers() {
        return numfollowers;
    }

    public void setNumfollowers(int numfollowers) {
        this.numfollowers = numfollowers;
    }

    public int getNumfollowing() {
        return numfollowing;
    }

    public void setNumfollowing(int numfollowing) {
        this.numfollowing = numfollowing;
    }

    public int getNumfriends() {
        return numfriends;
    }

    public void setNumfriends(int numfriends) {
        this.numfriends = numfriends;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public ArrayList<String> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<String> posts) {
        this.posts = posts;
    }

    public ArrayList<String> getSaved() {
        return saved;
    }

    public void setSaved(ArrayList<String> saved) {
        this.saved = saved;
    }

    public ArrayList<String> getTaggedIn() {
        return taggedIn;
    }

    public void setTaggedIn(ArrayList<String> taggedIn) {
        this.taggedIn = taggedIn;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public ArrayList<Post> getPostLiked() {
        return postLiked;
    }

    public void setPostLiked(ArrayList<Post> postLiked) {
        this.postLiked = postLiked;
    }

    public ArrayList<String> getReposted() {
        return reposted;
    }

    public void setReposted(ArrayList<String> reposted) {
        this.reposted = reposted;
    }
}