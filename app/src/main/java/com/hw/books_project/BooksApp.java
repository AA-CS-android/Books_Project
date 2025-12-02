package com.hw.books_project;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class BooksApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //keep firebase active when offline, update firebase when connection is restored
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
