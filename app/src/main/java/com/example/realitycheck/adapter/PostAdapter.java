package com.example.realitycheck.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realitycheck.bean.PostBean;
import com.example.realitycheck.databinding.ItemPostBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
//todo: understand how homepage works which is related to post adapter/viewholder/activity. if we can, design new one
public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private Context context;
    private List<PostBean> postList;
    public int a;

    public PostAdapter(Context context) {
        this.context = context;
        postList = new ArrayList<>();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false);
        PostViewHolder postViewHolder = new PostViewHolder(binding);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostBean postBean = postList.get(position);
        String avatar = postBean.getAvatar();
        String title = postBean.getTitle();
        String content = postBean.getContent();
        UUID postID = postBean.getPostId();
        String description = postBean.getDescription();
        String currentDate = postBean.getCurrentDate();
        final int[] a = new int[1];

        holder.binding.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int likeCount = Integer.parseInt(holder.binding.tvLike.getText().toString());
                holder.binding.tvLike.setText(Integer.toString(likeCount+1));
                DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(postID.toString());
                document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Map<String,Object> postMap = value.getData();
                        String likes = postMap.get("likeCount").toString();
                        String postAuthor = postMap.get("postAuthor").toString();
                        String postDate = postMap.get("postDate").toString();
                        int numlikes = Integer.valueOf(likes);
                        a[0] = numlikes;

                    }
                });
                FirebaseFirestore.getInstance().collection("Posts").document(postID.toString())
                        .update("likeCount",a[0]+1 );

                //update number of likes in post data
                //update list of users who liked this post
                //update view display number of likes
            }
        });
        if (avatar != null && !avatar.isEmpty()) {
            Glide.with(context).load(avatar).into(holder.binding.sivAvatar);
        }
        if (title != null && !title.isEmpty()) {
            holder.binding.tvTitle.setText(title);
        }
        if (content != null && !content.isEmpty()) {
            holder.binding.tvContent.setText(content);
        }
        if (description != null && !description.isEmpty()) {
            holder.binding.tvDescription.setText(description);
        }
            holder.binding.date.setText(currentDate);

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void addData(PostBean newItem) {
        postList.add(newItem);
        notifyItemInserted(postList.size());
        notifyItemChanged(postList.size());
    }

}

class PostViewHolder extends RecyclerView.ViewHolder {
    ItemPostBinding binding;
    public PostViewHolder(ItemPostBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
