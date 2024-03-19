package com.example.book.ui.myPost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.book.R;
import com.example.book.ui.Adapter.myPostAdapter;
import com.example.book.ui.Model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyPost extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private myPostAdapter postAdapter;
    private List<Post> allBooks;
    private final String TAG = "My Post";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list
        postAdapter = new myPostAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(postAdapter);

        mAuth = FirebaseAuth.getInstance();

        // Fetch data from Firebase
        loadDataFromFirebase();

        // Set click listener for item removal
        postAdapter.setOnItemClickListener(new myPostAdapter.OnItemClickListener() {
            @Override
            public void removeBook(int position) {
                deletePost(position);
            }
        });
    }

    private void loadDataFromFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("uploads");

        postsRef.orderByChild("userId").equalTo(currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        allBooks = new ArrayList<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);
                            if (post != null) {
                                allBooks.add(post);
                            }
                        }

                        Collections.reverse(allBooks);

                        // Initially, show all books
                        postAdapter.setPostList(allBooks);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Error fetching featured posts: " + error.getMessage());
                    }
                });
    }

    private void deletePost(int position) {
        Post postToRemove = allBooks.get(position);
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("uploads");

        if (postToRemove != null) {
            String postId = postToRemove.getPostId();
            postsRef.child(postId).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Post deleted successfully from Firebase
                                allBooks.remove(position);
                                postAdapter.setPostList(allBooks);
                                Toast.makeText(MyPost.this, "Post deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                // Failed to delete post from Firebase
                                Toast.makeText(MyPost.this, "Failed to delete post", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
