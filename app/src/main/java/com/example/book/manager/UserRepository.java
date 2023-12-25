package com.example.book.manager;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserRepository {

    private static final String BOOKS_NODE = "users"; // Replace with your actual node name

    private DatabaseReference databaseReference;

    // Singleton instance
    private static UserRepository instance;

    private UserRepository() {
        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    // Fetch book name based on book ID
    public void getBookNameById(String userId, final UserNameFetchCallback callback) {
        DatabaseReference bookReference = databaseReference.child(BOOKS_NODE).child(userId).child("username");
        bookReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.getValue(String.class);
                    callback.onUserNameFetched(userName);
                } else {
                    callback.onUserNameFetchFailed("user not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onUserNameFetchFailed(databaseError.getMessage());
            }
        });
    }

    // Callback interface to handle asynchronous book name fetching
    public interface UserNameFetchCallback {
        void onUserNameFetched(String bookName);

        void onUserNameFetchFailed(String errorMessage);
    }
}

