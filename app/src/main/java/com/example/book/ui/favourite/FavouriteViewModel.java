package com.example.book.ui.favourite;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.book.ui.Model.Post;
import com.example.book.ui.extra.Enums;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavouriteViewModel extends ViewModel {

    private MutableLiveData<List<Post>> posts;
    private List<Post> bookList = new ArrayList<>();
    private FirebaseAuth auth;

    // Set to store book IDs already added to favorites
    private Set<String> addedBookIds = new HashSet<>();

    public FavouriteViewModel() {
        posts = new MutableLiveData<>();

        auth = FirebaseAuth.getInstance();
        // Initialize or load data from Firebase here
        loadDataFromFirebase();
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    private void loadDataFromFirebase() {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("favorites");
        databaseReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        bookList.clear();
                        addedBookIds.clear(); // Clear existing added book IDs

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post book = snapshot.getValue(Post.class);
                            if (book != null) {
                                // Check if the book ID is not already added
                                if (!addedBookIds.contains(book.getPostId())) {
                                    bookList.add(book);
                                    addedBookIds.add(book.getPostId());
                                }
                            }
                        }

                        // Update LiveData with the new data
                        posts.setValue(bookList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error appropriately
                    }
                });
    }
}
