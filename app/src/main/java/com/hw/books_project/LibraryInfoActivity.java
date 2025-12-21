package com.hw.books_project;

import android.os.Bundle;
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

    private TextView tvLibraryName, tvOpeningHours;
    private ListView lvAdmins;
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
    }

    private void displayLibraryInfo() {
        tvLibraryName.setText(library.getName());

        // Format and display opening hours
        if (library.getOpeningDaysTimes() != null) {
            StringBuilder hoursBuilder = new StringBuilder();
            for (String dayTime : library.getOpeningDaysTimes()) {
                hoursBuilder.append(dayTime).append("\n");
            }
            tvOpeningHours.setText(hoursBuilder.toString());
        }

        // Fetch and display admin names
        if (library.getAdmins() != null) {
            fetchAdminNames(library.getAdmins());
        }
    }

    private void fetchAdminNames(List<String> adminUids) {
        ArrayList<String> adminNames = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, adminNames);
        lvAdmins.setAdapter(adapter);

        for (String uid : adminUids) {
            FBRef.refUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User admin = snapshot.getValue(User.class);
                    if (admin != null) {
                        adminNames.add(admin.getName());
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LibraryInfoActivity.this, "Failed to load admin names.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
