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

import com.example.book.GoogleAdMobManager;
import com.example.book.R;
import com.example.book.databinding.FragmentHomeBinding;
import com.example.book.ui.Adapter.BookAdapter;
import com.example.book.ui.Model.Post;
import com.example.book.ui.bookdetail.BookDetailActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
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
    private RewardedAd mRewarded;
    private RewardedAd rewardedAd;


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

        bookAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Post clickedBook = bookAdapter.getItem(position);
                openBookDetailActivity(clickedBook);
            }
        });



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
//        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
//                Log.d(TAG,"On Initialization Completed"+initializationStatus.toString());
//            }
//        });
//        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
//            }
//        });
        // Load the rewarded ad
        loadRewardedAd();

        // Show the rewarded ad when a button is clicked, for example
        binding.getCoin.setOnClickListener(v -> GoogleAdMobManager.getInstance().ShowRewardedAd(getActivity(), new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "ShowRewardedAd", Toast.LENGTH_SHORT).show();
            }
        }));
        // Fetch and display user's coin information
        fetchUserCoinsAndDisplay();

        return root;
    }

    private void openBookDetailActivity(Post book) {
        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
        intent.putExtra("bookName", book.getBookName());
        intent.putExtra("bookPrice", book.getBookPrice());
        intent.putExtra("imageUrl", book.getImageUrl());
        intent.putExtra("description", book.getDescription());
        startActivity(intent);
    }

    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(getActivity(), "ca-app-pub-3940256099942544/5224354917", adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
                Toast.makeText(getActivity(), "Rewarded ad loaded successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Toast.makeText(getActivity(), "Rewarded ad failed to load", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd.show(getActivity(), rewardItem -> {
                // User earned reward, handle accordingly
                int rewardAmount = rewardItem.getAmount();
                String rewardType = rewardItem.getType();
                Toast.makeText(getActivity(), "Earned " + rewardAmount + " " + rewardType, Toast.LENGTH_SHORT).show();
                loadRewardedAd();
            });
        } else {
            Toast.makeText(getActivity(), "Rewarded ad not loaded yet", Toast.LENGTH_SHORT).show();
        }
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