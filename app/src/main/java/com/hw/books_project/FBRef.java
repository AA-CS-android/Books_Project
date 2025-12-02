package com.hw.books_project;

import android.util.Log;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hw.books_project.models.User;

public class FBRef {
    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();
    public static FirebaseDatabase refDB = FirebaseDatabase.getInstance();

    public static DatabaseReference refBooks=refDB.getReference("Books");
    public static DatabaseReference refUsers=refDB.getReference("Users");

    public static User currentUser;

    public static String firebaseAuthError(Exception exp) {
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
        return message;
    }
}
