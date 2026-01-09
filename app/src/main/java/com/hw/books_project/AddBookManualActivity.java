package com.hw.books_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hw.books_project.models.Book;
import com.hw.books_project.models.Library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddBookManualActivity extends AppCompatActivity {

    private EditText etBookName, etAuthor, etPublisher, etReleaseDate, etLanguage, etGenres, etISBN, etDanacode, etCoverImageUrl;
    private Button btnAddBook;
    private Library library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_manual);

        library = (Library) getIntent().getSerializableExtra("library");

        if (library == null) {
            Toast.makeText(this, "Library data not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        init();
    }

    private void init() {
        etBookName = findViewById(R.id.etBookName);
        etAuthor = findViewById(R.id.etAuthor);
        etPublisher = findViewById(R.id.etPublisher);
        etReleaseDate = findViewById(R.id.etReleaseDate);
        etLanguage = findViewById(R.id.etLanguage);
        etGenres = findViewById(R.id.etGenres);
        etISBN = findViewById(R.id.etISBN);
        etDanacode = findViewById(R.id.etDanacode);
        etCoverImageUrl = findViewById(R.id.etCoverImageUrl);
        btnAddBook = findViewById(R.id.btnAddBook);

        btnAddBook.setOnClickListener(v -> addBook());
    }

    private void addBook() {
        String name = etBookName.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();

        if (name.isEmpty() || author.isEmpty()) {
            Toast.makeText(this, "Please fill at least name and author", Toast.LENGTH_SHORT).show();
            return;
        }

        String bookUid = FBRef.refBooks.push().getKey();
        if (bookUid == null) {
            Toast.makeText(this, "Could not create book entry.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> genres = Arrays.asList(etGenres.getText().toString().split(","));

        Book newBook = new Book();
        newBook.setUid(bookUid);
        newBook.setName(name);
        newBook.setAuthor(author);
        newBook.setPublisher(etPublisher.getText().toString().trim());
        newBook.setRelease_date(etReleaseDate.getText().toString().trim());
        newBook.setLanguage(etLanguage.getText().toString().trim());
        newBook.setGenres(genres);
        newBook.setISBN(etISBN.getText().toString().trim());
        newBook.setDanacode(etDanacode.getText().toString().trim());
        newBook.setCover_image_url(etCoverImageUrl.getText().toString().trim());
        newBook.setStatus("Available");

        // Save the book to the global /Books node
        FBRef.refBooks.child(bookUid).setValue(newBook).addOnSuccessListener(aVoid -> {
            // Add the book's UID to the library's list of books
            List<String> libraryBooks = library.getBooks() != null ? new ArrayList<>(library.getBooks()) : new ArrayList<>();
            libraryBooks.add(bookUid);
            FBRef.refLibraries.child(library.getUid()).child("books").setValue(libraryBooks)
                    .addOnSuccessListener(aVoid1 -> {
                        Toast.makeText(AddBookManualActivity.this, "Book added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(AddBookManualActivity.this, "Failed to add book.", Toast.LENGTH_SHORT).show();
        });
    }
}
