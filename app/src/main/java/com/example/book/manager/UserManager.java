package com.example.book.manager;


import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.book.AppController;
import com.example.book.ui.Model.User;
import com.example.book.ui.extra.StringConst;

public class UserManager extends Manager {


    //region Attributes
    private boolean initialized;
    private User user;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    //endregion Attributes

    //region Singleton
    //endregion Singleton

    //region Methods
    @Override
    public void Initialize() {
        setInitialized(true);
    }

    public boolean isUserLoggedIn(){
        SharedPreferences sharedPreferences = AppController.getInstance().getCurrentActivity().getSharedPreferences(StringConst.userPrefs, MODE_PRIVATE);
        return sharedPreferences.getBoolean(StringConst.userLoggedIn, false);
    }
    public void setUserLoggedIn(User _loggedUser){
        SharedPreferences sharedPreferences = AppController.getInstance().getCurrentActivity().getSharedPreferences(StringConst.userPrefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(StringConst.userLoggedIn, true);
        editor.putString(StringConst.userName, _loggedUser.getUsername());
        editor.apply();

        setUser(_loggedUser);
        Initialize();
        AppController.getInstance().LoadChatRooms();
    }
    public void setUser(String username, final FirebaseManager.OnUserFetchListener listener) {
        Log.d(TAG, "setUser: username " + username);
        AppController.getInstance().getManager(FirebaseManager.class).getUserFromDatabase(username, new FirebaseManager.OnUserFetchListener() {
            @Override
            public void onUserFetchSuccess(User user) {

                if (user != null) {
                    setUser(user);
                    listener.onUserFetchSuccess(user);
                } else {
                    listener.onUserFetchFailure("User data is null");
                }
                Log.d(TAG, "Initialize: onUserFetchSuccess" + user);
            }
            @Override
            public void onUserFetchFailure(String errorMessage) {

                Log.d(TAG, "Initialize: onUserFetchFailure " + errorMessage);
                listener.onUserFetchFailure("User data is null");
            }
        });
    }
    //endregion Methods

    public boolean isInitialized() {
        return initialized;
    }
    public void setInitialized(boolean initialize) {
        this.initialized = initialize;
    }
}