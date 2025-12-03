package com.hw.books_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hw.books_project.databinding.FragmentLibariesBinding;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibrariesFragment extends Fragment {

    private FragmentLibariesBinding binding;
    private ArrayAdapter<String> adapter;
    private final List<String> demoList = Arrays.asList("Library 1", "Library 2", "Library 3", "Library 4", "Library 5");
    private final List<String> filteredList = new ArrayList<>();

    public LibrariesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment using View Binding
        binding = FragmentLibariesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        // Initialize UI components and Listeners here

        filteredList.addAll(demoList);
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,filteredList);

        // Logic for Creating a Library
        binding.createLibBtn.setOnClickListener(v -> {
            // TODO: Add your logic to open a dialog or activity to create a library
            Toast.makeText(requireContext(), "Create Library Button Clicked", Toast.LENGTH_SHORT).show();
        });

        // Logic for Search
        binding.libSearchBar.setOnClickListener(v -> {
            binding.searchView.show();
        });

        binding.searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            binding.libSearchBar.setText(binding.searchView.getText().toString());
            Toast.makeText(requireContext(), "Search: " + binding.searchView.getText().toString(), Toast.LENGTH_SHORT).show();
            binding.searchView.hide();
            return false;
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear binding reference to prevent memory leaks
        binding = null;
    }
}
