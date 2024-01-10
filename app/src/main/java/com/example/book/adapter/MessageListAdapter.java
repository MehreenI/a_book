package com.example.book.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.AppController;
import com.example.book.activity.ChatActivity;
import com.example.book.activity.MessageListActivity;
import com.example.book.databinding.ItemmodelChatlistBinding;
import com.example.book.manager.FirebaseManager;
import com.example.book.ui.Model.ChatMessage;
import com.example.book.ui.Model.ChatRoom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.CustomViewHolder> {

    private Context context;
    private String username;
    private List<ChatRoom> chatRooms = new ArrayList<>();
    private AppController appController;

    public MessageListAdapter(Context context, String username, List<ChatRoom> chatRooms) {
        this.context = context;
        this.username = username;
        this.chatRooms.addAll(chatRooms);
        appController = AppController.getInstance();

        Log.d(TAG, "MessageListAdapter: chatRooms " + chatRooms + " ");
        for (ChatRoom chatRoom : chatRooms) {
            Log.d(TAG, "MessageListAdapter: chatRoomIds 2 " + chatRoom.getChatroomId() + " ");
            AppController.getInstance().getManager(FirebaseManager.class).showChatRoom(chatRoom.getChatroomId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {
                    Log.d(TAG, "MessageListAdapter: chatRoomIds 3 " + task.getResult().getValue() + " ");
                    if(task.isSuccessful()){
                        ChatRoom chatRoom = fromDataSnapshot(task.getResult());
                        if (chatRoom != null) {
                            chatRooms.add(chatRoom);
                            Log.d(TAG, "MessageListAdapter: task.getResult() " + chatRoom + " " + task.getResult());
                            System.out.println(chatRoom);
                        } else {
                            System.out.println("DataSnapshot does not exist or is invalid.");
                        }
                    }else{
                        Log.d(TAG, "MessageListAdapter: chatRoomIds 4");
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(ItemmodelChatlistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        System.out.println("onBindViewHolder " + position);
        ChatRoom chatRoom  = chatRooms.get(position);
        for (String userid : chatRoom.getUserIds()) {
            if(userid != username){
                holder.binding.txtUsername.setText(userid);
//                try {
//                    String profileImageUrl = appController.getManager(FirebaseManager.class).getUserProfileImg(userid);
//                    Picasso.get().load(profileImageUrl).into(holder.binding.imgUser);
//                    Log.d("ProfileImage", "Received: " + profileImageUrl);
//                } catch (DatabaseException e) {
//                    // Handle errors
//                    Log.e("ProfileImage", "Error: " + e.getMessage());
//                }
            }
        }

        holder.binding.txtLastmessage.setText(chatRoom.getLastMessage());
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppController.getInstance().setChatRoom(chatRoom);

                System.out.println("setOnClickListener chatRoomId " + chatRoom.getChatroomId());
                Intent intent = new Intent(AppController.getInstance().getCurrentActivity(), ChatActivity.class);
                Bundle bundle = new Bundle();
                String chatRoomId = chatRoom.getChatroomId();
                System.out.println("setOnClickListener String chatRoomId " + chatRoomId);

                bundle.putString("ChatRoomId", chatRoomId);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });
    }

    public static ChatRoom fromDataSnapshot(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            String chatroomId = dataSnapshot.getKey();

            Map<String, Object> dataMap = (Map<String, Object>) dataSnapshot.getValue();

            List<String> userIds = (List<String>) dataMap.get("userIds");
            long lastMessageTimestamp = (long) dataMap.get("lastMessageTimestamp");
            String postId = (String) dataMap.get("postId");
            String lastSenderId = (String) dataMap.get("lastSenderId");
            String lastMessage = (String) dataMap.get("lastMessage");

            List<Map<String, Object>> messagesData = (List<Map<String, Object>>) dataMap.get("messages");
            List<ChatMessage> messages = new ArrayList<>();
            if (messagesData != null) {
                for (Map<String, Object> messageData : messagesData) {
                    long timestamp = (long) messageData.get("timestamp");
                    String senderId = (String) messageData.get("senderId");
                    boolean delivered = (boolean) messageData.get("delivered");
                    String messageText = (String) messageData.get("message");
                    boolean sent = (boolean) messageData.get("sent");
                    boolean read = (boolean) messageData.get("read");

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setTimestamp(timestamp);
                    chatMessage.setSenderId(senderId);
                    chatMessage.setDelivered(delivered);
                    chatMessage.setMessage(messageText);
                    chatMessage.setSent(sent);
                    chatMessage.setRead(read);
                    messages.add(chatMessage);
                }
            }
            return new ChatRoom(chatroomId, userIds, messages, lastMessageTimestamp, postId, lastSenderId, lastMessage);
        } else {
            // Handle the case where the DataSnapshot does not exist
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private ItemmodelChatlistBinding binding;

        public CustomViewHolder(ItemmodelChatlistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}