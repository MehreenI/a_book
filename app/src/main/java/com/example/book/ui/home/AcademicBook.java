package com.example.book.ui.home;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.book.R;
import com.example.book.ui.Adapter.BookAdapter;
import com.example.book.ui.Model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.book.databinding.ActivityAcademicBookBinding;

import java.util.ArrayList;
import java.util.List;

public class AcademicBook extends AppCompatActivity {

    private ActivityAcademicBookBinding binding;
    private List<Post> allBooks;
    private BookAdapter bookAdapter;
    private AcademicViewModel academicViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use layout binding to inflate the layout
        binding = ActivityAcademicBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Academic Book"); // Set the title
        }

        // Initialize ViewModelProvider
        academicViewModel = new ViewModelProvider(this).get(AcademicViewModel.class);

        // Initialize RecyclerView and set up adapter
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookAdapter = new BookAdapter(new ArrayList<>());
        recyclerView.setAdapter(bookAdapter);

        // Observe the LiveData from ViewModel and update UI when data changes
        academicViewModel.getPosts().observe(this, posts -> {
            allBooks = posts; // Update allBooks with the new data
            bookAdapter.setData(posts);
        });

        // Initialize the SearchView from the binding
        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });

        // Set item click listener
        bookAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Post clickedBook = bookAdapter.getItem(position);
            }

            @Override
            public void addFavourite(int position) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (userId != null) {
                    DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("favorites");

                    Post selectedBook = bookAdapter.getItem(position);

                    String favoriteId = favoritesRef.push().getKey();
                    favoritesRef.child(favoriteId).setValue(selectedBook);
                    favoritesRef.child(favoriteId).child("userId").setValue(userId);
                    Toast.makeText(AcademicBook.this, "Book added to favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void filterBooks(String query) {
        List<Post> filteredBooks = new ArrayList<>();

        if (allBooks != null) {
            for (Post book : allBooks) {
                if (isBookMatch(book, query)) {
                    filteredBooks.add(book);
                }
            }
        }

        bookAdapter.setData(filteredBooks);
    }

    private boolean isBookMatch(Post book, String query) {
        String bookName = book.getBookName().toLowerCase();
        String authors = book.getAuthors().toString().toLowerCase();

        return bookName.contains(query.toLowerCase()) || authors.contains(query.toLowerCase());
    }
}
