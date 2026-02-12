package com.hw.books_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hw.books_project.databinding.ActivityLibraryViewBinding;
import com.hw.books_project.models.Book;
import com.hw.books_project.models.Library;
import com.hw.books_project.models.User;

import java.util.ArrayList;

public class LibraryViewActivity extends AppCompatActivity {

    private ActivityLibraryViewBinding binding;
    private Library library;
    private ArrayList<Book> bookList = new ArrayList<>();
    private ArrayAdapter<Book> bookAdapter;

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

        User currentUser = FBRef.currentUser;
        if (currentUser != null && library.getAdmin() != null && library.getAdmin().equals(currentUser.getUid())) {
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

        bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookList);
        binding.lvBooks.setAdapter(bookAdapter);

        binding.lvBooks.setOnItemClickListener((parent, view, position, id) -> {
            Book selectedBook = bookList.get(position);
            Toast.makeText(this, selectedBook.getName() + " clicked", Toast.LENGTH_SHORT).show();
        });

        setupBookSearch();
    }

    private void setupBookSearch() {
        binding.bookSearchBar.setOnClickListener(v -> binding.bookSearchView.show());
        binding.bookSearchView.getEditText().setSingleLine();

        binding.bookSearchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.bookSearchView.getText().toString().trim();
                binding.bookSearchBar.setText(query);
                binding.bookSearchView.hide();
                if (!query.isEmpty()) {
                    searchBooks(query);
                }
            }
            return false;
        });
    }

    private void searchBooks(String query) {
        if (library.getBooks() == null || library.getBooks().isEmpty()) {
            Toast.makeText(this, "This library has no books.", Toast.LENGTH_SHORT).show();
            return;
        }

        bookList.clear();
        bookAdapter.notifyDataSetChanged();

        for (String bookId : library.getBooks().keySet()) {
            FBRef.refBooks.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot bookSnapshot) {
                    Book book = bookSnapshot.getValue(Book.class);
                    if (book != null && book.getName() != null && book.getName().toLowerCase().contains(query.toLowerCase())) {
                        bookList.add(book);
                        bookAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LibraryViewActivity.this, "Failed to load book details for ID: " + bookId, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
