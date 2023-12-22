package com.example.book.ui.sinup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.book.MainActivity;
import com.example.book.R;
import com.example.book.ui.Model.User;
import com.example.book.ui.home.HomeFragment;
import com.example.book.ui.signin.loginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private EditText user_name;
    private EditText etemail;
    private EditText password;
    private EditText phone;
    private EditText confirmPassword;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        user_name = findViewById(R.id.user_name);
        etemail = findViewById(R.id.etemail);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        confirmPassword = findViewById(R.id.confirmPassword);

        Button btnSignup = findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(view -> signUp());

        Button btnlogin = findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(view -> {
            Intent intent = new Intent(this, loginActivity.class);
            startActivity(intent);
        });
    }




    private void signUp() {
        String username = user_name.getText().toString();
        String email = etemail.getText().toString();
        String password = this.password.getText().toString();
        String phonenumber = phone.getText().toString();
        String confirmpassword = confirmPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || confirmpassword.isEmpty()) {
            Toast.makeText(this, "Fields can't be blank", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmpassword)) {
            Toast.makeText(this, "Password and Confirm password do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        String userEmail = firebaseAuth.getCurrentUser().getEmail();

                        User user = new User(username, email, phonenumber);
                        databaseReference.child(userId).setValue(user);

                        Toast.makeText(this, "Sign-up Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Exception exception = task.getException();
                        Toast.makeText(this, "Error creating user: " + (exception != null ? exception.getMessage() : ""), Toast.LENGTH_SHORT).show();
                    }
                });
    }}