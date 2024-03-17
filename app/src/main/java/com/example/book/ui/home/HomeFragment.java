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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.R;
import com.example.book.databinding.FragmentHomeBinding;
import com.example.book.ui.Adapter.BookAdapter;
import com.example.book.ui.Model.Post;
import com.example.book.ui.bookdetail.BookDetailActivity;
import com.example.book.ui.extra.Enums;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
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
import java.util.Collections;
import java.util.List;
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Activity activity;
    private final String TAG = "HomeFragment";

    private RewardedAd rewardedAd;

    private BookAdapter bookAdapter;
    private List<Post> allBooks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.featured_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set up the adapter
        bookAdapter = new BookAdapter(new ArrayList<>());
        recyclerView.setAdapter(bookAdapter);

        // Load data from Firebase
        loadDataFromFirebase();

        bookAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Post clickedBook = bookAdapter.getItem(position);
            }

            @Override
            public void addFavourite(int position) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (userId != null) {
                    DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("favorites");

                    Post selectedBook = bookAdapter.getItem(position);

                    String favoriteId = favoritesRef.push().getKey();
                    favoritesRef.child(favoriteId).setValue(selectedBook);
                    favoritesRef.child(favoriteId).child("userId").setValue(userId);
                    Toast.makeText(getActivity(), "Book added to favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.button5.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AcademicBook.class);
            startActivity(intent);
        });

        binding.button6.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GeneralBook.class);
            startActivity(intent);
        });

        MobileAds.initialize(getActivity());

        loadRewardedAd();

        // Show the rewarded ad when a button is clicked, for example
        binding.getCoin.setOnClickListener(v ->{
            if (rewardedAd != null) {
                showRewardedAd();
            } else {
                Toast.makeText(getActivity(), "Ad not loaded yet. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // Search functionality
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });

        // Fetch and display user's coin information
        fetchUserCoinsAndDisplay();

        return root;
    }

    private void loadDataFromFirebase() {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("uploads");

        postsRef.orderByChild("timestamp")
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
                        bookAdapter.setData(allBooks);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Error fetching featured posts: " + error.getMessage());
                    }
                });
    }

    private void filterBooks(String query) {
        List<Post> filteredBooks = new ArrayList<>();

        for (Post book : allBooks) {
            if (isBookMatch(book, query)) {
                filteredBooks.add(book);
            }
        }

        bookAdapter.setData(filteredBooks);
    }

    private boolean isBookMatch(Post book, String query) {
        String bookName = book.getBookName().toLowerCase();
        String authors = book.getAuthors().toString().toLowerCase();

        return bookName.contains(query.toLowerCase()) || authors.contains(query.toLowerCase());
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

        RewardedAd.load(getActivity(), getString(R.string.rewarded_ad_unit_id), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
                Toast.makeText(getActivity(), "Ad loaded successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Toast.makeText(getActivity(), "Ad failed to load: " + loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd.show(getActivity(), rewardItem -> {
                int rewardAmount = rewardItem.getAmount();
                String rewardType = rewardItem.getType();
                Toast.makeText(getActivity(), "Earned " + rewardAmount + " " + rewardType, Toast.LENGTH_SHORT).show();
                loadRewardedAd(); // Load a new rewarded ad after showing
            });
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

//public class HomeFragment extends Fragment {
//
//    private FragmentHomeBinding binding;
//    private Activity activity;
//    private final String TAG = "HomeFragment";
//
//    private RewardedAd rewardedAd;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        // Set up RecyclerView
//        RecyclerView recyclerView = root.findViewById(R.id.featured_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//        // Set up the adapter
//        BookAdapter bookAdapter = new BookAdapter(new ArrayList<>()); // Pass an empty list initially
//        recyclerView.setAdapter(bookAdapter);
//
//        // Load data from Firebase
//        loadDataFromFirebase(bookAdapter);
//
//        bookAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Post clickedBook = bookAdapter.getItem(position);
//                openBookDetailActivity(clickedBook);
//            }
//
//            @Override
//            public void addFavourite(int position) {
//                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                if (userId != null) {
//                    DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("favorites");
//
//                    Post selectedBook = bookAdapter.getItem(position);
//
//                    String favoriteId = favoritesRef.push().getKey();
//                    favoritesRef.child(favoriteId).setValue(selectedBook);
//                    favoritesRef.child(favoriteId).child("userId").setValue(userId);
//                    Toast.makeText(getActivity(), "Book added to favorites", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        binding.button5.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), AcademicBook.class);
//            startActivity(intent);
//        });
//
//        binding.button6.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), GeneralBook.class);
//            startActivity(intent);
//        });
//
//        MobileAds.initialize(getActivity());
//
//        loadRewardedAd();
//
//        // Show the rewarded ad when a button is clicked, for example
//        binding.getCoin.setOnClickListener(v ->{
//        if (rewardedAd != null) {
//            showRewardedAd();
//        } else {
//            Toast.makeText(getActivity(), "Ad not loaded yet. Try again.", Toast.LENGTH_SHORT).show();
//        }
//    });
//        // Fetch and display user's coin information
//        fetchUserCoinsAndDisplay();
//        return root;
//    }
//
//    private void loadDataFromFirebase(BookAdapter bookAdapter) {
//        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("uploads");
//
//        postsRef.orderByChild("postType").equalTo(Enums.PostType.FEATURED.toString())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        List<Post> featuredPosts = new ArrayList<>();
//                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                            Post post = postSnapshot.getValue(Post.class);
//                            if (post != null) {
//                                featuredPosts.add(post);
//                            }
//                        }
//
//                        Collections.reverse(featuredPosts);
//
//                        // Update RecyclerView adapter with the new data
//                        bookAdapter.setData(featuredPosts);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError error) {
//                        Log.e(TAG, "Error fetching featured posts: " + error.getMessage());
//                    }
//                });
//    }
//
//    private void openBookDetailActivity(Post book) {
//        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
//        intent.putExtra("bookName", book.getBookName());
//        intent.putExtra("bookPrice", book.getBookPrice());
//        intent.putExtra("imageUrl", book.getImageUrl());
//        intent.putExtra("description", book.getDescription());
//        startActivity(intent);
//    }
//
//    private void loadRewardedAd() {
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        RewardedAd.load(getActivity(), getString(R.string.rewarded_ad_unit_id), adRequest, new RewardedAdLoadCallback() {
//            @Override
//            public void onAdLoaded(@NonNull RewardedAd ad) {
//                rewardedAd = ad;
//                Toast.makeText(getActivity(), "Ad loaded successfully", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                Toast.makeText(getActivity(), "Ad failed to load: " + loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void showRewardedAd() {
//        if (rewardedAd != null) {
//            rewardedAd.show(getActivity(), rewardItem -> {
//                // User earned a reward
//                int rewardAmount = rewardItem.getAmount();
//                String rewardType = rewardItem.getType();
//                Toast.makeText(getActivity(), "Earned " + rewardAmount + " " + rewardType, Toast.LENGTH_SHORT).show();
//                loadRewardedAd(); // Load a new rewarded ad after showing
//            });
//        }
//    }
//
//    private void fetchUserCoinsAndDisplay() {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String userId = currentUser.getUid();
//            updateCoinTextView(userId);
//        }
//    }
//
//    private void updateCoinTextView(String userId) {
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
//
//        userRef.child("coin").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    int userCoins = dataSnapshot.getValue(Integer.class);
//                    binding.coin.setText(String.valueOf(userCoins));
//                } else {
//                    Log.e(TAG, "User coins data not found");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "Error fetching user coins: " + databaseError.getMessage());
//            }
//        });
//    }
//}
//
