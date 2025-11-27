package com.hw.books_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hw.books_project.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private FirebaseUser currentUser;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences("user_details", MODE_PRIVATE);
        boolean rememberMe = sharedPref.getBoolean("rememberMe", false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (rememberMe && currentUser != null) {
            intent = new Intent(AuthActivity.this, HomeScreenAct.class);
            startActivity(intent);
//            finish();
//            return;
        }

        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
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
