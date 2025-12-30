package com.hw.books_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hw.books_project.models.Library;
import com.hw.books_project.models.User;

import java.util.ArrayList;

public class JoinLibraryActivity extends AppCompatActivity {

    private TextView tvLibraryName;
    private Button btnJoinLibrary;
    private Library library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_library);

        library = (Library) getIntent().getSerializableExtra("library");

        if (library == null) {
            Toast.makeText(this, "Library data not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        init();
        displayLibraryInfo();
    }

    private void init() {
        tvLibraryName = findViewById(R.id.tvLibraryName);
        btnJoinLibrary = findViewById(R.id.btnJoinLibrary);

        btnJoinLibrary.setOnClickListener(v -> joinLibrary());
    }

    private void displayLibraryInfo() {
        tvLibraryName.setText(library.getName());
    }

    private void joinLibrary() {
        User currentUser = FBRef.currentUser;
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to join a library.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current list of users, or create a new one
        ArrayList<String> users = library.getUsers() != null ? new ArrayList<>(library.getUsers()) : new ArrayList<>();
        
        // Add the current user's UID to the list if not already present
        if (!users.contains(currentUser.getUid())) {
            users.add(currentUser.getUid());
            library.setUsers(users);

            // Update the library in Firebase
            FBRef.refLibraries.child(library.getUid()).child("users").setValue(users)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Successfully joined " + library.getName(), Toast.LENGTH_SHORT).show();
                        // Navigate to the library view screen
                        Intent intent = new Intent(this, LibraryViewActivity.class);
                        intent.putExtra("library", library);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to join library.", Toast.LENGTH_SHORT).show());
        }
    }
}
