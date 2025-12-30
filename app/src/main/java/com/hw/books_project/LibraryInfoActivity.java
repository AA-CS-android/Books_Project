package com.hw.books_project;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

public class LibraryInfoActivity extends AppCompatActivity {

    private TextView tvLibraryName, tvOpeningHours, tvMembersLabel;
    private ListView lvAdmins, lvMembers;
    private Library library;

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
        tvOpeningHours = findViewById(R.id.tvOpeningHours);
        lvAdmins = findViewById(R.id.lvAdmins);
        tvMembersLabel = findViewById(R.id.tvMembersLabel);
        lvMembers = findViewById(R.id.lvMembers);
    }

    private void displayLibraryInfo() {
        tvLibraryName.setText(library.getName());

        if (library.getOpeningDaysTimes() != null) {
            StringBuilder hoursBuilder = new StringBuilder();
            for (String dayTime : library.getOpeningDaysTimes()) {
                hoursBuilder.append(dayTime).append("\n");
            }
            tvOpeningHours.setText(hoursBuilder.toString());
        }

        if (library.getAdmins() != null) {
            fetchAndDisplayUserNames(library.getAdmins(), lvAdmins);
        }

        // Check if the current user is an admin to show the members list
        User currentUser = FBRef.currentUser;
        if (currentUser != null && library.getAdmins() != null && library.getAdmins().contains(currentUser.getUid())) {
            tvMembersLabel.setVisibility(View.VISIBLE);
            lvMembers.setVisibility(View.VISIBLE);
            if (library.getUsers() != null) {
                fetchAndDisplayUserNames(library.getUsers(), lvMembers);
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
