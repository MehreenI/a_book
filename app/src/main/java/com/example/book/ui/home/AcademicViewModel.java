package com.example.book.ui.home;

import android.widget.ArrayAdapter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.book.ui.Model.Post;
import com.example.book.ui.extra.Enums;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AcademicViewModel extends ViewModel {

    private MutableLiveData<List<Post>> posts;
    private List<Post> bookList = new ArrayList<>();

    public AcademicViewModel() {
        posts = new MutableLiveData<>();
        // Initialize or load data from Firebase here
        loadDataFromFirebase();
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    private void loadDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        databaseReference.orderByChild("bookCategory").equalTo(Enums.BookCategory.ACADEMIC.toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        bookList.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post book = snapshot.getValue(Post.class);
                            if (book != null) {
                                bookList.add(book);
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

