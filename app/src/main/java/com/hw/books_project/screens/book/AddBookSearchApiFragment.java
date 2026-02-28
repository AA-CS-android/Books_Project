package com.hw.books_project.screens.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hw.books_project.R;
import com.hw.books_project.models.Library;

public class AddBookSearchApiFragment extends Fragment {

    private Library library;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            library = (Library) getArguments().getSerializable("library");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book_search_api, container, false);

        Button btnManageApiKey = view.findViewById(R.id.btnManageApiKey);
        btnManageApiKey.setOnClickListener(v -> {
            ((AddBookApiActivity) getActivity()).showApiKeyScreen();
        });

        Button btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Search functionality will be implemented here.", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
