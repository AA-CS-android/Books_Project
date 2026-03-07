package com.hw.books_project.screens.book;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hw.books_project.adapters.BookAdapter;
import com.hw.books_project.databinding.FragmentAddBookSearchBinding;
import com.hw.books_project.models.Book;
import com.hw.books_project.models.Library;
import com.hw.books_project.utils.FBRef;
import com.hw.books_project.utils.LibraryUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddBookSearchFragment extends Fragment {

    private FragmentAddBookSearchBinding binding;
    private Library library;
    private final ArrayList<Book> searchResults = new ArrayList<>();
    private BookAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBookSearchBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            library = (Library) getArguments().getSerializable("library");
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new BookAdapter(requireContext(), searchResults);
        binding.lvInternalResults.setAdapter(adapter);

        setupFormatting();

        binding.btnSearch.setOnClickListener(v -> {
            hideKeyboard();
            String name = binding.etBookName.getText().toString().trim().toLowerCase();
            String isbn = binding.etISBN.getText().toString().trim();
            int searchType = binding.rgSearchType.getCheckedRadioButtonId();
            if (searchType == binding.rbSearchISBN.getId()) {
                if (isbn.isEmpty()) {
                    Toast.makeText(requireContext(), "Search term cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                performInternalSearch("isbn", isbn);
            } else if (searchType == binding.rbSearchName.getId()) {
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Search term cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                performInternalSearch("name", name);
            }
        });

        binding.lvInternalResults.setOnItemClickListener((parent, view1, position, id) -> {
            Book selectedBook = searchResults.get(position);
            showAddConfirmationDialog(selectedBook);
        });

    }

    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void performInternalSearch(String searchField, String searchValue) {
        Query query = FBRef.refBooks.orderByChild(searchField).startAt(searchValue).limitToFirst(20);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Query response", "number returned: " + snapshot.getChildrenCount());
                searchResults.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Book book = data.getValue(Book.class);
                    if (book != null) {
                        searchResults.add(book);
                    }
                }
                adapter.notifyDataSetChanged();
                if (searchResults.isEmpty()) {
                    Toast.makeText(requireContext(), "No books found in internal database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Search failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddConfirmationDialog(Book book) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Add Book")
                .setMessage("Would you like to add '" + book.getName() + "' to your library?")
                .setPositiveButton("Add", (dialog, which) -> addBookToLibrary(book))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addBookToLibrary(Book book) {
        LibraryUtils.addBookToLibrary(library, book.getBookId(), new LibraryUtils.OnLibraryUpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireContext(), "Book added to library.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to add book to library: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
