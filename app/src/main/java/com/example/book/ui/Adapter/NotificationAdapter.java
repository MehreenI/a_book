package com.example.book.ui.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.AppController;
import com.example.book.R;
import com.example.book.manager.FirebaseManager;
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
        private TextView bookPrice;
        private TextView bidAmount;

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
                    userName.setText(bookName);
                }

                @Override
                public void onUserNameFetchFailed(String errorMessage) {
                    userName.setText("Book Name: Not Found");
                }
            });
            if(notification.getbookName() != null){
                bookName.setText(notification.getbookName());
            }
            else{
                bookName.setText("BookName");
            }

            itemView.setOnClickListener(view -> {
                showCustomDialog(view.getContext(),
                        "Select a choice",
                        "continue the conversation or decline offer",
                        "Conversation",
                        "Decline", notification);
            });
        }

        public void showCustomDialog(Context context, String title, String message,
                                     String positiveButtonText, String negativeButtonText, Bid notification) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);

            builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(context, "Conversation Start", Toast.LENGTH_SHORT).show();
                    //AppController.getInstance().getManager(FirebaseManager.class).isChatRoomCreated(, notification.getBidderId());
                }
            });

            builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(context, "Decline offer", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
