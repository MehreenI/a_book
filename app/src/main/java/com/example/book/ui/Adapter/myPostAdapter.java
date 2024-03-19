package com.example.book.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.R;
import com.example.book.ui.Model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class myPostAdapter extends RecyclerView.Adapter<myPostAdapter.PostViewHolder> {

    private List<Post> postList;
    private Context context;
    private OnItemClickListener listener;

    public myPostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_post_list, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView bookName;
        private TextView price;
        private TextView dateTime;
        private ImageView favorite;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            bookName = itemView.findViewById(R.id.bookname);
            price = itemView.findViewById(R.id.price);
            dateTime = itemView.findViewById(R.id.datetime);
            favorite = itemView.findViewById(R.id.favorite);
        }

        public void bind(Post post) {
            // Load image using Picasso or any other image loading library
            Picasso.get().load(post.getImageUrl()).into(imageView);
            bookName.setText(post.getBookName());
            price.setText(post.getBookPrice());
            dateTime.setText(post.getUploadDate());

            // Handle remove book click
            favorite.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.removeBook(position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void removeBook(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
