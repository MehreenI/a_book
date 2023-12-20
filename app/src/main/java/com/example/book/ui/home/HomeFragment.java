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

        binding.button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AcademicBook.class);
                startActivity(intent);
            }
        });

        binding.button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GeneralBook.class);
                startActivity(intent);
            }
        });

        GoogleAdMobManager.getInstance().Initialize(getActivity());

        binding.getCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btnShowRewardAd clicked");
                // check if ad is available
                if (GoogleAdMobManager.getInstance().IsRewardedAdAvailable()) {
                    // Show the rewarded ad
                    GoogleAdMobManager.getInstance().ShowRewardedAd(getActivity(), addCoinsCallback);
                } else {
                    Log.d(TAG, "The rewarded ad isn't ready yet.");
                }
            }
        });

        addCoinsCallback = new Runnable() {
            @Override
            public void run() {
                int coinsToAdd = 10;
                String userId = "dummyuser";
                CoinManager coinManager = AppController.getInstance().getManager(CoinManager.class);

                // Add coins to Firebase
                coinManager.addCoinsToFirebase(userId, coinsToAdd);
                updateCoinTextView();

                // Display a toast or perform any other actions
                Log.d(TAG, "give addCoins(" + coinsToAdd + ")");
                Toast.makeText(getActivity(), "Reward Given: +" + coinsToAdd + " coins", Toast.LENGTH_SHORT).show();
            }
        };

        return root;
    }

    private void updateCoinTextView() {
        CoinManager coinManager = AppController.getInstance().getManager(CoinManager.class);
        int currentCoins = coinManager.getTotalCoins();

        // Update the TextView with the current coin count
        // Assuming you have a TextView named coinTextView in your layout
        binding.coin.setText(String.valueOf(currentCoins));
    }
}
