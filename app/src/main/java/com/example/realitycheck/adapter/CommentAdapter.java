package com.example.realitycheck.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realitycheck.Comment;
import com.example.realitycheck.Activitys.LoginPage;
import com.example.realitycheck.R;
import com.example.realitycheck.User;
import com.example.realitycheck.Activitys.ViewPostActivity;
import com.example.realitycheck.Activitys.otherUserProfileActivity;
import com.example.realitycheck.databinding.ItemCommentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;

    public Uri postAuthorProfilePhoto;

    public ArrayList<Comment> commentList;
    public static Comment comment;
    public String author;
    public String content;
    public String date;
    public FirebaseFirestore fStore;

    public static User userToNavTo;


    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.commentList = comments;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(LayoutInflater.from(context), parent, false);
        CommentAdapter.CommentViewHolder commentViewHolder = new CommentAdapter.CommentViewHolder(binding);
        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {

        //sorts list of post based on post date
        //need to convert String postDate to dateTime in order to compare accurately
        Collections.sort(commentList, new Comparator<Comment>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public int compare(Comment comment, Comment t1) {
                return(comment.getCommentDate().compareTo(t1.getCommentDate()));

            }
        });
        //sets in order of most recent
        Collections.reverse(commentList);

        //initialize Firebase Firestore database instance
        fStore = FirebaseFirestore.getInstance();


        //gets current post from the recycler view list based its position
        comment = commentList.get(position);

        //gets comment values and sets them on screen
        content = comment.getCommentContent();
        author = comment.getCommentAuthor();
        date = comment.getCommentDate();
        holder.binding.tvTitle.setText(author);
        holder.binding.tvContent.setText(content);
        holder.binding.date.setText(date);

        //click profile photo to navigate to the user's profile
        holder.binding.sivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment = commentList.get(position);
                navigateToUserProfile(holder);

            }
        });
        //click profile photo to navigate to the user's profile
        holder.binding.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment = commentList.get(position);
                navigateToUserProfile(holder);

            }
        });



        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment = commentList.get(position);
                deleteComment(holder,comment);

            }
        });
        //gets the post authors profile photo and displays it on the comment
        loadProfilePhotos(author,holder);

    }


    //gets the post authors profile photo and displays it on the comment
    public void loadProfilePhotos(String author,CommentViewHolder holder){
        DocumentReference docRef = fStore.collection("Users").document(author);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> userMap = documentSnapshot.getData();
                String profileImagePath = userMap.get("profileImagePath").toString();
                // Reference to an image file in Cloud Storage
                FirebaseStorage.getInstance().getReference().child("images/"+profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        postAuthorProfilePhoto = uri;
                        ImageView imageView = holder.binding.sivAvatar;
                        Glide.with(context)
                                .load(postAuthorProfilePhoto)
                                .into(imageView);
                    }
                });
            }
        });
    }

    //adds new comment
    public void addData(Comment newItem) {
        commentList.add(0,newItem);
        notifyItemInserted(0);
    }


    //removes post from the recyclerview
    public void removeData(Comment item){
        int position = commentList.indexOf(item);
        commentList.remove(item);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());

    }

    public void deleteComment(CommentViewHolder holder, Comment comment){
        if(comment.getCommentAuthor().contains(LoginPage.currUser.username)){
            new AlertDialog.Builder(context)
                    .setTitle("Delete Comment")
                    .setMessage("Are you sure you want to delete this comment?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String postID = ViewPostActivity.currPost.getPostId();
                            ArrayList<Comment> comments;
                            DocumentReference postDoc = FirebaseFirestore.getInstance().collection("Posts").document(postID);
                            postDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot querySnapshot) {
                                    ArrayList<HashMap<String,Object>> commentsMap = (ArrayList<HashMap<String, Object>>) querySnapshot.get("comments");
                                    ArrayList<Comment> comments = new ArrayList<>();
                                    for(HashMap commentMap :commentsMap){
                                        Comment eachComment = new Comment(commentMap.get("commentAuthor").toString(),commentMap.get("commentContent").toString(),commentMap.get("commentDate").toString());
                                        comments.add(eachComment);
                                    }
                                    for(int i = 0;i<comments.size();i++){
                                        if(comments.get(i).getCommentDate().compareTo(comment.getCommentDate())==0){
                                            comments.remove(i);
                                        }
                                    }
                                    ViewPostActivity.currPost.setComments(comments);
                                    ViewPostActivity.currPost.setCommentCount(comments.size());
                                    ViewPostActivity.binding.commentCount.setText(String.valueOf(comments.size()));
                                    FirebaseFirestore.getInstance().collection("Posts").document(postID).update("comments",comments);
                                    FirebaseFirestore.getInstance().collection("Posts").document(postID).update("commentCount",comments.size());

                                }

                            });
                            removeData(comment);
                        }
                    }).setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
    }


    //gets the information for the user to navigate to, and then loads that navigate to that user's profile page
    public void navigateToUserProfile(CommentAdapter.CommentViewHolder holder){
        fStore.collection("Users").document(comment.getCommentAuthor()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                ArrayList<String> reposted = (ArrayList<String>) userMap.get("reposted");
                userToNavTo = new User(uid, email, username, name, bio, birthday, profileImagePath, posts, followers, following, friends,privateMode,notificationsEnabled,taggedIn,reposted);
                otherUserProfileActivity.previousActivty = "comment";
                Navigation.createNavigateOnClickListener(R.id.to_OtherUserProfileActivity).onClick(holder.binding.sivAvatar);
            }
        });
    }



    @Override
    public int getItemCount() {
        return commentList.size();
    }


    class CommentViewHolder extends RecyclerView.ViewHolder {
        ItemCommentBinding binding;
        TextView postAuthor;
        TextView postContent;
        TextView postDate;
        TextView likeCount;
        ImageView postAuhtorProfileImage;



        public CommentViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            postAuthor = this.binding.tvTitle;
            postContent = this.binding.tvContent;
            postDate = this.binding.date;
            postAuhtorProfileImage = this.binding.sivAvatar;


        }
    }
}
