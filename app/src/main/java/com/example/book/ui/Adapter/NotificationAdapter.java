package com.example.book.ui.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.R;
import com.example.book.manager.UserRepository;
import com.example.book.ui.Model.Bid;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Bid> notificationList;

    public void setNotificationList(List<Bid> notifications) {
        notificationList = notifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Bid notification = notificationList.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private TextView bookName;
        private TextView userName;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            bookName = itemView.findViewById(R.id.bookname);
            userName = itemView.findViewById(R.id.UserName);
        }

        void bind(Bid notification) {
            text.setText("A Notification from buyer");

            UserRepository.getInstance().getBookNameById(notification.getBidderId(), new UserRepository.UserNameFetchCallback() {
                @Override
                public void onUserNameFetched(String bookName) {
                    userName.setText("Book Name: " + bookName);
                }

                @Override
                public void onUserNameFetchFailed(String errorMessage) {
                    userName.setText("Book Name: Not Found");
                }
            });

            bookName.setText("Book Name");  // Replace with actual username
        }
    }
}
