package com.example.book.ui.bookdetail;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.example.book.databinding.ActivityBookDetailBinding;
import com.squareup.picasso.Picasso;

public class BookDetailActivity extends AppCompatActivity {

    private ActivityBookDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get data from the intent
        Intent intent = getIntent();
        String bookName = intent.getStringExtra("bookName");
        String bookPrice = intent.getStringExtra("bookPrice");
        String imageUrl = intent.getStringExtra("imageUrl");
        String description = intent.getStringExtra("description");
        String author = intent.getStringExtra("author");
        String condition = intent.getStringExtra("condition");

        // Set data to the views
        binding.bookName.setText(bookName);
        binding.bookPrice.setText("Price: " + bookPrice + "/-");
        binding.description.setText(description);
        binding.author.setText(author);
        binding.condition.setText(condition);

        // Load image using Picasso
        Picasso.get().load(imageUrl).into(binding.imageView);
    }
}
