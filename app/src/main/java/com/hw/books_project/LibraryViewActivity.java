package com.hw.books_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hw.books_project.databinding.ActivityLibraryViewBinding;
import com.hw.books_project.models.Library;
import com.hw.books_project.models.User;

public class LibraryViewActivity extends AppCompatActivity {

    private ActivityLibraryViewBinding binding;
    private Library library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLibraryViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        library = (Library) getIntent().getSerializableExtra("library");

        if (library == null) {
            Toast.makeText(this, "Library data not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        init();
    }

    private void init() {
        binding.toolbar.setTitle(library.getName());

        // Show the add book button only if the current user is an admin
        User currentUser = FBRef.currentUser;
        if (currentUser != null && library.getAdmins() != null && library.getAdmins().contains(currentUser.getUid())) {
            binding.fabAddBook.setVisibility(View.VISIBLE);
        }

        binding.btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, LibraryInfoActivity.class);
            intent.putExtra("library", library);
            startActivity(intent);
        });

        binding.fabAddBook.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddBookActivity.class);
            intent.putExtra("library", library);
            startActivity(intent);
        });

        // TODO: Setup book search and list
    }
}
