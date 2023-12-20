package com.example.book.ui.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.databinding.ActivityBookListBinding;
import com.example.book.ui.Model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Post> bookList;
    private OnItemClickListener listener;


    public BookAdapter(List<Post> bookList) {
        this.bookList = bookList;
    }

    public void setData(List<Post> newBookList) {
        this.bookList = newBookList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityBookListBinding binding = ActivityBookListBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BookViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Post book = bookList.get(position);

        // Load the book details into the views
        holder.binding.bookname.setText(book.getBookName());
        holder.binding.price.setText("Price: " + book.getBookPrice() + "/-");

        // 3rd party library used to load image URLs
        Picasso.get().load(book.getImageUrl()).into(holder.binding.imageView);

        // Set the date in the datetime TextView
        holder.binding.datetime.setText(book.getUploadDate());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (listener != null) {
//                    int position = holder.getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        listener.onItemClick(position);
//                        // Open the BookDetailActivity and pass the details
//                        Intent intent = new Intent(view.getContext(), BookDetailActivity.class);
//                        intent.putExtra("bookName", book.getBookName());
//                        intent.putExtra("bookPrice", book.getBookPrice());
//                        intent.putExtra("imageUrl", book.getImageUrl());
//                        view.getContext().startActivity(intent);
//                    }
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        // Use the generated binding class
        ActivityBookListBinding binding;

        public BookViewHolder(ActivityBookListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

