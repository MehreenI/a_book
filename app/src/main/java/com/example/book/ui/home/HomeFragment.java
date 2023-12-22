package com.example.book.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.AppController;
import com.example.book.R;
import com.example.book.databinding.FragmentHomeBinding;
import com.example.book.manager.CoinManager;
import com.example.book.ui.Adapter.BookAdapter;
import com.example.customAdsPackage.GoogleAdMobManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private Activity activity;
    private final String TAG = "HomeFragment";
    private Runnable addCoinsCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set up the adapter
        BookAdapter bookAdapter = new BookAdapter(new ArrayList<>()); // Pass an empty list initially
        recyclerView.setAdapter(bookAdapter);

        // Observe the LiveData from ViewModel and update UI when data changes
        homeViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            // Update RecyclerView adapter with the new data
            bookAdapter.setData(posts);
        });

        binding.button5.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AcademicBook.class);
            startActivity(intent);
        });

        binding.button6.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GeneralBook.class);
            startActivity(intent);
        });

        GoogleAdMobManager.getInstance().Initialize(getActivity());

        binding.getCoin.setOnClickListener(view -> {
            Log.d(TAG, "btnShowRewardAd clicked");

            if (GoogleAdMobManager.getInstance().IsRewardedAdAvailable()) {
                // Show the rewarded ad
                GoogleAdMobManager.getInstance().ShowRewardedAd(getActivity(), addCoinsCallback);
            } else {
                Log.d(TAG, "The rewarded ad isn't ready yet.");
            }
        });

        addCoinsCallback = () -> {
            // Increment coins in Firebase
            int coinsToAdd = 10; // or any other amount you want to reward

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                CoinManager coinManager = AppController.getInstance().getManager(CoinManager.class);

                // Add coins to Firebase
                coinManager.addCoinsToFirebase(userId, coinsToAdd);

                // Update the UI or perform any other actions
                updateCoinTextView(userId);

                // Display a toast or perform any other actions
                Log.d(TAG, "Added " + coinsToAdd + " coins to user: " + userId);
                Toast.makeText(getActivity(), "Reward Given: +" + coinsToAdd + " coins", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "User is not logged in");
                // Handle the case where the user is not logged in
            }
        };

        // Fetch and display user's coin information
        fetchUserCoinsAndDisplay();

        return root;
    }

    private void fetchUserCoinsAndDisplay() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            updateCoinTextView(userId);
        }
    }

    private void updateCoinTextView(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.child("coin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int userCoins = dataSnapshot.getValue(Integer.class);
                    binding.coin.setText(String.valueOf(userCoins));
                } else {
                    Log.e(TAG, "User coins data not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user coins: " + databaseError.getMessage());
            }
        });
    }
}
