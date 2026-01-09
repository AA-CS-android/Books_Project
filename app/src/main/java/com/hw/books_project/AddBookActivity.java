package com.hw.books_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hw.books_project.databinding.ActivityAddBookBinding;
import com.hw.books_project.models.Library;

public class AddBookActivity extends AppCompatActivity {

    private ActivityAddBookBinding binding;
    private Library library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        library = (Library) getIntent().getSerializableExtra("library");

        init();
    }

    private void init() {
        binding.btnAddManually.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddBookManualActivity.class);
            intent.putExtra("library", library);
            startActivity(intent);
        });

        binding.btnSearchOnline.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddBookSearchActivity.class);
            intent.putExtra("library", library);
            startActivity(intent);
        });
    }
}
