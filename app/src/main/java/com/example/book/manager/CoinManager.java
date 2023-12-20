package com.example.book.manager;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CoinManager extends Manager{

    //region Attributes
    private final String TAG = "CoinManager";
    private int totalCoins;
    private DatabaseReference databaseReference;
    //endregion Attributes

    //region Singleton
    //endregion Singleton


    public CoinManager() {
    }
    public CoinManager(String userId) {
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("totalCoins");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child("dummyuser").child("totalCoins");
        Initialize();
    }

    @Override
    public void Initialize() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    totalCoins = dataSnapshot.getValue(Integer.class);
                } else {
                    totalCoins = 0;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        setInitialized(true);
    }

    public int getTotalCoins()
    {
        return totalCoins;
    }

    public void setTotalCoins(int totalCoins) {
        this.totalCoins = totalCoins;
        databaseReference.setValue(totalCoins);
    }

    public void addCoins(int amount) {
        Log.d(TAG, "addCoins: 5 coins");
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
        DatabaseReference userCoinsRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("totalCoins");

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