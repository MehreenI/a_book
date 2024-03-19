package com.example.book.ui.awards;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.book.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class DailyRewards extends AppCompatActivity {

    private static final String PREF_NAME = "DailyRewardPrefs";
    private static final String KEY_REWARD_CLAIMED = "rewardClaimed";
    private static final String KEY_LAST_CLAIMED_DAY = "lastClaimedDay";
    private static final String KEY_LAST_CLAIM_TIME = "lastClaimTime";

    int current_awards = 10;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_rewards);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Daily Rewards"); // Set the title
        }

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Check if a user is logged in
        if (mAuth.getCurrentUser() != null) {
            // If logged in, proceed with initializing the rewards UI
            initializeRewardsUI();
        } else {
            // If not logged in, handle accordingly (e.g., redirect to login screen)
            showToast("User not logged in. Redirect to login screen.");
            // Implement your login redirection logic here.
        }
    }

    private void initializeRewardsUI() {
        // Retrieve the current day of the week (1 to 7, where 1 is Sunday and 7 is Saturday)
        int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        // Check if 24 hours have passed since the last claim
        if (is24HoursPassed()) {
            // Reset the state if 24 hours have passed
            resetState();
        }

        // Get the last claimed day
        int lastClaimedDay = getLastClaimedDay();

        // Check if the reward was claimed today or not
        boolean rewardClaimedToday = isRewardClaimedToday();

        if (currentDayOfWeek != lastClaimedDay && !rewardClaimedToday) {
            // Enable the button only if it's a new day and the reward hasn't been claimed today
            enableButtonForDay(currentDayOfWeek);

        } else {
            // Disable all buttons if the user has already claimed the reward today
            disableAllButtons();
        }
    }

    public void onButtonClick(View view) {
    }

    public void onClaimButtonClick(View view) {
        if (isRewardClaimedToday()) {
            showToast("You have already claimed your reward today");
        } else {
            // Perform the claim daily reward logic here
            showToast("Congratulations! You claimed your daily reward.");

            // Save the current day as the last claimed day
            saveLastClaimedDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

            // Mark the reward as claimed today
            markRewardAsClaimedToday();

            // Update the claimed reward in the database
            claimedReward();

            // Disable all buttons
            disableAllButtons();
        }
    }

    private void claimedReward() {
        // Get the current user ID
        String userId = mAuth.getCurrentUser().getUid();

        // Get a reference to the user's rewards node
        DatabaseReference userRef = databaseReference.child(userId).child("coin");

        // Read the current value from the database
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the current value
                int currentAmount = 0; // Default value if no data exists
                if (task.getResult().getValue() != null) {
                    currentAmount = Integer.parseInt(task.getResult().getValue().toString());
                }

                // Add the claimed award amount
                int newAmount = currentAmount + current_awards;

                // Update the value in the database
                userRef.setValue(newAmount);

                // Display a toast or perform any other actions as needed
                showToast("Added " + current_awards + " to your existing rewards. Your new total: " + newAmount);
            } else {
                // Handle the error
                showToast("Error reading data from the database");
            }
        });
    }



    private boolean is24HoursPassed() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        long lastClaimTime = preferences.getLong(KEY_LAST_CLAIM_TIME, 0);
        long currentTime = System.currentTimeMillis();
        return currentTime - lastClaimTime >= 24 * 60 * 60 * 1000; // 24 hours in milliseconds
    }

    private void resetState() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_REWARD_CLAIMED, false);
        editor.apply();
    }

    private void enableButtonForDay(int dayOfWeek) {
        int buttonId = getResources().getIdentifier("button" + dayOfWeek, "id", getPackageName());
        Button button = findViewById(buttonId);

        if (button != null) {
            button.setEnabled(true);
            button.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_orange_light));
        }
    }

    private void disableAllButtons() {
        for (int i = 1; i <= 7; i++) {
            int buttonId = getResources().getIdentifier("button" + i, "id", getPackageName());
            Button button = findViewById(buttonId);

            if (button != null) {
                button.setEnabled(false);
                button.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
            }
        }
    }

    private void saveLastClaimedDay(int dayOfWeek) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_LAST_CLAIMED_DAY, dayOfWeek);
        editor.apply();
    }

    private int getLastClaimedDay() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return preferences.getInt(KEY_LAST_CLAIMED_DAY, 0);
    }

    private boolean isRewardClaimedToday() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return preferences.getBoolean(KEY_REWARD_CLAIMED, false);
    }

    private void markRewardAsClaimedToday() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_REWARD_CLAIMED, true);
        editor.putLong(KEY_LAST_CLAIM_TIME, System.currentTimeMillis());
        editor.apply();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}