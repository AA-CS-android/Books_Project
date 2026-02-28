package com.hw.books_project.screens.book;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.hw.books_project.R;
import com.hw.books_project.models.Library;

public class AddBookApiActivity extends AppCompatActivity {

    private Library library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_api);

        library = (Library) getIntent().getSerializableExtra("library");

        if (savedInstanceState == null) {
            showAddBookSearchScreen(false); // Show for the first time, don't add to backstack
        }
    }

    public void showApiKeyScreen() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new ApiKeyFragment());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void showAddBookSearchScreen(boolean addToBackStack) {
        Fragment searchFragment = new AddBookSearchApiFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("library", library);
        searchFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, searchFragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void onApiKeySaved() {
        getSupportFragmentManager().popBackStack();
    }
}
