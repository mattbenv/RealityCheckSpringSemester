package com.example.realitycheck;


import java.util.ArrayList;

public class User {
    public String email;
    public String username;
    public String name;
    public String bio;
    public String birthday;
    public String profileImagePath;



    public int numfollowers;
    public int numfollowing;
    public int numfriends;

    //change from user list to string for now
    public ArrayList<String> followers;
    public ArrayList<String> following;
    public ArrayList<String> posts;
    public ArrayList<User> friends;
    public ArrayList<Post> postLiked;
    public ArrayList<Post> reposted;

    //more information to track

    public User(String email, String username, String name, String bio, String birthday, String profileImagePath, ArrayList<String>  posts, ArrayList<String> followers, ArrayList<String> following, ArrayList<User> friends){
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
}
