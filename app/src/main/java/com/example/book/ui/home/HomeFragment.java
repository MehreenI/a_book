package com.example.book.ui.home;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.book.R;
import com.example.book.databinding.FragmentDashboardBinding;
import com.example.book.ui.Adapter.BookAdapter;
import com.example.book.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

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
//                Toast.makeText(getActivity(), "Academic Button is Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), AcademicBook.class);
                startActivity(intent);
            }
        });
        binding.button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Academic Button is Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), GeneralBook.class);
                startActivity(intent);
            }
        });
        return root;
    }



}
