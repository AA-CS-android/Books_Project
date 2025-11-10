package com.hw.books_project;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBRef {
    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();
    public static FirebaseDatabase refDB = FirebaseDatabase.getInstance();

    public static DatabaseReference refBooks=refDB.getReference("Books");
    public static DatabaseReference refUsers=refDB.getReference("Users");
}
