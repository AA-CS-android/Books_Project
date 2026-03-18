package com.hw.books_project.screens.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hw.books_project.screens.home.HomeScreenActivity;
import com.hw.books_project.databinding.ActivityAuthBinding;
import com.hw.books_project.objects.User;
import com.hw.books_project.utils.FBRef;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private Intent intent;
    private boolean rememberMe;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        if (rememberMe && currentUser != null) {
            // Fetch user data before redirecting
            FBRef.refUsers.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    FBRef.currentUser = snapshot.getValue(User.class);
                    intent = new Intent(AuthActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle potential error or just load the UI
                }
            });
            return;
        }
        //load view content only after checking if user logged in
        setContentView(binding.getRoot());

    }

    private void init() {

        binding = ActivityAuthBinding.inflate(getLayoutInflater());

        SharedPreferences sharedPref = getSharedPreferences("user_details", MODE_PRIVATE);
        rememberMe = sharedPref.getBoolean("rememberMe", false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.btnLogin.setOnClickListener(v -> {
            intent = new Intent(AuthActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        binding.btnSignUp.setOnClickListener(v -> {
            intent = new Intent(AuthActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}
