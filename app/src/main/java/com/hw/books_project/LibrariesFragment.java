package com.hw.books_project;

import android.content.Intent;
import android.os.Bundle;
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
import com.hw.books_project.models.User;

import java.util.ArrayList;

public class LibrariesFragment extends Fragment implements SearchHelper.OnLibrarySelectedListener {

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
        mainAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, libraryList);
        suggestionsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        binding.libsList.setAdapter(mainAdapter);
        binding.searchListView.setAdapter(suggestionsAdapter);

        fetchLibraries();

        SearchHelper.setupSearch(requireContext(), binding.libSearchBar, binding.searchView, binding.searchListView, mainAdapter, suggestionsAdapter, libraryList, this);

        binding.createLibBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CreateLibraryActivity.class);
            startActivity(intent);
        });

        binding.libsList.setOnItemClickListener((parent, view, position, id) -> {
            Library selectedLibrary = mainAdapter.getItem(position);
            if (selectedLibrary != null) {
                onLibrarySelected(selectedLibrary);
            }
        });
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

    @Override
    public void onLibrarySelected(Library library) {
        User currentUser = FBRef.currentUser;
        if (currentUser != null &&
                ((library.getUsers() != null && library.getUsers().containsKey(currentUser.getUid())) ||
                (library.getAdmins() != null && library.getAdmins().containsKey(currentUser.getUid())))
        ) {
            // User is a member or an admin, go to the library view
            Intent intent = new Intent(requireContext(), LibraryViewActivity.class);
            intent.putExtra("library", library);
            startActivity(intent);
        } else {
            // User is not a member, go to the join screen
            Intent intent = new Intent(requireContext(), JoinLibraryActivity.class);
            intent.putExtra("library", library);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
