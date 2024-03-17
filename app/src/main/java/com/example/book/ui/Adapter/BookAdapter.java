//package com.example.book.ui.Adapter;
//
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.book.databinding.ActivityBookListBinding;
//import com.example.book.ui.Model.Post;
//import com.example.book.ui.bookdetail.BookDetailActivity;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
//
//    private List<Post> bookList;
//    private OnItemClickListener listener;
//
//    public BookAdapter(List<Post> bookList) {
//        this.bookList = bookList;
//    }
//
//    public void setData(List<Post> newBookList) {
//        this.bookList = newBookList;
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        ActivityBookListBinding binding = ActivityBookListBinding.inflate(
//                LayoutInflater.from(parent.getContext()), parent, false);
//        return new BookViewHolder(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
//        Post book = bookList.get(position);
//
//        // Load the book details into the views
//        holder.binding.bookname.setText(book.getBookName());
//        holder.binding.price.setText("Price: " + book.getBookPrice() + "/-");
//
//        // 3rd party library used to load image URLs
//        Picasso.get().load(book.getImageUrl()).into(holder.binding.imageView);
//
//        // Set the date in the datetime TextView
//        holder.binding.datetime.setText(book.getUploadDate());
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (listener != null) {
//                    int position = holder.getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        listener.onItemClick(position);
//                        // Open the BookDetailActivity and pass the details
//                        Intent intent = new Intent(view.getContext(), BookDetailActivity.class);
//                        intent.putExtra("postId", book.getPostId());
//                        intent.putExtra("bookName", book.getBookName());
//                        intent.putExtra("bookPrice", book.getBookPrice());
//                        intent.putExtra("imageUrl", book.getImageUrl());
//                        intent.putExtra("description", book.getDescription());
//                        ArrayList<String> authorsList = new ArrayList<>(book.getAuthors());
//                        intent.putStringArrayListExtra("author", authorsList);
//                        intent.putExtra("condition",book.getCondition());
//                        intent.putExtra("sellerId",book.getUserId());
//                        view.getContext().startActivity(intent);
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return bookList.size();
//    }
//
//    public Post getItem(int position) {
//        return bookList.get(position);
//    }
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.listener = listener;
//    }
//
//    public interface OnItemClickListener {
//        void onItemClick(int position);
//    }
//
//    public static class BookViewHolder extends RecyclerView.ViewHolder {
//        // Use the generated binding class
//        ActivityBookListBinding binding;
//
//        public BookViewHolder(ActivityBookListBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//    }
//}
package com.example.book.ui.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.databinding.ActivityBookListBinding;
import com.example.book.databinding.ActivityFeaturedBookListBinding;
import com.example.book.ui.Model.Post;
import com.example.book.ui.bookdetail.BookDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FEATURED_BOOK = 0;
    private static final int TYPE_REGULAR_BOOK = 1;

    private List<Post> bookList;
    private OnItemClickListener listener;

    public BookAdapter(List<Post> bookList) {
        this.bookList = bookList;
    }

    public void setData(List<Post> newBookList) {
        this.bookList = newBookList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        // Determine the view type based on your logic (e.g., check if the book is featured)
        return bookList.get(position).isFeatured() ? TYPE_FEATURED_BOOK : TYPE_REGULAR_BOOK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_FEATURED_BOOK) {
            ActivityFeaturedBookListBinding featuredBinding = ActivityFeaturedBookListBinding.inflate(
                    inflater, parent, false);
            return new FeaturedBookViewHolder(featuredBinding, listener);
        } else {
            ActivityBookListBinding regularBinding = ActivityBookListBinding.inflate(
                    inflater, parent, false);
            return new RegularBookViewHolder(regularBinding, listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post book = bookList.get(position);

        if (holder instanceof FeaturedBookViewHolder) {
            FeaturedBookViewHolder featuredHolder = (FeaturedBookViewHolder) holder;
            // Bind data for featured book
            featuredHolder.binding.featuredTag.setVisibility(View.VISIBLE);
            // Load the book details into the views
            featuredHolder.binding.bookname.setText(book.getBookName());
            featuredHolder.binding.price.setText("Price: " + book.getBookPrice() + "/-");
            Picasso.get().load(book.getImageUrl()).into(featuredHolder.binding.imageView);
            featuredHolder.binding.datetime.setText(book.getUploadDate());

        } else if (holder instanceof RegularBookViewHolder) {
            // Bind data for regular book
            RegularBookViewHolder regularHolder = (RegularBookViewHolder) holder;
            regularHolder.binding.bookname.setText(book.getBookName());
            regularHolder.binding.price.setText("Price: " + book.getBookPrice() + "/-");
            Picasso.get().load(book.getImageUrl()).into(regularHolder.binding.imageView);
            regularHolder.binding.datetime.setText(book.getUploadDate());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                        // Open the BookDetailActivity and pass the details
                        Intent intent = new Intent(view.getContext(), BookDetailActivity.class);
                        intent.putExtra("postId", book.getPostId());
                        intent.putExtra("bookName", book.getBookName());
                        intent.putExtra("bookPrice", book.getBookPrice());
                        intent.putExtra("imageUrl", book.getImageUrl());
                        intent.putExtra("description", book.getDescription());
                        ArrayList<String> authorsList = new ArrayList<>(book.getAuthors());
                        intent.putStringArrayListExtra("author", authorsList);
                        intent.putExtra("condition",book.getCondition());
                        intent.putExtra("sellerId",book.getUserId());
                        view.getContext().startActivity(intent);

                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public Post getItem(int position) {
        return bookList.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void addFavourite(int position);
    }

    // ViewHolder for Featured Books
    public static class FeaturedBookViewHolder extends RecyclerView.ViewHolder {
        ActivityFeaturedBookListBinding binding;

        public FeaturedBookViewHolder(ActivityFeaturedBookListBinding binding, OnItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            binding.favorite.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.addFavourite(position);

                }
            });
        }
    }

    // ViewHolder for Regular Books
    public class RegularBookViewHolder extends RecyclerView.ViewHolder {
        ActivityBookListBinding binding;

        public RegularBookViewHolder(ActivityBookListBinding binding, OnItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            binding.favorite.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.addFavourite(position);
                }
            });
        }
    }
}