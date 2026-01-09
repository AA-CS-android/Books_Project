package com.hw.books_project;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hw.books_project.databinding.FragmentAddBookSearchBinding;

public class AddBookSearchFragment extends Fragment {

    private FragmentAddBookSearchBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBookSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupFormatting();

        binding.btnSearch.setOnClickListener(v -> {
            String name = binding.etBookName.getText().toString().trim();
            String isbn = binding.etISBN.getText().toString().trim();
            String danacode = binding.etDanacode.getText().toString().trim();

            if (name.isEmpty() && isbn.isEmpty() && danacode.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter at least one search term.", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Implement API call to search for book
            String toastMessage = "Searching for: Name=" + name + ", ISBN=" + isbn + ", Danacode=" + danacode;
            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_LONG).show();
        });
    }

    private void setupFormatting() {
        binding.etISBN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvFormattedISBN.setText(formatIsbn(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        binding.etDanacode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvFormattedDanacode.setText(formatDanacode(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private String formatIsbn(String isbn) {
        if (isbn == null) return "";
        isbn = isbn.replaceAll("[^\\d]", "");
        if (isbn.length() >= 10 && isbn.length() <= 13) {
            StringBuilder formatted = new StringBuilder(isbn);
            if (isbn.length() == 10) {
                if (isbn.length() > 1) formatted.insert(1, "-");
                if (isbn.length() > 6) formatted.insert(7, "-");
                if (isbn.length() > 9) formatted.insert(11, "-");
            } else {
                if (isbn.length() > 3) formatted.insert(3, "-");
                if (isbn.length() > 4) formatted.insert(5, "-");
                if (isbn.length() > 7) formatted.insert(8, "-");
                if (isbn.length() > 12) formatted.insert(13, "-");
            }
            return formatted.toString();
        }
        return isbn;
    }

    private String formatDanacode(String danacode) {
        if (danacode == null) return "";
        danacode = danacode.replaceAll("[^\\d]", "");
        long number;
        try {
            number = Long.parseLong(danacode);
        } catch (NumberFormatException e) {
            return danacode;
        }

        if (danacode.length() >= 3 && danacode.length() <= 12) {
            String firstPart = String.valueOf(number).substring(0, Math.min(3, String.valueOf(number).length()));
            String secondPart = String.valueOf(number).substring(3);
            return firstPart + (secondPart.isEmpty() ? "" : "-" + secondPart);
        }
        return String.valueOf(number);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
