package com.hw.books_project;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hw.books_project.models.User;

public class FBRef {
    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();
    public static FirebaseDatabase refDB = FirebaseDatabase.getInstance();

    public static DatabaseReference refBooks = refDB.getReference("Books");
    public static DatabaseReference refUsers = refDB.getReference("Users");
    public static DatabaseReference refLibraries = refDB.getReference("Libraries");

    public static User currentUser;

    public static String firebaseAuthError(Exception exp) {
        // ... (error handling code remains the same)
        return "An error occurred. Please try again later.";
    }
}
