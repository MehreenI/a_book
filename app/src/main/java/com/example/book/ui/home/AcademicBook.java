package com.example.book.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.book.R;
import com.example.book.ui.Adapter.BookAdapter;

import java.util.ArrayList;

public class AcademicBook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_book);

        // Assuming you have initialized ViewModelProvider properly
        AcademicViewModel academicViewModel = new ViewModelProvider(this).get(AcademicViewModel.class);


        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the adapter
        BookAdapter bookAdapter = new BookAdapter(new ArrayList<>()); // Pass an empty list initially
        recyclerView.setAdapter(bookAdapter);

        // Observe the LiveData from ViewModel and update UI when data changes
        academicViewModel.getPosts().observe(this, posts -> {
            // Update RecyclerView adapter with the new data
            bookAdapter.setData(posts);
        });
    }
}
