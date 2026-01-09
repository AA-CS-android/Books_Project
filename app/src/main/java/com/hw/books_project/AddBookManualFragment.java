package com.hw.books_project;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hw.books_project.databinding.FragmentAddBookManualBinding;

public class AddBookManualFragment extends Fragment {

    private FragmentAddBookManualBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBookManualBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupFormatting();
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
            // Example format: 978-3-16-148410-0
            StringBuilder formatted = new StringBuilder(isbn);
            if (isbn.length() == 10) { // ISBN-10
                if (isbn.length() > 1) formatted.insert(1, "-");
                if (isbn.length() > 6) formatted.insert(7, "-");
                if (isbn.length() > 9) formatted.insert(11, "-");
            } else { // ISBN-13
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
            // Format: XXX-XXXXXXXXX
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
