package com.hw.books_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hw.books_project.databinding.ActivityLoginBinding;
import com.hw.books_project.models.User;

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
        String email = binding.eTMail.getText().toString().trim();
        String password = binding.eTPass.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            binding.tVMsg.setText("Please fill all fields");
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Logging in...");
        pd.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Fetch user data from Realtime Database
                            FBRef.refUsers.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    pd.dismiss();
                                    FBRef.currentUser = snapshot.getValue(User.class);

                                    // Save remember me preference
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putBoolean("rememberMe", binding.rememberMe.isChecked());
                                    editor.apply();

                                    Log.i("LoginActivity", "User logged in successfully.");
                                    Intent intent = new Intent(LoginActivity.this, HomeScreenAct.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    pd.dismiss();
                                    binding.tVMsg.setText("Failed to load user data.");
                                    Log.w("LoginActivity", "loadUser:onCancelled", error.toException());
                                }
                            });
                        }
                    } else {
                        pd.dismiss();
                        binding.tVMsg.setText(FBRef.firebaseAuthError(task.getException()));
                        Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                    }
                });
    }
}
