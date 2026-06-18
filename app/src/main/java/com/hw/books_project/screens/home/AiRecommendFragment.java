package com.hw.books_project.screens.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hw.books_project.R;
import com.hw.books_project.utils.GeminiCallback;
import com.hw.books_project.utils.GeminiManager;

// REQUIREMENT: 9.5 Fragment
public class AiRecommendFragment extends Fragment {

    private TextInputEditText etUserInput;
    private Button btnFindBook;
    private ProgressBar progressBar;
    private MaterialCardView cardResult;
    private TextView tvRecommendation;
    private Button btnReset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_recommend, container, false);

        etUserInput = view.findViewById(R.id.etUserInput);
        btnFindBook = view.findViewById(R.id.btnFindBook);
        progressBar = view.findViewById(R.id.progressBar);
        cardResult = view.findViewById(R.id.cardResult);
        tvRecommendation = view.findViewById(R.id.tvRecommendation);
        btnReset = view.findViewById(R.id.btnReset);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnFindBook.setOnClickListener(v -> findBook());
        btnReset.setOnClickListener(v -> resetState());
    }

    private void findBook() {
        hideKeyboard();
        
        String userInput = etUserInput.getText().toString().trim();
        if (userInput.isEmpty()) {
            etUserInput.setError("Please describe what you're looking for");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnFindBook.setEnabled(false);

        String systemPrompt = getString(R.string.librarian_system_prompt);
        String finalPrompt = systemPrompt + "\n\nUser request: " + userInput;

        GeminiManager.getInstance().sendTextPrompt(finalPrompt, new GeminiCallback() {
            @Override
            public void onSuccess(String result) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnFindBook.setEnabled(true);
                        tvRecommendation.setText(result);
                        cardResult.setVisibility(View.VISIBLE);
                    });
                }
            }

            @Override
            public void onFailure(Throwable error) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnFindBook.setEnabled(true);
                        Snackbar.make(requireView(), "Error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    private void hideKeyboard() {
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void resetState() {
        etUserInput.setText("");
        cardResult.setVisibility(View.GONE);
        tvRecommendation.setText("");
    }
}
