package com.igtai.androidassignmentgallery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.igtai.androidassignmentgallery.R;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, signupButton, guestButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide ActionBar (Toolbar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //If user is logged in, go directly to HomeActivity
        if (user != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish(); // Close MainActivity
            return;  // Stop further execution
        }

        loginButton = findViewById(R.id.login);
        signupButton = findViewById(R.id.signup);
        guestButton = findViewById(R.id.guestButton);

        // Handle Sign Up button click
        signupButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Handle Login button click (assuming LoginActivity is already created)
        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        guestButton.setOnClickListener(view->{
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("isGuest",true);
            startActivity(intent);
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

    }



}