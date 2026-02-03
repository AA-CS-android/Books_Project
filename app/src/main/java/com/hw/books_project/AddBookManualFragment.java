package com.hw.books_project;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hw.books_project.databinding.FragmentAddBookManualBinding;
import com.hw.books_project.models.Book;
import com.hw.books_project.models.Library;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddBookManualFragment extends Fragment {

    private FragmentAddBookManualBinding binding;
    private Library library;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBookManualBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            library = (Library) getArguments().getSerializable("library");
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupFormatting();

        binding.btnAddBook.setOnClickListener(v -> addBook());
    }

    private void setupFormatting() {
        binding.etISBN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvFormattedISBN.setText(formatIsbn(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        binding.etDanacode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvFormattedDanacode.setText(formatDanacode(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private String formatIsbn(String isbn) {
        if (isbn == null) return "";
        isbn = isbn.replaceAll("[^\\d]", "");
        if (isbn.length() >= 10 && isbn.length() <= 13) {
            StringBuilder formatted = new StringBuilder(isbn);
            if (isbn.length() == 10) {
                if (isbn.length() > 1) formatted.insert(1, "-");
                if (isbn.length() > 6) formatted.insert(7, "-");
                if (isbn.length() > 9) formatted.insert(11, "-");
            } else {
                if (isbn.length() > 3) formatted.insert(3, "-");
                if (isbn.length() > 4) formatted.insert(5, "-");
                if (isbn.length() > 7) formatted.insert(8, "-");
                if (isbn.length() > 12) formatted.insert(13, "-");
            }
            return formatted.toString();
        }
        return isbn;
    }

    private String formatDanacode(String danacode) {
        if (danacode == null) return "";
        danacode = danacode.replaceAll("[^\\d]", "");
        long number;
        try {
            number = Long.parseLong(danacode);
        } catch (NumberFormatException e) {
            return danacode;
        }

        if (danacode.length() >= 3 && danacode.length() <= 12) {
            String firstPart = String.valueOf(number).substring(0, Math.min(3, String.valueOf(number).length()));
            String secondPart = String.valueOf(number).substring(3);
            return firstPart + (secondPart.isEmpty() ? "" : "-" + secondPart);
        }
        return String.valueOf(number);
    }

    private void addBook() {
        if (library == null) {
            Toast.makeText(requireContext(), "Library data not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = binding.etBookName.getText().toString().trim();
        String author = binding.etAuthor.getText().toString().trim();

        if (name.isEmpty() || author.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill at least name and author", Toast.LENGTH_SHORT).show();
            return;
        }

        String bookId = FBRef.refBooks.push().getKey();
        if (bookId == null) {
            Toast.makeText(requireContext(), "Could not create book entry.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> genres = Arrays.asList(binding.etGenres.getText().toString().split(","));

        Book newBook = new Book();
        newBook.setBookId(bookId);
        newBook.setName(name);
        newBook.setAuthor(author);
        newBook.setPublisher(binding.etPublisher.getText().toString().trim());
        newBook.setRelease_date(binding.etReleaseDate.getText().toString().trim());
        newBook.setLanguage(binding.etLanguage.getText().toString().trim());
        newBook.setGenres(genres);
        newBook.setISBN(binding.etISBN.getText().toString().trim());
        newBook.setDanacode(binding.etDanacode.getText().toString().trim());
        newBook.setCover_image_url(binding.etCoverImageUrl.getText().toString().trim());
        newBook.setStatus("Available");

        FBRef.refBooks.child(bookId).setValue(newBook).addOnSuccessListener(aVoid -> {
            Map<String, Map<String, Boolean>> libraryBooks = library.getBooks() != null ? library.getBooks() : new HashMap<>();
            String physicalBookId = FBRef.refLibraries.child(library.getLibraryId()).child("books").push().getKey();
            if (physicalBookId == null) {
                Toast.makeText(requireContext(), "Could not create physical book entry.", Toast.LENGTH_SHORT).show();
                return;
            }
            libraryBooks.put(bookId, new HashMap<>(Map.of(physicalBookId, true)));
            library.setBooks(libraryBooks);

            FBRef.refLibraries.child(library.getLibraryId()).child("books").setValue(libraryBooks)
                    .addOnSuccessListener(aVoid1 -> {
                        Toast.makeText(requireContext(), "Book added successfully!", Toast.LENGTH_SHORT).show();
                        requireActivity().finish();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to add book.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
