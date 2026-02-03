package com.hw.books_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hw.books_project.models.Library;
import com.hw.books_project.models.User;

import java.util.HashMap;
import java.util.Map;

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

        Map<String, Boolean> users = library.getUsers() != null ? library.getUsers() : new HashMap<>();

        if (!users.containsKey(currentUser.getUid())) {
            users.put(currentUser.getUid(), true);
            library.setUsers(users);

            FBRef.refLibraries.child(library.getLibraryId()).child("users").setValue(users)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Successfully joined " + library.getName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, LibraryViewActivity.class);
                        intent.putExtra("library", library);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to join library.", Toast.LENGTH_SHORT).show());
        }
    }
}
