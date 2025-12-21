package com.hw.books_project;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.hw.books_project.models.Library;

import java.util.ArrayList;

public class SearchHelper {

    /**
     * Sets up the search functionality for a library list.
     *
     * @param context            The application context.
     * @param searchBar          The Material SearchBar.
     * @param searchView         The Material SearchView.
     * @param searchListView     The ListView inside the SearchView for suggestions.
     * @param mainAdapter        The ArrayAdapter for the main list.
     * @param suggestionsAdapter The ArrayAdapter for the suggestions list.
     * @param fullLibraryList    The complete list of libraries to be searched.
     */
    public static void setupSearch(Context context, SearchBar searchBar, SearchView searchView, ListView searchListView, ArrayAdapter<Library> mainAdapter, ArrayAdapter<Library> suggestionsAdapter, ArrayList<Library> fullLibraryList) {

        // 1. Show the search view when the search bar is clicked
        searchBar.setOnClickListener(v -> searchView.show());

        // 2. Filter suggestions in real-time as the user types
        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Library> filteredSuggestions = new ArrayList<>();
                for (Library library : fullLibraryList) {
                    if (library.getName() != null && library.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                        filteredSuggestions.add(library);
                    }
                }
                suggestionsAdapter.clear();
                suggestionsAdapter.addAll(filteredSuggestions);
                suggestionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // 3. Handle clicks on items in the suggestions list
        searchListView.setOnItemClickListener((parent, view, position, id) -> {
            Library selectedLibrary = suggestionsAdapter.getItem(position);
            if (selectedLibrary != null) {
                searchBar.setText(selectedLibrary.getName());
                searchView.hide();
                // You could add navigation logic here
                Toast.makeText(context, "Selected: " + selectedLibrary.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        // 4. Handle search submission from the keyboard
        searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            String query = searchView.getText().toString();
            searchBar.setText(query);
            searchView.hide();
            mainAdapter.getFilter().filter(query);
            return false;
        });
    }
}
