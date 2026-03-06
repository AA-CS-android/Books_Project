package com.hw.books_project.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchHelper {

    /**
     * A callback interface to notify when an item has been selected.
     */
    public interface OnItemSelectedListener<T> {
        void onItemSelected(T item);
    }

    /**
     * An interface to provide the searchable text for an item.
     */
    public interface SearchableProvider<T> {
        String getSearchableText(T item);
    }

    /**
     * Sets up the search functionality for a generic list.
     * It does not
     *
     * @param context            The application context.
     * @param searchBar          The Material SearchBar.
     * @param searchView         The Material SearchView.
     * @param searchListView     The ListView inside the SearchView for suggestions.
     * @param mainAdapter        The ArrayAdapter for the main list.
     * @param suggestionsAdapter The ArrayAdapter for the suggestions list.
     * @param fullList           The complete list of items to be searched.
     * @param searchableProvider A provider that returns the text to search in for each item.
     * @param listener           The callback for when an item is selected.
     */
    public static <T> void setupSearch(Context context, SearchBar searchBar, SearchView searchView, ListView searchListView, 
                                     ArrayAdapter<T> mainAdapter, ArrayAdapter<T> suggestionsAdapter, 
                                     List<T> fullList, SearchableProvider<T> searchableProvider, 
                                     OnItemSelectedListener<T> listener) {

        // 1. Show the search view when the search bar is clicked
        searchBar.setOnClickListener(v -> searchView.show());

        // 2. Filter suggestions in real-time as the user types
        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<T> filteredSuggestions = new ArrayList<>();
                String query = s.toString().toLowerCase();
                for (T item : fullList) {
                    String searchableText = searchableProvider.getSearchableText(item);
                    if (searchableText != null && searchableText.toLowerCase().contains(query)) {
                        filteredSuggestions.add(item);
                    }
                }
                suggestionsAdapter.clear();
                suggestionsAdapter.addAll(filteredSuggestions);
                suggestionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // 3. Handle clicks on items in the suggestions list by calling the listener
        searchListView.setOnItemClickListener((parent, view, position, id) -> {
            T selectedItem = suggestionsAdapter.getItem(position);
            if (selectedItem != null) {
                searchView.hide();
                listener.onItemSelected(selectedItem);
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
