package com.hw.books_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hw.books_project.databinding.ActivitySignupBinding;
import com.hw.books_project.models.User;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        sharedPref = getSharedPreferences("user_details", MODE_PRIVATE);
        binding.btnSignUp.setOnClickListener(v -> signUpUser());
    }

    private void signUpUser() {
        String firstName = binding.eTFirstName.getText().toString().trim();
        String lastName = binding.eTLastName.getText().toString().trim();
        String email = binding.eTMail.getText().toString().trim();
        String password = binding.eTPass.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            binding.tVMsg.setText("Please fill all fields");
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Connecting");
        pd.setMessage("Creating user");
        pd.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Create and save user to Realtime Database
                            User user = new User(firstName + " " + lastName, email, firebaseUser.getUid());
                            FBRef.refUsers.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(dbTask -> {
                                pd.dismiss();
                                if (dbTask.isSuccessful()) {
                                    // Set the global user object
                                    FBRef.currentUser = user;

                                    // Save remember me preference
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putBoolean("rememberMe", binding.rememberMe.isChecked());
                                    editor.apply();

                                    Log.i("SignUpActivity", "User created and saved successfully.");
                                    Intent intent = new Intent(SignUpActivity.this, HomeScreenAct.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    binding.tVMsg.setText("Failed to save user data.");
                                    Log.w("SignUpActivity", "saveUser:failure", dbTask.getException());
                                }
                            });
                        }
                    } else {
                        pd.dismiss();
                        binding.tVMsg.setText(FBRef.firebaseAuthError(task.getException()));
                        Log.w("SignUpActivity", "createUserWithEmail:failure", task.getException());
                    }
                });
    }
}
