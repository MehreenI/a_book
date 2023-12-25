package com.example.book.ui.bookdetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.book.AppController;
import com.example.book.MainActivity;
import com.example.book.R;
import com.example.book.databinding.ActivityBookDetailBinding;
import com.example.book.manager.CoinFetchCallback;
import com.example.book.manager.CoinManager;
import com.example.book.ui.home.HomeFragment;
import com.example.book.ui.signin.loginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BookDetailActivity extends AppCompatActivity {

    private ActivityBookDetailBinding binding;
    private Dialog dialogue;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();

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

        dialogue = new Dialog(this);

        // click listener on interested button
        binding.interested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLoginChecked();
            }
        });

    }

    private void showInterestedDialogueBox() {
        dialogue.setContentView(R.layout.activity_dialogue_bid_offer);

        // Find the "OK" button in the dialog layout
        Button okButton = dialogue.findViewById(R.id.ok);

        // Set click listener on the "OK" button
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoinManager coinManager = AppController.getInstance().getManager(CoinManager.class);
                coinManager.getTotalCoins(new CoinFetchCallback() {
                    @Override
                    public void onCoinsFetched(int totalCoins) {
                        if (totalCoins >= 5) {
                            // Deduct coins in Firebase
                            deductCoinsFromFirebase(5);
                            Toast.makeText(BookDetailActivity.this, "You are interested! Coins deducted: 5", Toast.LENGTH_SHORT).show();

                            // Close the dialog when the "OK" button is clicked
                            dialogue.dismiss();

                            // Delay starting MainActivity by 1 seconds so that updated coins are loaded
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(BookDetailActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }, 1000); // 5000 milliseconds = 5 seconds
                        } else {
                            Toast.makeText(BookDetailActivity.this, "Not enough coins. Please earn more coins.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialogue.show();
    }

    private void userLoginChecked() {
        if (firebaseAuth.getCurrentUser() != null) {
            showInterestedDialogueBox();
        } else {
            Intent intent = new Intent(BookDetailActivity.this, loginActivity.class);
            startActivity(intent);
            Toast.makeText(BookDetailActivity.this, "Kindly Login First", Toast.LENGTH_SHORT).show();
        }
    }

    private void deductCoinsFromFirebase(int coinsToDeduct) {
        // Get the user's ID from Firebase Auth
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Reference to the user's coin data in Firebase
        DatabaseReference userCoinsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("coin");

        userCoinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the current coin balance
                    int currentCoins = dataSnapshot.getValue(Integer.class);

                    // Deduct the coins
                    int newCoinsBalance = currentCoins - coinsToDeduct;

                    // Update the user's coin balance in Firebase
                    userCoinsRef.setValue(newCoinsBalance);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if needed
                Log.e("CoinDeduction", "Database Error: " + databaseError.getMessage());
            }
        });
    }
}
