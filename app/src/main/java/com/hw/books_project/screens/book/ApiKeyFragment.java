package com.hw.books_project.screens.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hw.books_project.R;
import com.hw.books_project.utils.ApiKeyDataStore;

public class ApiKeyFragment extends Fragment {

    private EditText etApiKey;
    private Button btnSaveApiKey;
    private ApiKeyDataStore apiKeyDataStore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_api_key, container, false);
        etApiKey = view.findViewById(R.id.etApiKey);
        btnSaveApiKey = view.findViewById(R.id.btnSaveApiKey);

        apiKeyDataStore = new ApiKeyDataStore(getContext());

        etApiKey.setText(apiKeyDataStore.getApiKey());

        btnSaveApiKey.setOnClickListener(v -> {
            String apiKey = etApiKey.getText().toString().trim();
            if (apiKey.isEmpty()) {
                Toast.makeText(getContext(), "Please enter an API key", Toast.LENGTH_SHORT).show();
            } else {
                apiKeyDataStore.saveApiKey(apiKey);
                Toast.makeText(getContext(), "API Key saved", Toast.LENGTH_SHORT).show();
                ((AddBookApiActivity) getActivity()).onApiKeySaved();
            }
        });

        return view;
    }
}
