package com.hw.books_project.screens.book;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hw.books_project.R;
import com.hw.books_project.adapters.BookAdapter;
import com.hw.books_project.models.Book;
import com.hw.books_project.models.Library;
import com.hw.books_project.utils.ApiKeyDataStore;
import com.hw.books_project.utils.NLIOpenLibraryClient;

import java.util.ArrayList;
import java.util.List;

public class AddBookSearchApiFragment extends Fragment {

    private Library library;
    private EditText etTitle, etAuthor, etIsbn;
    private RadioGroup rgLanguage;
    private Button btnSearch, btnLoadMore, btnManageApiKey;
    private ListView lvResults;

    private ApiKeyDataStore apiKeyDataStore;
    private NLIOpenLibraryClient.QueryBuilder queryBuilder;
    private BookAdapter bookAdapter;
    private final ArrayList<Book> bookList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            library = (Library) getArguments().getSerializable("library");
        }
        apiKeyDataStore = new ApiKeyDataStore(getContext());
        queryBuilder = new NLIOpenLibraryClient.QueryBuilder();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book_search_api, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etAuthor = view.findViewById(R.id.etAuthor);
        etIsbn = view.findViewById(R.id.etIsbn);
        rgLanguage = view.findViewById(R.id.rgLanguage);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnLoadMore = view.findViewById(R.id.btnLoadMore);
        btnManageApiKey = view.findViewById(R.id.btnManageApiKey);
        lvResults = view.findViewById(R.id.lvResults);

        bookAdapter = new BookAdapter(getContext(), bookList);
        lvResults.setAdapter(bookAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnManageApiKey.setOnClickListener(v -> ((AddBookApiActivity) getActivity()).showApiKeyScreen());

        btnSearch.setOnClickListener(v -> performSearch(true));

        btnLoadMore.setOnClickListener(v -> performSearch(false));

        lvResults.setOnItemClickListener((parent, view1, position, id) -> {
            Book selectedBook = bookList.get(position);
            Toast.makeText(getContext(), selectedBook.getName() + " pressed", Toast.LENGTH_SHORT).show();
        });
    }

    private void performSearch(boolean isNewSearch) {
        String apiKey = apiKeyDataStore.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            Toast.makeText(getContext(), "Please set an API key first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isNewSearch) {
            bookList.clear();
            queryBuilder = new NLIOpenLibraryClient.QueryBuilder();
        } else {
            queryBuilder.nextPage();
        }

        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String isbn = etIsbn.getText().toString().trim();

        if (title.isEmpty() && author.isEmpty() && isbn.isEmpty()) {
            Toast.makeText(getContext(), "Please fill at least one search field.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!title.isEmpty()) queryBuilder.searchTitle(title);
        if (!author.isEmpty()) queryBuilder.addCondition("creator", "contains", author);
        if (!isbn.isEmpty()) queryBuilder.searchISNB(isbn);

        int selectedLangId = rgLanguage.getCheckedRadioButtonId();
        if (selectedLangId == R.id.rbLangHebrew) {
            queryBuilder.searchLanguage("heb");
        } else if (selectedLangId == R.id.rbLangEnglish) {
            queryBuilder.searchLanguage("eng");
        }

        NLIOpenLibraryClient client = new NLIOpenLibraryClient(apiKey);
        client.executeSearch(queryBuilder, new NLIOpenLibraryClient.SearchCallback() {
            @Override
            public void onResult(List<Book> books) {
                if (books.isEmpty() && isNewSearch) {
                    Toast.makeText(getContext(), "No results found.", Toast.LENGTH_SHORT).show();
                    btnLoadMore.setVisibility(View.GONE);
                } else {
                    bookList.addAll(books);
                    btnLoadMore.setVisibility(View.VISIBLE);
                }
                bookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API Error", e.getMessage());
                btnLoadMore.setVisibility(View.GONE);
            }
        });
    }
}
