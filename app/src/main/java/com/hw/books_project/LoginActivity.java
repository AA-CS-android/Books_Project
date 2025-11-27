package com.hw.books_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hw.books_project.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        sharedPref = getSharedPreferences("user_details", MODE_PRIVATE);
        binding.btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = binding.eTMail.getText().toString();
        String password = binding.eTPass.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            binding.tVMsg.setText("Please fill all fields");
            return;
        }

        Log.i("MainActivity", "mail: " + email + " pass: " + password);
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Connecting");
        pd.setMessage("Logging in...");
        pd.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("rememberMe", binding.rememberMe.isChecked());
                            editor.apply();

                            Log.i("MainActivity", "signInUserWithEmailAndPassword: success");
                            Intent intent = new Intent(LoginActivity.this, HomeScreenAct.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        binding.tVMsg.setText(FBRef.firebaseAuthError(task.getException()));
                        Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                    }
                });
    }
}
