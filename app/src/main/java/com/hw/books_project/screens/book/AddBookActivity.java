package com.hw.books_project.screens.book;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hw.books_project.R;
import com.hw.books_project.models.Library;

public class AddBookActivity extends AppCompatActivity {

    private Library library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        library = (Library) getIntent().getSerializableExtra("library");

        BottomNavigationView bottomNav = findViewById(R.id.add_book_bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // as a default, select the manual fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.add_book_frame_container, new AddBookManualFragment()).commit();
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
            Bundle bundle = new Bundle();
            bundle.putSerializable("library", library);
            selectedFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.add_book_frame_container, selectedFragment).commit();
        }
        return true;
    };
}
