package com.hw.books_project;

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

import com.hw.books_project.databinding.FragmentLibariesBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class LibrariesFragment extends Fragment {

    private FragmentLibariesBinding binding;
    private ArrayAdapter<String> mainAdapter;
    private ArrayAdapter<String> suggestionsAdapter;
    private ArrayList<String> demoArr;

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
        // 1. Initialize Demo Data
        String[] initialData = {"Central Library", "City Archive", "Community Bookstop", "University Main Lib", "Science Fiction Hub", "History Corner", "Kids Reading Room", "Tech Library"};
        demoArr = new ArrayList<>(Arrays.asList(initialData));

        // 2. Initialize Adapters
        mainAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, demoArr);
        suggestionsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        binding.libsList.setAdapter(mainAdapter);
        binding.searchListView.setAdapter(suggestionsAdapter);

        // --- Listeners ---

        binding.createLibBtn.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Create Library Button Clicked", Toast.LENGTH_SHORT).show();
        });

        binding.libSearchBar.setOnClickListener(v -> binding.searchView.show());

        // 3. TextWatcher for real-time suggestions
        binding.searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Handle item click in suggestions list
        binding.searchListView.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(requireContext(), "Item Clicked: " + suggestionsAdapter.getItem(position), Toast.LENGTH_SHORT).show();
//            String selectedItem = suggestionsAdapter.getItem(position);
//            binding.libSearchBar.setText(selectedItem);
//            binding.searchView.hide();
//            mainAdapter.getFilter().filter(selectedItem);
        });

        // 4. Handle search submit
        binding.searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            String query = binding.searchView.getText().toString();
            binding.libSearchBar.setText(query);
            binding.searchView.hide();
            mainAdapter.getFilter().filter(query);
            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void filterList(String query)
    {
        suggestionsAdapter.clear();
        ArrayList<String> filteredSuggestions = new ArrayList<>();
        for (String item : demoArr) {
            if (item.toLowerCase().contains(query.toLowerCase())) {
                suggestionsAdapter.add(item);
            }
        }
//        suggestionsAdapter.clear();
        //suggestionsAdapter.addAll(filteredSuggestions);
        suggestionsAdapter.notifyDataSetChanged();
    }
}
