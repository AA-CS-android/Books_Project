package com.hw.books_project.screens.library;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hw.books_project.R;
import com.hw.books_project.objects.Library;
import com.hw.books_project.objects.User;
import com.hw.books_project.utils.FBRef;

import java.util.ArrayList;
import java.util.List;

public class LibraryInfoActivity extends AppCompatActivity {

    private TextView tvLibraryName, tvMembersLabel, tvAdminName;
    private ListView lvMembers;
    private Library library;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_info);

        library = (Library) getIntent().getSerializableExtra("library");

        if (library == null) {
            Toast.makeText(this, "Library data not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        init();
        displayLibraryInfo();
    }

    private void init() {
        tvLibraryName = findViewById(R.id.tvLibraryName);
        tvAdminName = findViewById(R.id.tvAdminName);
        tvMembersLabel = findViewById(R.id.tvMembersLabel);
        lvMembers = findViewById(R.id.lvMembers);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());
    }

    private void displayLibraryInfo() {
        tvLibraryName.setText(library.getName());

        // Fetch and display admin name
        if (library.getAdmin() != null) {
            FBRef.refUsers.child(library.getAdmin()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        tvAdminName.setText(user.getName());
                    } else {
                        tvAdminName.setText("[Unknown Admin]");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    tvAdminName.setText("Error loading admin");
                }
            });
        }

        User currentUser = FBRef.currentUser;
        if (currentUser != null && library.getAdmin() != null && library.getAdmin().equals(currentUser.getUid())) {
            tvMembersLabel.setVisibility(View.VISIBLE);
            lvMembers.setVisibility(View.VISIBLE);
            if (library.getUsers() != null) {
                fetchAndDisplayUserNames(new ArrayList<>(library.getUsers().keySet()), lvMembers);
            }
        }
    }

    private void fetchAndDisplayUserNames(List<String> uids, ListView listView) {
        ArrayList<String> names = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);

        for (String uid : uids) {
            FBRef.refUsers.child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Note: This getValue(User.class) will fail if the user node 
                    // in the DB contains old/corrupted loan objects instead of Longs.
                    try {
                        String user = snapshot.getValue(String.class);
                        if (user != null) {
                            names.add(user);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        names.add("Corrupted User Data (" + uid + ")");
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LibraryInfoActivity.this, "Failed to load user names.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
