package com.example.book.manager;

import android.util.Log;

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
    private DatabaseReference databaseReference;

    public CoinManager() {
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

    public int getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(int totalCoins) {
        this.totalCoins = totalCoins;
        databaseReference.setValue(totalCoins);
    }

    public void addCoins(int amount) {
        Log.d(TAG, "addCoins: " + amount + " coins");
        totalCoins += amount;
        setTotalCoins(totalCoins);
    }

    public void deductCoins(int amount) {
        if (totalCoins >= amount) {
            totalCoins -= amount;
            setTotalCoins(totalCoins);
        } else {
            // Handle the case where the user doesn't have enough coins.
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
