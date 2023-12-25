package com.example.book.manager;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CoinManager extends Manager {

    private final String TAG = "CoinManager";
    private int totalCoins;
    private DatabaseReference databaseReference ;
    FirebaseAuth firebaseAuth;

    public CoinManager() {
        firebaseAuth = FirebaseAuth.getInstance();
        Initialize();
    }

    public CoinManager(String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("coin");
        Initialize();
    }

    public void Initialize() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.child("coin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    totalCoins = dataSnapshot.getValue(Integer.class);
                    Log.e(TAG, "totalCoins: " + totalCoins);
                } else {
                    totalCoins = 5;
                    Log.e(TAG, "totalCoins: " + totalCoins);
                    // If the "coin" node doesn't exist in the database, you can create it here.
                    databaseReference.setValue(totalCoins);
                }
                setInitialized(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                setInitialized(false);
            }
        });
    }

    public void getTotalCoins(CoinFetchCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            userRef.child("coin").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        totalCoins = dataSnapshot.getValue(Integer.class);
                        Log.e(TAG, "totalCoins: 23 " + totalCoins);
                        callback.onCoinsFetched(totalCoins);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error getting total coins: " + error.getMessage());
                }
            });
        }
    }


    public void setTotalCoins(int totalCoins) {
        this.totalCoins = totalCoins;
        if (databaseReference != null) {
            databaseReference.setValue(totalCoins);
        } else {
            Log.e(TAG, "Error: databaseReference is null");
        }
    }


    public void addCoins(int amount) {
        Log.d(TAG, "addCoins: " + amount + " coins");
        totalCoins += amount;
        setTotalCoins(totalCoins);
    }

    public void deductCoinsFromFirebase(int coinsToDeduct) {
        // Check if firebaseAuth is not null before using it
        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            DatabaseReference userCoinsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("coin");

            userCoinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int currentCoins = dataSnapshot.getValue(Integer.class);
                        int newCoinsBalance = currentCoins - coinsToDeduct;
                        userCoinsRef.setValue(newCoinsBalance);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error if needed
                }
            });
        } else {
            // Handle the case where firebaseAuth or currentUser is null
            Log.e("CoinManager", "FirebaseAuth or currentUser is null");
        }
    }

    public void addCoinsToFirebase(String userId, int amount) {
        DatabaseReference userCoinsRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("coin");

        userCoinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int currentCoins = dataSnapshot.exists() ? dataSnapshot.getValue(Integer.class) : 0;

                int newTotalCoins = currentCoins + amount;

                // Update the total coins in Firebase
                userCoinsRef.setValue(newTotalCoins);

                // Optionally, you can update the local totalCoins variable if needed
                totalCoins = newTotalCoins;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors if necessary
            }
        });
    }
}
