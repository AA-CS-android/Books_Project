package com.hw.books_project;

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
import com.hw.books_project.models.Library;
import com.hw.books_project.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LibraryInfoActivity extends AppCompatActivity {

    private TextView tvLibraryName, tvMembersLabel;
    private ListView lvAdmins, lvMembers;
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
        lvAdmins = findViewById(R.id.lvAdmins);
        tvMembersLabel = findViewById(R.id.tvMembersLabel);
        lvMembers = findViewById(R.id.lvMembers);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());
    }

    private void displayLibraryInfo() {
        tvLibraryName.setText(library.getName());

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
            FBRef.refUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        names.add(user.getName());
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
