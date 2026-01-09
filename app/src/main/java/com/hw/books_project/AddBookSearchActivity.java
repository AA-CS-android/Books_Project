package com.hw.books_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hw.books_project.models.Library;

public class AddBookSearchActivity extends AppCompatActivity {

    private EditText etBookName, etISBN, etDanacode;
    private Button btnSearch;
    private Library library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_search);

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
        etISBN = findViewById(R.id.etISBN);
        etDanacode = findViewById(R.id.etDanacode);
        btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> searchBook());
    }

    private void searchBook() {
        String name = etBookName.getText().toString().trim();
        String isbn = etISBN.getText().toString().trim();
        String danacode = etDanacode.getText().toString().trim();

        if (name.isEmpty() && isbn.isEmpty() && danacode.isEmpty()) {
            Toast.makeText(this, "Please enter at least one search term.", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implement API call to search for book
        String toastMessage = "Searching for: Name=" + name + ", ISBN=" + isbn + ", Danacode=" + danacode;
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }
}
