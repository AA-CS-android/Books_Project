package com.hw.books_project.screens.library;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hw.books_project.R;
import com.hw.books_project.screens.book.AddBookActivity;
import com.hw.books_project.adapters.BookAdapter;
import com.hw.books_project.databinding.ActivityLibraryViewBinding;
import com.hw.books_project.objects.Book;
import com.hw.books_project.objects.Library;
import com.hw.books_project.objects.User;
import com.hw.books_project.screens.book.AddBookApiActivity;
import com.hw.books_project.utils.FBRef;
import com.hw.books_project.utils.LoanUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LibraryViewActivity extends AppCompatActivity {

    private ActivityLibraryViewBinding binding;
    private Library library;
    private final ArrayList<Book> bookList = new ArrayList<>();
    private final ArrayList<Book> fullBookList = new ArrayList<>();
    private final Set<String> loadedBookIds = new HashSet<>();
    private BookAdapter bookAdapter;

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
        fetchFullLibraryDetails();
    }

    private void init() {
        binding.toolbar.setTitle(library.getName());

        initAdminFeatures();

        binding.btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, LibraryInfoActivity.class);
            intent.putExtra("library", library);
            startActivity(intent);
        });

        bookAdapter = new BookAdapter(this, bookList);
        binding.lvBooks.setAdapter(bookAdapter);

        binding.lvBooks.setOnItemClickListener((parent, view, position, id) -> {
            Book selectedBook = bookList.get(position);
            showLoanDialog(selectedBook);
        });

        setupBookSearch();
    }

    private void showLoanDialog(Book book) {
        // Calculate return date
        String formattedReturnDate = LoanUtils.getReturnDate(library.getMaxLoanDuration());
        String message = "Book name: " + book.getName() + "\n" +
                         "Return date: " + formattedReturnDate;

        new AlertDialog.Builder(this)
                .setTitle("Loan book")
                .setMessage(message)
                .setPositiveButton("loan", (dialog, which) -> {
                    // Placeholder for actual loan logic
                    loanBook(library, book, FBRef.currentUser);
                    /// TODO: create an alarm/notification using alarm manager and notification (project requirements)
                })
                .setNegativeButton("cancel", null)
                .show();
    }

    private void loanBook(Library library, Book book, User user){
        LoanUtils.loanBook(library, book, user, new LoanUtils.LoanCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(LibraryViewActivity.this, "loan successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(LibraryViewActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initAdminFeatures() {
        User currentUser = FBRef.currentUser;
        if (currentUser != null && library.getAdmin() != null && library.getAdmin().equals(currentUser.getUid())) {
            binding.addBookMenuBtn.setVisibility(View.VISIBLE);
            binding.addBookMenuBtn.setOnMenuItemClickListener(i -> {
                if (R.drawable.api_icon == i){
                    Intent addBookIntent = new Intent(this, AddBookApiActivity.class);
                    addBookIntent.putExtra("library", library);
                    startActivity(addBookIntent);
                } else if (R.drawable.archive_icon == i){
                    Intent addBookIntent = new Intent(this, AddBookActivity.class);
                    addBookIntent.putExtra("library", library);
                    startActivity(addBookIntent);
                }
            });
        } else {
            binding.addBookMenuBtn.setVisibility(View.GONE);
        }
    }

    private void fetchFullLibraryDetails() {
        FBRef.refLibraries.child(library.getLibraryId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Library fullLibrary = snapshot.getValue(Library.class);
                if (fullLibrary != null) {
                    library = fullLibrary;
                    binding.toolbar.setTitle(library.getName());
                    initAdminFeatures();
                    loadLibraryBooks();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadLibraryBooks() {
        if (library.getBooks() == null) {
            bookList.clear();
            fullBookList.clear();
            loadedBookIds.clear();
            bookAdapter.notifyDataSetChanged();
            return;
        }

        for (String bookId : library.getBooks().keySet()) {
            if (loadedBookIds.contains(bookId)) continue;

            loadedBookIds.add(bookId);
            FBRef.refBooks.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Book book = snapshot.getValue(Book.class);
                    if (book != null) {
                        updateBookInLists(book);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    private synchronized void updateBookInLists(Book book) {
        int index = -1;
        for (int i = 0; i < fullBookList.size(); i++) {
            if (fullBookList.get(i).getBookId().equals(book.getBookId())) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            fullBookList.set(index, book);
        } else {
            fullBookList.add(book);
        }
        
        // Refresh visible list with current search query (or empty for all)
        searchBooks(binding.bookSearchBar.getText().toString());
    }

    private void setupBookSearch() {
        binding.bookSearchBar.setOnClickListener(v -> binding.bookSearchView.show());
        binding.bookSearchView.getEditText().setSingleLine();

        binding.bookSearchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.bookSearchView.getText().toString().trim();
                binding.bookSearchBar.setText(query);
                binding.bookSearchView.hide();
                searchBooks(query);
            }
            return false;
        });
    }

    private void searchBooks(String query) {
        String lowerQuery = query.toLowerCase().trim();
        bookList.clear();
        for (Book book : fullBookList) {
            if (lowerQuery.isEmpty() || (book.getName() != null && book.getName().toLowerCase().contains(lowerQuery))) {
                bookList.add(book);
            }
        }
        bookAdapter.notifyDataSetChanged();
    }
}
