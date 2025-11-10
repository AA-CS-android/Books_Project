package com.hw.books_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.hw.books_project.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    EditText eTMail, eTPass;
    TextView tVMsg;
    Button btnCreateUser, btnLoginUser;
    CheckBox stayConnected;
    SharedPreferences sharedPref;
    private ActivityMainBinding binding;
    FirebaseUser user;
    Intent si;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        init();
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean isChecked = sharedPref.getBoolean("stayConnected", false);
        si = new Intent(MainActivity.this, HomeScreenAct.class);
        FirebaseUser fbuser = FBRef.refAuth.getCurrentUser();
        if (fbuser != null && isChecked) {
            user = fbuser;
            startActivity(si);
        }
    }

    private void init() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        eTMail = binding.eTMail;
        eTPass = binding.eTPass;
        tVMsg = binding.tVMsg;
        btnCreateUser = binding.btnCreateUser;
        btnLoginUser = binding.btnLoginUser;
        stayConnected = binding.stayConnected;
        sharedPref = getSharedPreferences("user_details", MODE_PRIVATE);
        btnCreateUser.setOnClickListener(this::createUser);
        btnLoginUser.setOnClickListener(this::loginUser);
    }

    public void createUser(View view) {
        String email = eTMail.getText().toString();
        String pass = eTPass.getText().toString();
        if (email.isEmpty() || pass.isEmpty()) {
            tVMsg.setText("Please fill all fields");
            return;
        }

        Log.i("MainActivity", "mail: " + email + " pass: " + pass);
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Connecting");
        pd.setMessage("Creating user");
        pd.show();

        FBRef.refAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("stayConnected", stayConnected.isChecked());
                        editor.apply();

                        Log.i("MainActivity", "createUserWithEmailAndPassword: success");
                        user = FBRef.refAuth.getCurrentUser();
                        tVMsg.setText("User created successfully\nUid: " + (user != null ? user.getUid() : ""));
                        startActivity(si);
                    } else {
                        handleFirebaseAuthError(task.getException());
                    }
                });
    }

    public void loginUser(View view) {
        String email = eTMail.getText().toString();
        String pass = eTPass.getText().toString();
        if (email.isEmpty() || pass.isEmpty()) {
            tVMsg.setText("Please fill all fields");
            return;
        }

        Log.i("MainActivity", "mail: " + email + " pass: " + pass);
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Connecting");
        pd.setMessage("Logging in");
        pd.show();

        FBRef.refAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("stayConnected", stayConnected.isChecked());
                        editor.apply();

                        Log.i("MainActivity", "signInUserWithEmailAndPassword: success");
                        user = FBRef.refAuth.getCurrentUser();
                        tVMsg.setText("User logged in successfully\nUid: " + (user != null ? user.getUid() : ""));
                        startActivity(si);
                    } else {
                        handleFirebaseAuthError(task.getException());
                    }
                });
    }

    private void handleFirebaseAuthError(Exception exp) {
        String message;
        if (exp instanceof FirebaseAuthInvalidUserException) {
            message = "Invalid email address.";
        } else if (exp instanceof FirebaseAuthWeakPasswordException) {
            message = "Password too weak.";
        } else if (exp instanceof FirebaseAuthUserCollisionException) {
            message = "User already exists.";
        } else if (exp instanceof FirebaseAuthInvalidCredentialsException) {
            message = "Invalid credentials.";
        } else if (exp instanceof FirebaseNetworkException) {
            message = "Network error. Please check your connection.";
        } else {
            message = "An error occurred. Please try again later.";
        }
        tVMsg.setText(message);
        Log.e("MainActivity", "Firebase Authentication Error", exp);
    }
}
