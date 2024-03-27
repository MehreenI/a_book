package com.example.book;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.book.manager.CoinFetchCallback;
import com.example.book.manager.CoinManager;
import com.example.book.manager.FirebaseManager;
import com.example.book.manager.Manager;
import com.example.book.manager.UserManager;
import com.example.book.ui.Model.ChatMessage;
import com.example.book.ui.Model.ChatRoom;
import com.example.book.ui.Model.User;
import com.example.book.ui.signin.loginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AppController {

    //region Singleton
    private static AppController instance;
    public static synchronized AppController getInstance() {
        if (instance == null) {
            instance = new AppController();
        }
        return instance;
    }
    //endregion Singleton

    //region Attributes
    private final String TAG = "AppController";
    private ChatRoom chatRoom;
    private User user;
    private List<String> chatroomIds = new ArrayList<>();
    private Set<ChatRoom> chatRooms = new HashSet<>();
    private Activity currentActivity;
    //endregion Attributes

    //region Initialization
    
    private AppController() {
        Log.d("AppController", "Constructor Created: ");
    
        addManager(FirebaseManager.class, new FirebaseManager());
        addManager(CoinManager.class, new CoinManager());
        addManager(UserManager.class, new UserManager());
    }
    public void initialize() {
        getManager(UserManager.class).Initialize();
        getManager(FirebaseManager.class).Initialize();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("AppController", "Starting LoadChatRooms: ");
                AppController.getInstance().LoadChatRooms();
            }
        }, 3000);
    }
    //endregion Initialization
    private Map<Class<?>, Manager> managerMap = new HashMap<>();

    private void addManager(Class<?> managerClass, Manager manager) {
        managerMap.put(managerClass, manager);
    }
    @SuppressWarnings("unchecked")
    public <T extends Manager> T getManager(Class<T> managerClass) {
        return (T) managerMap.get(managerClass);
    }

    public ContentResolver getContentResolver(Context context) {
        return context.getContentResolver();
    }


    //region Methods
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
    //endregion Methods

    //region Extras
    public static String convertTimestampToDateTime(long timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = new Date(timestamp);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error converting timestamp";
        }
    }
    public static String getRelativeTime(long timestamp) {
        try {
            long now = getCurrentTimestamp();
            long differenceMillis = now - timestamp;

            // Convert milliseconds to minutes, hours, and days
            long differenceMinutes = differenceMillis / (60 * 1000);
            long differenceHours = differenceMillis / (60 * 60 * 1000);
            long differenceDays = differenceMillis / (24 * 60 * 60 * 1000);

            if (differenceMinutes < 1) {
                return "just now";
            } else if (differenceMinutes == 1) {
                return "a minute ago";
            } else if (differenceHours < 1) {
                return differenceMinutes + " minutes ago";
            } else if (differenceHours == 1) {
                return "an hour ago";
            } else if (differenceDays < 1) {
                return differenceHours + " hours ago";
            } else if (differenceDays == 1) {
                return "yesterday";
            } else {
                // Format the date and time for older posts
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date date = new Date(timestamp);
                return sdf.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calculating relative time";
        }
    }

    public void FillChatRoomsList(){
        chatRooms.clear();
        for (String chatRoomId : chatroomIds) {
            getManager(FirebaseManager.class).showChatRoom(chatRoomId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {
                    Log.d(TAG, "MessageListAdapter: chatRoomIds " + task.getResult().getValue() + " ");
                    if(task.isSuccessful()){
                        ChatRoom chatRoom = fromDataSnapshot(task.getResult());
                        if (chatRoom != null) {
                            if (!chatRooms.contains(chatRoom)){
                                chatRooms.add(chatRoom);
                                Log.d(TAG, "MessageListAdapter: task.getResult() " + chatRoom + " " + task.getResult());
                            }
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
    public void LoadChatRooms(){
        chatroomIds.clear();
        try {
            String username = AppController.getInstance().getManager(UserManager.class).getUser().getUsername();
            Log.d(TAG, "LoadChatRooms: username: " + username);
            AppController.getInstance().getManager(FirebaseManager.class).showChatList(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onCreate: task LoadChatRooms " + task.getResult());
                        for (DataSnapshot chatRoomSnapshot : task.getResult().getChildren()) {
                            if (!chatroomIds.contains(chatRoomSnapshot.getValue().toString())){
                                chatroomIds.add(chatRoomSnapshot.getValue().toString());
                                Log.d(TAG, "LoadChatRooms: adding chatroom " + chatroomIds + " " + task.getResult());
                            }
                        }
                        FillChatRoomsList();
                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "LoadChatRooms " + e);
        }
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

    public void saveLoginPrefs(String email, String userPassword){
        SharedPreferences sharedPreferences = currentActivity.getSharedPreferences("oldBook", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("userLoggedIn", true);
        editor.putString("email", email);
        editor.putString("password", userPassword);
        editor.apply();
    }
    public void resetLoginPrefs(){
        SharedPreferences sharedPreferences = currentActivity.getSharedPreferences("oldBook", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("userLoggedIn", false);
        editor.putString("email", "");
        editor.putString("password", "");
        editor.apply();
    }
    public Boolean loadLoginPrefs(){
        SharedPreferences sharedPreferences = currentActivity.getSharedPreferences("oldBook", Context.MODE_PRIVATE);
        Boolean userLoggedIn = sharedPreferences.getBoolean("userLoggedIn", false);
        Log.d("loadLoginPrefs", "loadLoginPrefs: 1");
        if(userLoggedIn){
            Log.d("loadLoginPrefs", "loadLoginPrefs: 2");

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            String email = sharedPreferences.getString("email", "");
            String userPassword = sharedPreferences.getString("password", "");
    
            Log.d("loadLoginPrefs", "email: " + email);
            Log.d("loadLoginPrefs", "userPassword: " + userPassword);
            User user = new User();
            user.setUsername(email);
            AppController.getInstance().getManager(UserManager.class).setUserLoggedIn(user);
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, userPassword)
                    .addOnCompleteListener(currentActivity, task -> {
                        if (task.isSuccessful()) {

                            Log.d("loadLoginPrefs", "loadLoginPrefs: 3");
                            AppController.getInstance().saveLoginPrefs(email,userPassword);
                            String userId = firebaseAuth.getCurrentUser().getUid();
                            Log.d("loadLoginPrefs", "loadLoginPrefs: 3 " + userId);
                            user.setUsername(userId);
                            user.setEmail(email);
    
                            AppController.getInstance().getManager(UserManager.class).setUserLoggedIn(user);
    
                            fetchUserCoinsFromFirebase(userId, new CoinFetchCallback() {
                                public void onCoinsFetched(int userCoins) {

                                    Log.d("loadLoginPrefs", "loadLoginPrefs: 4 coins: " + userCoins);
                                    user.setCoin(userCoins);
                                    AppController.getInstance().getManager(UserManager.class).setUserLoggedIn(user);

                                    AppController.getInstance().setUser(user);
                                }
                            });
                        } else {
                            Exception exception = task.getException();
    
                            Log.d("loadLoginPrefs", "exception 5: " + exception);
                        }
                    });
        }
        return userLoggedIn;
    }
    private void fetchUserCoinsFromFirebase(String userId, CoinFetchCallback callback) {
        final int[] userCoins = {0};

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(userId);

        userRef.child("coin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userCoins[0] = dataSnapshot.getValue(Integer.class);
                    callback.onCoinsFetched(userCoins[0]);
                } else {
                    Log.e("Firebase", "User coins data not found");
                    callback.onCoinsFetched(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
                Log.e("Firebase", "Error fetching user coins: " + databaseError.getMessage());
                callback.onCoinsFetched(0); // Default value
            }
        });
    }
    //endregion Extras

    //region Getter/Setter
    public Activity getCurrentActivity() {
        return currentActivity;
    }
    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }
    public ChatRoom getChatRoom() {
        return chatRoom;
    }
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
    public Set<ChatRoom> getChatRooms() {
        return chatRooms;
    }
    public void setChatRooms(Set<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }
    public List<String> getChatroomIds() {
        return chatroomIds;
    }
    public void setChatroomIds(List<String> chatroomIds) {
        this.chatroomIds = chatroomIds;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    //endregion Getter/Setter
}