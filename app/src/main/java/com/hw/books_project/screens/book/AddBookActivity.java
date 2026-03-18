package com.hw.books_project.screens.book;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hw.books_project.R;
import com.hw.books_project.objects.Book;
import com.hw.books_project.objects.Library;

public class AddBookActivity extends AppCompatActivity {

    private Library library;
    private Book prefilledBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        library = (Library) getIntent().getSerializableExtra("library");
        prefilledBook = (Book) getIntent().getSerializableExtra("prefilledBook");

        BottomNavigationView bottomNav = findViewById(R.id.add_book_bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            // If prefilledBook is present, we show the manual fragment immediately
            showFragment(new AddBookManualFragment());
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.nav_manual) {
            selectedFragment = new AddBookManualFragment();
        } else if (itemId == R.id.nav_search) {
            selectedFragment = new AddBookSearchFragment();
        }

        if (selectedFragment != null) {
            showFragment(selectedFragment);
        }
        return true;
    };

    private void showFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("library", library);
        if (fragment instanceof AddBookManualFragment) {
            bundle.putSerializable("prefilledBook", prefilledBook);
        }
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_book_frame_container, fragment)
                .commit();
    }
}
