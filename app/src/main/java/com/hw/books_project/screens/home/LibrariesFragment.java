package com.hw.books_project.screens.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hw.books_project.screens.library.CreateLibraryActivity;
import com.hw.books_project.screens.library.JoinLibraryActivity;
import com.hw.books_project.screens.library.LibraryViewActivity;
import com.hw.books_project.databinding.FragmentLibariesBinding;
import com.hw.books_project.objects.Library;
import com.hw.books_project.utils.FBRef;
import com.hw.books_project.utils.SearchHelper;

import java.util.ArrayList;

public class LibrariesFragment extends Fragment implements SearchHelper.OnItemSelectedListener<Library> {

    private FragmentLibariesBinding binding;
    private ArrayAdapter<Library> mainAdapter;
    private ArrayAdapter<Library> suggestionsAdapter;
    private final ArrayList<Library> joinedLibrariesList = new ArrayList<>();
    private final ArrayList<Library> searchResults = new ArrayList<>();

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
        mainAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, joinedLibrariesList);
        suggestionsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, searchResults);
        binding.libsList.setAdapter(mainAdapter);
        binding.searchListView.setAdapter(suggestionsAdapter);

        fetchJoinedLibraries();
        setupServerSideSearch();

        binding.createLibBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CreateLibraryActivity.class);
            startActivity(intent);
        });

        binding.libsList.setOnItemClickListener((parent, view, position, id) -> {
            Library selectedLibrary = mainAdapter.getItem(position);
            if (selectedLibrary != null) {
                onItemSelected(selectedLibrary);
            }
        });

        binding.searchListView.setOnItemClickListener((parent, view, position, id) -> {
            Library selectedLibrary = suggestionsAdapter.getItem(position);
            if (selectedLibrary != null) {
                binding.searchView.hide();
                onItemSelected(selectedLibrary);
            }
        });

        binding.libSearchBar.setOnClickListener(v -> binding.searchView.show());
    }

    private void fetchJoinedLibraries() {
        FirebaseUser firebaseUser = FBRef.refAuth.getCurrentUser();
        if (firebaseUser == null) return;
        String uid = firebaseUser.getUid();

        // 1. Keep the SMALL index node synced for offline availability of the ID list
        DatabaseReference userLibsRef = FBRef.refUserLibraries.child(uid);
        userLibsRef.keepSynced(true);

        // 2. Listen to the ID list. This will trigger from cache instantly.
        userLibsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    joinedLibrariesList.clear();
                    mainAdapter.notifyDataSetChanged();
                    return;
                }
                
                ArrayList<String> activeIds = new ArrayList<>();
                for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                    String libId = idSnapshot.getKey();
                    if (libId != null) {
                        activeIds.add(libId);
                        // Fetch details only if not already in list or to refresh
                        fetchLibraryDetails(libId);
                    }
                }
                
                // Cleanup removed libraries
                joinedLibrariesList.removeIf(lib -> !activeIds.contains(lib.getLibraryId()));
                mainAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null)
                    Toast.makeText(requireContext(), "Sync error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLibraryDetails(String libId) {
        // Use single value event to reduce traffic. 
        // Firebase persistence will still serve the cached version immediately.
        FBRef.refLibraries.child(libId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Library library = snapshot.getValue(Library.class);
                if (library != null) {
                    boolean found = false;
                    for (int i = 0; i < joinedLibrariesList.size(); i++) {
                        if (joinedLibrariesList.get(i).getLibraryId().equals(library.getLibraryId())) {
                            joinedLibrariesList.set(i, library);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        joinedLibrariesList.add(library);
                    }
                    mainAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupServerSideSearch() {
        binding.searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String queryText = s.toString().trim();
                if (queryText.length() >= 1) {
                    performServerSearch(queryText);
                } else {
                    searchResults.clear();
                    suggestionsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performServerSearch(String queryText) {
        Query query = FBRef.refLibraries.orderByChild("name")
                .startAt(queryText)
                .endAt(queryText + "\uf8ff")
                .limitToFirst(10);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchResults.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Library library = data.getValue(Library.class);
                    if (library != null) {
                        searchResults.add(library);
                    }
                }
                suggestionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Search", "Search failed: " + error.getMessage());
            }
        });
    }

    @Override
    public void onItemSelected(Library library) {
        FirebaseUser firebaseUser = FBRef.refAuth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            if ((library.getUsers() != null && library.getUsers().containsKey(uid)) ||
                (library.getAdmin() != null && library.getAdmin().equals(uid))) {
                Intent intent = new Intent(requireContext(), LibraryViewActivity.class);
                intent.putExtra("library", library);
                startActivity(intent);
            } else {
                Intent intent = new Intent(requireContext(), JoinLibraryActivity.class);
                intent.putExtra("library", library);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
