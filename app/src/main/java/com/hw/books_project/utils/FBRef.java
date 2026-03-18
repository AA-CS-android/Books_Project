package com.hw.books_project.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.hw.books_project.objects.User;

public class FBRef {
    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();
    public static FirebaseDatabase refDB = FirebaseDatabase.getInstance();

    public static DatabaseReference refBooks = refDB.getReference("Books");
    public static DatabaseReference refUsers = refDB.getReference("Users");
    public static DatabaseReference refLibraries = refDB.getReference("Libraries");
    public static DatabaseReference refUserLibraries = refDB.getReference("UserLibraries");
    public static DatabaseReference refLoans = refDB.getReference("Loans");

    public static User currentUser;

    public static String firebaseAuthError(Exception exp) {
        return "An error occurred. Please try again later.";
    }
}
