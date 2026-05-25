package com.hw.books_project.utils;

import android.widget.Button;
import android.widget.EditText;
import com.hw.books_project.objects.Library;

import java.util.HashMap;
import java.util.Map;

public class LibraryUtils {

    public interface OnLibraryUpdateListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    /**
     * Adds a book reference to the library's books map.
     * If the book already exists, its count is incremented by the specified amount.
     */
    public static void addBookToLibrary(Library library, String bookId, int count, OnLibraryUpdateListener listener) {
        if (library == null || bookId == null) {
            if (listener != null) listener.onFailure(new Exception("Library or Book ID is null"));
            return;
        }

        Map<String, Integer> books = (library.getBooks() != null) ? library.getBooks() : new HashMap<>();
        books.merge(bookId, count, Integer::sum);
        
        // Update local object
        library.setBooks(books);

        // Update database
        FBRef.refLibraries.child(library.getLibraryId()).child("books").setValue(books)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e);
                });
    }

    /**
     * Helper to set up increment/decrement buttons for an EditText.
     */
    public static void setupIncDecListeners(EditText et, Button btn, int val) {
        btn.setOnClickListener(v -> {
            try {
                int current = Integer.parseInt(et.getText().toString());
                int newVal = current + val;
                if (newVal <= 0) newVal = 1;
                et.setText(String.valueOf(newVal));
            } catch (NumberFormatException e) {
                et.setText("1");
            }
        });
    }
}
