package com.example.book.manager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.book.AppController;
import com.example.book.MainActivity;
import com.example.book.activity.ChatActivity;
import com.example.book.ui.Model.Bid;
import com.example.book.ui.Model.ChatMessage;
import com.example.book.ui.Model.ChatRoom;
import com.example.book.ui.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;

public class FirebaseManager extends Manager {

    public DatabaseReference DBPostPath = FirebaseDatabase.getInstance().getReference("post");
    public DatabaseReference DBUserPath = FirebaseDatabase.getInstance().getReference("user");
    public DatabaseReference DBChatRoomPath = FirebaseDatabase.getInstance().getReference("chatroom");

    @Override
    public void Initialize() {
        DBPostPath.keepSynced(true);
        DBUserPath.keepSynced(true);
        DBChatRoomPath.keepSynced(true);
    }

    public Query onlyFeaturedPosts() {
        return DBPostPath.orderByChild("featured").equalTo(true);
    }

    public String getUserProfileImg(String username) throws DatabaseException {
        try {
            DataSnapshot dataSnapshot = DBUserPath.child(username).child("profileImg").get().getResult();
            if (dataSnapshot.exists()) {
                return dataSnapshot.getValue(String.class);
            }
        } catch (DatabaseException e) {}
        return "";
    }
    public Query getPostsFromCountry(String country) {
        return DBPostPath.orderByChild("country").equalTo(country);
    }

    public Query getUsersWithRole(String role) {
        return DBUserPath.orderByChild("role").equalTo(role);
    }

    public Query showChatMessage(String chatRoomId) {
        return DBChatRoomPath.child(chatRoomId).child("messages");
    }
    public Query showChatList(String username) {
        return FirebaseDatabase.getInstance().getReference("user").child(username).child("chatroomId");
    }
    public Query showChatRoom(String chatroomId) {
        return DBChatRoomPath.child(chatroomId);
    }
    public void generateChatList(String username) {
        List<String> usernames = new ArrayList<>();
        usernames.add(username);
        Log.d("firebase", "usernames.get(0): " + usernames.toString());
        DBChatRoomPath.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    Log.d("firebase", "generateChatList: " + task.getResult());
                }
            }
        });
    }
    public void startChat(String postId, String bidderId) {
        ChatRoom chatRoom = new ChatRoom();

        String chatRoomId = DBChatRoomPath.push().getKey();
        chatRoom.setChatroomId(chatRoomId);

        List<String> userIds = new ArrayList<>();
        userIds.add(AppController.getInstance().getManager(UserManager.class).getUser().getUsername());
        userIds.add(bidderId);
        chatRoom.setUserIds(userIds);

        List<ChatMessage> messages = new ArrayList<>();
        chatRoom.setMessages(messages);

        chatRoom.setPostId(postId);

        String username = AppController.getInstance().getManager(UserManager.class).getUser().getUsername();

        DBChatRoomPath.child(chatRoomId).setValue(chatRoom).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DBUserPath.child(username).child("chatroomId").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                List<String> chatroomIds = new ArrayList<>();
                                for (DataSnapshot chatRoomSnapshot : task.getResult().getChildren()) {
                                    chatroomIds.add(chatRoomSnapshot.getValue().toString());
                                }
                                chatroomIds.add(chatRoomId);
                                DBUserPath.child(username).child("chatroomId").setValue(chatroomIds).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            DBUserPath.child(bidderId).child("chatroomId").setValue(chatroomIds).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        openExistingChatRoom(chatRoom);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
    public void isChatRoomCreated(String postId, String bidderId) {
        DatabaseReference chatRoomsRef = DBChatRoomPath;

        Query query = chatRoomsRef.orderByChild("postId").equalTo(postId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d("isChatRoomCreated", "dataSnapshot.getChildren(): " + dataSnapshot.getChildrenCount());
                    Log.d("isChatRoomCreated", "dataSnapshot.getChildren().toString(): " + dataSnapshot.getChildren().toString());
                    dataSnapshot.getChildren().toString();
                    // dataSnapshot.getValue() returns a Map<String, Object> representing the JSON structure
                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();

                    // Now 'data' contains your JSON object
                    // You can access specific fields like data.get("fieldName")

                    // Example: Print the JSON object
                    Log.d("FirebaseData", "Data: " + data.toString());
                }

                for (DataSnapshot chatRoomSnapshot : dataSnapshot.getChildren()) {
                    ChatRoom chatRoom = chatRoomSnapshot.getValue(ChatRoom.class);

                    if (chatRoom != null && chatRoom.getUserIds() != null && chatRoom.getUserIds().contains(bidderId)) {
                        openExistingChatRoom(chatRoom);
                        return;
                    }
                }
                startChat(postId, bidderId);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void openExistingChatRoom(ChatRoom chatRoom) {
        AppController.getInstance().setChatRoom(chatRoom);
        AppController.getInstance().getCurrentActivity().startActivity(new Intent(AppController.getInstance().getCurrentActivity(), ChatActivity.class));
    }
    public void sendChatMessage(ChatRoom chatRoom, ChatMessage message) {
        DatabaseReference chatRoomRef = DBChatRoomPath.child(chatRoom.getChatroomId());

        chatRoom.setLastMessage(message.getMessage());
        chatRoom.setLastSenderId(message.getSenderId());
        chatRoom.setLastMessageTimestamp(message.getTimestamp());

        List<ChatMessage> messages = new ArrayList<>();
        messages.addAll(chatRoom.getMessages());
        messages.add(message);
        message.setSent(true);

        chatRoom.setMessages(messages);

        chatRoomRef.setValue(chatRoom).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    message.setSent(true);
//                    messagesRef.setValue(message);
//                }
            }
        });
    }

    List<String> favPostIds = new ArrayList<>();
    public List<String> getFavPosts() {
        DBUserPath.child("favPostId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    favPostIds = (List<String>) dataSnapshot.getValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
        return favPostIds;
    }

    public void setAsFavPost(String postId){
        getFavPosts();
        if (favPostIds == null || !favPostIds.contains(postId)) {
            if (favPostIds == null) {
                favPostIds = new ArrayList<>();
            }
            favPostIds.add(postId);
            DBUserPath.child("favPostId").setValue(favPostIds);
        }
    }

    public void removeFromFavPost(String postId){
        getFavPosts();
        if (favPostIds != null) {
            if (favPostIds.contains(postId)) {
                favPostIds.remove(postId);
                DBUserPath.child("favPostId").setValue(favPostIds);
            }
        }
    }
    public void placeBid(Context context, String postId, List<Bid> updatedBids){
        DBPostPath.child(postId).child("bids").setValue(updatedBids)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Post bid updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to update post bid", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void addNewUser(Context context, @NonNull User newUser){
        DBUserPath.child(newUser.getUsername()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "User Added into the database", Toast.LENGTH_SHORT).show();
                    loginUser(context, newUser);
                }
            }
        });
    }
    public void loginUser(Context context, @NonNull User loggedUser){
        AppController.getInstance().getManager(UserManager.class).setUserLoggedIn(loggedUser);
        context.startActivity(new Intent(context, MainActivity.class));
    }
