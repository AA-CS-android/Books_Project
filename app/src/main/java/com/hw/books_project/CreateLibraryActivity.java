package com.hw.books_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hw.books_project.models.Library;

import java.util.HashMap;
import java.util.Map;

public class CreateLibraryActivity extends AppCompatActivity {

    private TextInputEditText etLibName;
    private EditText etMaxDuration, etMaxCount, etCooldown;
    private Button btnCreateLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_library);
        init();
    }

    private void init() {
        etLibName = findViewById(R.id.etLibName);
        etMaxDuration = findViewById(R.id.etMaxDuration);
        etMaxCount = findViewById(R.id.etMaxCount);
        etCooldown = findViewById(R.id.etCooldown);
        btnCreateLibrary = findViewById(R.id.btnCreateLibrary);

        initIncDecButtons();

        btnCreateLibrary.setOnClickListener(v -> createLibrary());
    }

    private void initIncDecButtons() {
        setupIncDecListeners(etMaxDuration, findViewById(R.id.btnDurMin5), -5);
        setupIncDecListeners(etMaxDuration, findViewById(R.id.btnDurMin1), -1);
        setupIncDecListeners(etMaxDuration, findViewById(R.id.btnDurPls1), 1);
        setupIncDecListeners(etMaxDuration, findViewById(R.id.btnDurPls5), 5);
        setupIncDecListeners(etMaxCount, findViewById(R.id.btnCountMin5), -5);
        setupIncDecListeners(etMaxCount, findViewById(R.id.btnCountMin1), -1);
        setupIncDecListeners(etMaxCount, findViewById(R.id.btnCountPls1), 1);
        setupIncDecListeners(etMaxCount, findViewById(R.id.btnCountPls5), 5);
        setupIncDecListeners(etCooldown, findViewById(R.id.btnCoolMin5), -5);
        setupIncDecListeners(etCooldown, findViewById(R.id.btnCoolMin1), -1);
        setupIncDecListeners(etCooldown, findViewById(R.id.btnCoolPls1), 1);
        setupIncDecListeners(etCooldown, findViewById(R.id.btnCoolPls5), 5);
    }

    private void setupIncDecListeners(EditText et, Button btn, int val) {
        btn.setOnClickListener(v -> {
            try {
                int current = Integer.parseInt(et.getText().toString());
                int newVal = current + val;
                if (newVal < 0) newVal = 0;
                et.setText(String.valueOf(newVal));
            } catch (NumberFormatException e) {
                et.setText("0");
            }
        });
    }

    private void createLibrary() {
        String name = etLibName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a library name", Toast.LENGTH_SHORT).show();
            return;
        }

        int maxDuration = Integer.parseInt(etMaxDuration.getText().toString());
        if (maxDuration <= 0 || maxDuration > 365) {
            Toast.makeText(this, "Max loan duration must be between 1 and 365 days", Toast.LENGTH_SHORT).show();
            return;
        }

        Query nameQuery = FBRef.refLibraries.orderByChild("name").equalTo(name);
        nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(CreateLibraryActivity.this, "A library with this name already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    saveLibraryToDatabase(name, maxDuration);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CreateLibraryActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLibraryToDatabase(String name, int maxDuration) {
        int maxCount = Integer.parseInt(etMaxCount.getText().toString());
        int cooldown = Integer.parseInt(etCooldown.getText().toString());

        String key = FBRef.refLibraries.push().getKey();
        if (key == null) {
            Toast.makeText(this, "Failed to create library key.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Boolean> admins = new HashMap<>();
        if (FBRef.currentUser != null) {
            admins.put(FBRef.currentUser.getUid(), true);
        }

        Library newLibrary = new Library();
        newLibrary.setLibraryId(key);
        newLibrary.setName(name);
        newLibrary.setMaxLoanDuration(maxDuration);
        newLibrary.setMaxLoanCount(maxCount);
        newLibrary.setReloanCooldown(cooldown);
        newLibrary.setAdmins(admins);

        FBRef.refLibraries.child(key).setValue(newLibrary).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CreateLibraryActivity.this, "Library created successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CreateLibraryActivity.this, "Failed to create library.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}