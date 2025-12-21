package com.hw.books_project;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateLibraryActivity extends AppCompatActivity {

    private TextInputEditText etLibName;
    private EditText etMaxDuration, etMaxCount, etCooldown;
    private Button btnCreateLibrary;

    private CheckBox cbSun, cbMon, cbTue, cbWed, cbThu, cbFri, cbSat;
    private Button btnOpenSun, btnCloseSun, btnOpenMon, btnCloseMon, btnOpenTue, btnCloseTue, btnOpenWed, btnCloseWed, btnOpenThu, btnCloseThu, btnOpenFri, btnCloseFri, btnOpenSat, btnCloseSat;

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

        initDayViews();
        initIncDecButtons();

        btnCreateLibrary.setOnClickListener(v -> createLibrary());
    }

    private void initDayViews() {
        cbSun = findViewById(R.id.cbSun); btnOpenSun = findViewById(R.id.btnOpenSun); btnCloseSun = findViewById(R.id.btnCloseSun);
        cbMon = findViewById(R.id.cbMon); btnOpenMon = findViewById(R.id.btnOpenMon); btnCloseMon = findViewById(R.id.btnCloseMon);
        cbTue = findViewById(R.id.cbTue); btnOpenTue = findViewById(R.id.btnOpenTue); btnCloseTue = findViewById(R.id.btnCloseTue);
        cbWed = findViewById(R.id.cbWed); btnOpenWed = findViewById(R.id.btnOpenWed); btnCloseWed = findViewById(R.id.btnCloseWed);
        cbThu = findViewById(R.id.cbThu); btnOpenThu = findViewById(R.id.btnOpenThu); btnCloseThu = findViewById(R.id.btnCloseThu);
        cbFri = findViewById(R.id.cbFri); btnOpenFri = findViewById(R.id.btnOpenFri); btnCloseFri = findViewById(R.id.btnCloseFri);
        cbSat = findViewById(R.id.cbSat); btnOpenSat = findViewById(R.id.btnOpenSat); btnCloseSat = findViewById(R.id.btnCloseSat);

        setupDayLogic(cbSun, btnOpenSun, btnCloseSun);
        setupDayLogic(cbMon, btnOpenMon, btnCloseMon);
        setupDayLogic(cbTue, btnOpenTue, btnCloseTue);
        setupDayLogic(cbWed, btnOpenWed, btnCloseWed);
        setupDayLogic(cbThu, btnOpenThu, btnCloseThu);
        setupDayLogic(cbFri, btnOpenFri, btnCloseFri);
        setupDayLogic(cbSat, btnOpenSat, btnCloseSat);
    }

    private void setupDayLogic(CheckBox cb, Button btnOpen, Button btnClose) {
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnOpen.setEnabled(isChecked);
            btnClose.setEnabled(isChecked);
        });

        btnOpen.setOnClickListener(v -> showTimePicker(btnOpen));
        btnClose.setOnClickListener(v -> showTimePicker(btnClose));
    }

    private void showTimePicker(Button btn) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(CreateLibraryActivity.this, (timePicker, selectedHour, selectedMinute) ->
                btn.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)), hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
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

    private List<String> getOpeningDaysTimes() {
        List<String> times = new ArrayList<>();
        CheckBox[] checkBoxes = {cbSun, cbMon, cbTue, cbWed, cbThu, cbFri, cbSat};
        Button[] openButtons = {btnOpenSun, btnOpenMon, btnOpenTue, btnOpenWed, btnOpenThu, btnOpenFri, btnOpenSat};
        Button[] closeButtons = {btnCloseSun, btnCloseMon, btnCloseTue, btnCloseWed, btnCloseThu, btnCloseFri, btnCloseSat};

        for (int i = 0; i < checkBoxes.length; i++) {
            if (checkBoxes[i].isChecked()) {
                String openTime = openButtons[i].getText().toString();
                String closeTime = closeButtons[i].getText().toString();

                if (isTimeValid(openTime, closeTime)) {
                    times.add(checkBoxes[i].getText().toString() + ":" + openTime + "-" + closeTime);
                } else {
                    Toast.makeText(this, "Closing time must be after opening time for " + checkBoxes[i].getText(), Toast.LENGTH_SHORT).show();
                    return null; // Validation failure
                }
            }
        }
        if (times.isEmpty()) {
            Toast.makeText(this, "Please select at least one opening day.", Toast.LENGTH_SHORT).show();
            return null;
        }
        return times;
    }

    private boolean isTimeValid(String open, String close) {
        return open.compareTo(close) < 0;
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

        List<String> openingDaysTimes = getOpeningDaysTimes();
        if (openingDaysTimes == null) {
            return; // Validation failed
        }

        Query nameQuery = FBRef.refLibraries.orderByChild("name").equalTo(name);
        nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(CreateLibraryActivity.this, "A library with this name already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    saveLibraryToDatabase(name, openingDaysTimes, maxDuration);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CreateLibraryActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLibraryToDatabase(String name, List<String> openingDaysTimes, int maxDuration) {
        int maxCount = Integer.parseInt(etMaxCount.getText().toString());
        int cooldown = Integer.parseInt(etCooldown.getText().toString());

        String key = FBRef.refLibraries.push().getKey();
        if (key == null) {
            Toast.makeText(this, "Failed to create library key.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create admin list and add current user
        ArrayList<String> admins = new ArrayList<>();
        if (FBRef.currentUser != null) {
            admins.add(FBRef.currentUser.getUid());
        }

        Library newLibrary = new Library();
        newLibrary.setUid(key);
        newLibrary.setName(name);
        newLibrary.setMaxLoanDuration(maxDuration);
        newLibrary.setMaxLoanCount(maxCount);
        newLibrary.setReloanCooldown(cooldown);
        newLibrary.setOpeningDaysTimes(openingDaysTimes);
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
