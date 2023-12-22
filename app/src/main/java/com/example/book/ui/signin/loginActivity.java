package com.example.book.ui.signin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.book.MainActivity;
import com.example.book.manager.CoinFetchCallback;
import com.example.book.ui.sinup.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.book.R;

public class loginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText etemail;
    private EditText password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        etemail = findViewById(R.id.etemail);
        password = findViewById(R.id.password);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        Button btnSignup = findViewById(R.id.SignUp_login);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        String email = etemail.getText().toString();
        String userPassword = password.getText().toString();

        if (email.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Email and password can't be blank", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, userPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        String userEmail = firebaseAuth.getCurrentUser().getEmail();


                        // Fetch user's coins from Firebase or any other source
                        fetchUserCoinsFromFirebase(userId, new CoinFetchCallback() {
                            public void onCoinsFetched(int userCoins) {
                                // Now you have the user's coins, you can proceed
                                Toast.makeText(loginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                // Create an intent to navigate to the MainActivity
                                Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("userEmail", userEmail);
                                intent.putExtra("userCoins", userCoins);

                                startActivity(intent);
                            }
                        });
                    } else {
                        Exception exception = task.getException();
                        Toast.makeText(loginActivity.this, "Authentication Failed: " + (exception != null ? exception.getMessage() : ""), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void fetchUserCoinsFromFirebase(String userId, CoinFetchCallback callback) {
        // Declare userCoins as an array of size 1
        final int[] userCoins = {0}; // Default value

        // Reference to the "users" node in Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Read user's coins value from Firebase
        userRef.child("coin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User's coins value exists in the database
                    userCoins[0] = dataSnapshot.getValue(Integer.class);
                    // Now you have the user's coins, you can update the UI or perform other actions
                    callback.onCoinsFetched(userCoins[0]);
                } else {
                    // Handle the case where coins data is not available
                    Log.e("Firebase", "User coins data not found");
                    callback.onCoinsFetched(0); // Default value
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


}
