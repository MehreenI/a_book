package com.example.book.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.book.AppController;
import com.example.book.adapter.ChatMessageAdapter;
import com.example.book.databinding.ActivityChatBinding;
import com.example.book.manager.FirebaseManager;
import com.example.book.manager.UserManager;
import com.example.book.ui.Model.ChatMessage;
import com.example.book.ui.Model.ChatRoom;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding actBinding;
    private Activity activity;
    private final String TAG = "ChatActivity";
    private ChatMessageAdapter chatMessageAdapter;
    private String chatRoomId;
    private ChatRoom chatRoom;
    private DatabaseReference DBChatRoomPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(actBinding.getRoot());
        activity = this;
        AppController.getInstance().setCurrentActivity(activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Chat"); // Set the title
        }

        chatRoom = AppController.getInstance().getChatRoom();
        Intent intent = getIntent();
        if (intent != null) {
            chatRoomId = intent.getStringExtra("ChatRoomId");
            if (chatRoomId == null) {
                chatRoomId = chatRoom.getChatroomId();
            }
        }
        Log.d("ChatActivity", "onCreate: chatRoomId " + chatRoomId);

        // Initialize Firebase database reference
        DBChatRoomPath = FirebaseDatabase.getInstance().getReference("chatroom");

        // Get the current user's username
        UserManager userManager = AppController.getInstance().getManager(UserManager.class);
        if (userManager != null && userManager.getUser() != null) {
            String username = userManager.getUser().getUsername();

            // Build FirebaseRecyclerOptions
            FirebaseRecyclerOptions<ChatMessage> options =
                    new FirebaseRecyclerOptions.Builder<ChatMessage>()
                            .setQuery(DBChatRoomPath.child(chatRoomId).child("messages"), ChatMessage.class)
                            .build();

            // Create the adapter
            chatMessageAdapter = new ChatMessageAdapter(username, options);

            // Set up RecyclerView
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            actBinding.recChat.setLayoutManager(linearLayoutManager);
            actBinding.recChat.setAdapter(chatMessageAdapter);

            // Send button click listener
            actBinding.btnSend.setOnClickListener(v -> {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setMessage(actBinding.txtMessage.getText().toString());
                Log.d("ChatActivity", "onCreate: chatMessage " + chatMessage);
                AppController.getInstance().getManager(FirebaseManager.class).sendChatMessage(chatRoom, chatMessage);
                actBinding.txtMessage.setText("");
                chatMessageAdapter.notifyDataSetChanged();
                linearLayoutManager.scrollToPosition(chatMessageAdapter.getItemCount());
            });
        } else {
            // Handle case where UserManager or User is null
            Log.e(TAG, "UserManager or User is null");
            // You might want to display an error message or handle this case accordingly
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (chatMessageAdapter != null) {
            chatMessageAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (chatMessageAdapter != null) {
            chatMessageAdapter.stopListening();
        }
    }
}
