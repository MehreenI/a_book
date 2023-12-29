package com.example.book.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.R;
import com.example.book.ui.Adapter.NotificationAdapter;
import com.example.book.ui.Model.Bid;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    FirebaseAuth sellerId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = root.findViewById(R.id.recycler_view);
        notificationAdapter = new NotificationAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(notificationAdapter);

        // Load and set your notification data from Firebase
        getNotificationData();

        return root;
    }

    private void getNotificationData() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            // User is logged in
            String currentUserId = firebaseAuth.getCurrentUser().getUid();

            DatabaseReference bidsRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentUserId)
                    .child("bids_Notification");

            bidsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Bid> notificationList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Bid bid = snapshot.getValue(Bid.class);
                        notificationList.add(bid);
                    }

                    // Reverse the list to show the latest bids at the top
                    List<Bid> reversedList = new ArrayList<>(notificationList);
                    Collections.reverse(reversedList);

                    notificationAdapter.setNotificationList(reversedList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error
                }
            });
        } else {
            // User is not logged in, or there are no notifications
            List<Bid> emptyList = Collections.emptyList();
            notificationAdapter.setNotificationList(emptyList);
        }
    }
}
