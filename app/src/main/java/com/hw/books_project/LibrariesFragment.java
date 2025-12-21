package com.hw.books_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hw.books_project.databinding.FragmentLibariesBinding;
import com.hw.books_project.models.Library;

import java.util.ArrayList;

public class LibrariesFragment extends Fragment {

    private FragmentLibariesBinding binding;
    private ArrayAdapter<Library> mainAdapter;
    private ArrayAdapter<Library> suggestionsAdapter;
    private ArrayList<Library> libraryList = new ArrayList<>();

    public LibrariesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLibariesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        // 1. Initialize Adapters
        mainAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, libraryList);
        suggestionsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        binding.libsList.setAdapter(mainAdapter);
        binding.searchListView.setAdapter(suggestionsAdapter);

        // 2. Fetch Data from Firebase
        fetchLibraries();

        // 3. Setup Listeners
        setupListeners();
    }

    private void fetchLibraries() {
        FBRef.refLibraries.limitToFirst(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                libraryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Library library = dataSnapshot.getValue(Library.class);
                    if (library != null) {
                        libraryList.add(library);
                    }
                }
                mainAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load libraries.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        binding.createLibBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CreateLibraryActivity.class);
            startActivity(intent);
        });

        binding.libSearchBar.setOnClickListener(v -> binding.searchView.show());

        binding.searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        binding.searchListView.setOnItemClickListener((parent, view, position, id) -> {
            Library selectedLibrary = suggestionsAdapter.getItem(position);
            if (selectedLibrary != null) {
                binding.libSearchBar.setText(selectedLibrary.getName());
                binding.searchView.hide();
                // You might want to navigate to a detail screen here
                Toast.makeText(requireContext(), "Selected: " + selectedLibrary.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            String query = binding.searchView.getText().toString();
            binding.libSearchBar.setText(query);
            binding.searchView.hide();
            // This will filter the main list
            mainAdapter.getFilter().filter(query);
            return false;
        });
    }

    private void filterSuggestions(String query) {
        ArrayList<Library> filteredSuggestions = new ArrayList<>();
        for (Library library : libraryList) {
            if (library.getName() != null && library.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredSuggestions.add(library);
            }
        }
        suggestionsAdapter.clear();
        suggestionsAdapter.addAll(filteredSuggestions);
        suggestionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
