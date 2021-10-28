package com.example.realitycheck.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realitycheck.bean.PostBean;
import com.example.realitycheck.databinding.ItemPostBinding;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private Context context;
    private List<PostBean> postList;

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
        String description = postBean.getDescription();
        String currentDate = postBean.getCurrentDate();
        holder.binding.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int likeCount = Integer.parseInt(holder.binding.tvLike.getText().toString());
                holder.binding.tvLike.setText(Integer.toString(likeCount+1));
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
