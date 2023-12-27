package com.example.book.ui.bookdetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.book.AppController;
import com.example.book.MainActivity;
import com.example.book.R;
import com.example.book.databinding.ActivityBookDetailBinding;
import com.example.book.manager.CoinFetchCallback;
import com.example.book.manager.CoinManager;
import com.example.book.ui.Model.Bid;
import com.example.book.ui.signin.loginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookDetailActivity extends AppCompatActivity {

    private ActivityBookDetailBinding binding;
    private Dialog dialogue;
    private FirebaseAuth firebaseAuth;
    String bookPrice;
    String sellerId;
    String bookName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();

        // Get data from the intent
        Intent intent = getIntent();

        bookName = intent.getStringExtra("bookName");
        bookPrice = intent.getStringExtra("bookPrice");
        String imageUrl = intent.getStringExtra("imageUrl");
        String description = intent.getStringExtra("description");
        ArrayList<String> authorsList = getIntent().getStringArrayListExtra("author");
        String condition = intent.getStringExtra("condition");
        sellerId = intent.getStringExtra("sellerId"); // Assuming you have sellerId in the intent

        // Set data to the views
        binding.bookName.setText(bookName);
        binding.bookPrice.setText("Price: " + bookPrice + "/-");
        binding.description.setText(description);

//        displaying Authors seperated by comma
        String authorsText = null;
        for (String author : authorsList) {
            authorsText += author + ", ";
        }
        // Remove the trailing comma and space
        authorsText = authorsText.replaceAll(", $", "");

        binding.author.setText(authorsText);
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
        binding.offerButtonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("offered", "offer button cliecked");
                showOfferedDialogue();
            }
        });
    }

    private void showOfferedDialogue() {
        dialogue.setContentView(R.layout.activity_dialogue_offer);

        Button offerButton = dialogue.findViewById(R.id.bid_offer);

        // Move the EditText initialization inside the method
        EditText bidAmountEditText = dialogue.findViewById(R.id.editTextNumber);

        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text when the button is clicked
                String bidAmount = bidAmountEditText.getText().toString();

                CoinManager coinManager = AppController.getInstance().getManager(CoinManager.class);
                coinManager.getTotalCoins(new CoinFetchCallback() {
                    @Override
                    public void onCoinsFetched(int totalCoins) {
                        if (totalCoins >= 5) {
                            // Deduct coins in Firebase
                            deductCoinsFromFirebase(5);

                            // Create and send bid request
                            sendBidRequest(Integer.parseInt(bidAmount), sellerId);

                            Toast.makeText(BookDetailActivity.this, "You are interested! Coins deducted: 5", Toast.LENGTH_SHORT).show();

                            // Close the dialog when the "OK" button is clicked
                            dialogue.dismiss();

                            // Delay starting MainActivity by 1 second so that updated coins are loaded
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(BookDetailActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }, 1000); // 1000 milliseconds = 1 second
                        } else {
                            Toast.makeText(BookDetailActivity.this, "Not enough coins. Please earn more coins.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialogue.show();
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

                            // Create and send bid request
                            sendBidRequest(Integer.parseInt(bookPrice), sellerId); // Pass sellerId to the method

                            Toast.makeText(BookDetailActivity.this, "You are interested! Coins deducted: 5", Toast.LENGTH_SHORT).show();

                            // Close the dialog when the "OK" button is clicked
                            dialogue.dismiss();

                            // Delay starting MainActivity by 1 second so that updated coins are loaded
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(BookDetailActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }, 1000); // 1000 milliseconds = 1 second
                        } else {
                            Toast.makeText(BookDetailActivity.this, "Not enough coins. Please earn more coins.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialogue.show();
    }

    private void sendBidRequest(int bidAmount, String sellerId) {
        // Get the user's ID from Firebase Auth
        String bidderId = firebaseAuth.getCurrentUser().getUid();

        // Create a new Bid object
        Bid bid = new Bid();
        bid.setBidderId(bidderId);
        bid.setAmount(bidAmount);
        bid.setTimestamp(System.currentTimeMillis());
        bid.setSellerId(sellerId);
        bid.setbookName(bookName);

        bid.setOriginalPrice(Integer.parseInt(bookPrice));

        // Reference to the bids node in Firebase
        DatabaseReference bidsRef = FirebaseDatabase.getInstance().getReference("bids");

        // Push the bid to Firebase
        String bidId = bidsRef.push().getKey();
        bid.setBidId(bidId);
        bidsRef.child(bidId).setValue(bid);

        // Notify the seller about the bid with additional details
        DatabaseReference sellerBidsRef = FirebaseDatabase.getInstance().getReference("users").child(sellerId).child("bids_Notification").child(bidId);
        sellerBidsRef.child("bidderId").setValue(bidderId);
        sellerBidsRef.child("orignalPrice").setValue(bookPrice);
        sellerBidsRef.child("bookName").setValue(bookName);
        sellerBidsRef.child("amount").setValue(bidAmount);
        sellerBidsRef.child("timestamp").setValue(System.currentTimeMillis());
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
