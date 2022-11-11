package com.example.myapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private final Context context ;
    private final ArrayList<Post> mPosts;
    private final OnPostClickListener mOnPostClickListener;

    public interface OnPostClickListener {
        void onPostClick(int position);
    }

    public PostAdapter(Context context, ArrayList<Post> mPosts, OnPostClickListener onPostClickListener){
        this.context = context;
        this.mPosts = mPosts;
        this.mOnPostClickListener = onPostClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView postName, postText, postDate, postAuthor;

        public MyViewHolder(View itemView) {
            super(itemView);

            // Инициализация View
            postName = itemView.findViewById(R.id.post_name);
            postText = itemView.findViewById(R.id.post_text);
            postAuthor = itemView.findViewById(R.id.post_author);
            postDate = itemView.findViewById(R.id.post_date);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View posts = LayoutInflater.from(context).inflate(R.layout.post_card_element, parent, false);
        return new MyViewHolder(posts);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = mPosts.get(position);
        holder.postName.setText(post.getTitle());
        holder.postText.setText(post.getEpigraph());
        holder.postAuthor.setText(post.getAuthor());

        // Форматирование вывода даты
        SimpleDateFormat myFormat = new SimpleDateFormat("EEE, d MMM yyyy '\nв' K:mm a");
        holder.postDate.setText(myFormat.format(post.getDate()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnPostClickListener.onPostClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }
}