//    public boolean verifySignupCredential(Context context, @NonNull String userId) {
//        Task<Boolean> doesUserExistTask = doesUserExist(userId);
//        return Tasks.whenAllComplete(doesUserExist(userId),getUserFromDatabase(userId))
//                .continueWith(task -> {
//                    if (task.isSuccessful()) {
//                        boolean userExists = doesUserExistTask.getResult();
//                        Toast.makeText(context, "UserName already exist", Toast.LENGTH_SHORT).show();
//                        return userExists;
//                    } else {
//                        Toast.makeText(context, "UserName Credential not exist", Toast.LENGTH_SHORT).show();
//                        throw task.getException();
//                    }
//                }).getResult();
//    }
//    public boolean verifyLoginCredential(Context context, @NonNull String username) {
//        Task<User> getUserTask = getUserFromDatabase(username);
//        Task<Boolean> doesUserExistTask = doesUserExist(username);
//        return Tasks.whenAllComplete(doesUserExist(username),getUserFromDatabase(username))
//                .continueWith(task -> {
//                    if (task.isSuccessful()) {
//                        User user = getUserTask.getResult();
//                        boolean userExists = doesUserExistTask.getResult();
//                        Toast.makeText(context, "Login Credential Verified", Toast.LENGTH_SHORT).show();
//                        return userExists && user != null;
//                    } else {
//                        Toast.makeText(context, "Login Credential not Verified", Toast.LENGTH_SHORT).show();
//                        throw task.getException();
//                    }
//                }).getResult();
//    }
    public interface OnUserFetchListener {
        void onUserFetchSuccess(User user);
        void onUserFetchFailure(String errorMessage);
    }
    public void getUserFromDatabase(String username, final OnUserFetchListener listener) {
        Log.d("Firebase", "getUserFromDatabase: " + username);
        DBUserPath.child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d("Firebase", "onComplete: " + username);
                    if (task.getResult().exists()) {

                        Log.d("Firebase", "onComplete: dataSnapshot " + task.getResult());
                        User user = task.getResult().getValue(User.class);
                        Log.d("Firebase", "onComplete: dataSnapshot user " + user.getUsername());
                        if (user != null) {
                            listener.onUserFetchSuccess(user);
                        } else {
                            listener.onUserFetchFailure("User data is null");
                        }
                    } else {
                        listener.onUserFetchFailure("User not found");
                    }
                }
            }
        });
    }
    public Task<Boolean> doesUserExist(String userId) {
        return DBUserPath.child(userId).get().continueWith(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                return dataSnapshot.exists();
            } else {
                throw task.getException();
            }
        });
    }
}
