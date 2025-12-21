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


/**
 * Activity for creating a new library.
 * This class handles the UI and logic for creating a new library, including setting its name,
 * opening hours, and loan rules.
 */
public class CreateLibraryActivity extends AppCompatActivity {

    // UI Elements
    private TextInputEditText etLibName;
    private EditText etMaxDuration, etMaxCount, etCooldown;
    private Button btnCreateLibrary;

    // Day CheckBoxes and Time Buttons
    private CheckBox cbSun, cbMon, cbTue, cbWed, cbThu, cbFri, cbSat;
    private Button btnOpenSun, btnCloseSun, btnOpenMon, btnCloseMon, btnOpenTue, btnCloseTue, btnOpenWed, btnCloseWed, btnOpenThu, btnCloseThu, btnOpenFri, btnCloseFri, btnOpenSat, btnCloseSat;

    /**
     * Called when the activity is first created.
     * This is where you should do all of your normal static set up: create views, bind data to lists, etc.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_library);
        init();
    }

    /**
     * Initializes all UI elements and sets up the necessary listeners.
     */
    private void init() {
        // Initialize basic UI elements
        etLibName = findViewById(R.id.etLibName);
        etMaxDuration = findViewById(R.id.etMaxDuration);
        etMaxCount = findViewById(R.id.etMaxCount);
        etCooldown = findViewById(R.id.etCooldown);
        btnCreateLibrary = findViewById(R.id.btnCreateLibrary);

        // Initialize the views for day selection and time pickers
        initDayViews();
        // Initialize the increment and decrement buttons for loan rules
        initIncDecButtons();

        // Set a click listener for the create library button
        btnCreateLibrary.setOnClickListener(v -> createLibrary());
    }

    /**
     * Initializes all the CheckBox and Button views for each day of the week.
     */
    private void initDayViews() {
        cbSun = findViewById(R.id.cbSun); btnOpenSun = findViewById(R.id.btnOpenSun); btnCloseSun = findViewById(R.id.btnCloseSun);
        cbMon = findViewById(R.id.cbMon); btnOpenMon = findViewById(R.id.btnOpenMon); btnCloseMon = findViewById(R.id.btnCloseMon);
        cbTue = findViewById(R.id.cbTue); btnOpenTue = findViewById(R.id.btnOpenTue); btnCloseTue = findViewById(R.id.btnCloseTue);
        cbWed = findViewById(R.id.cbWed); btnOpenWed = findViewById(R.id.btnOpenWed); btnCloseWed = findViewById(R.id.btnCloseWed);
        cbThu = findViewById(R.id.cbThu); btnOpenThu = findViewById(R.id.btnOpenThu); btnCloseThu = findViewById(R.id.btnCloseThu);
        cbFri = findViewById(R.id.cbFri); btnOpenFri = findViewById(R.id.btnOpenFri); btnCloseFri = findViewById(R.id.btnCloseFri);
        cbSat = findViewById(R.id.cbSat); btnOpenSat = findViewById(R.id.btnOpenSat); btnCloseSat = findViewById(R.id.btnCloseSat);

        // Set up the logic for each day (enable/disable buttons and set time pickers)
        setupDayLogic(cbSun, btnOpenSun, btnCloseSun);
        setupDayLogic(cbMon, btnOpenMon, btnCloseMon);
        setupDayLogic(cbTue, btnOpenTue, btnCloseTue);
        setupDayLogic(cbWed, btnOpenWed, btnCloseWed);
        setupDayLogic(cbThu, btnOpenThu, btnCloseThu);
        setupDayLogic(cbFri, btnOpenFri, btnCloseFri);
        setupDayLogic(cbSat, btnOpenSat, btnCloseSat);
    }

    /**
     * Sets up the logic for a single day's row.
     * This includes enabling/disabling the time buttons based on the checkbox state and setting
     * up the time picker dialogs for the open and close buttons.
     * @param cb The CheckBox for the day.
     * @param btnOpen The Button to set the opening time.
     * @param btnClose The Button to set the closing time.
     */
    private void setupDayLogic(CheckBox cb, Button btnOpen, Button btnClose) {
        // Enable or disable the time buttons when the checkbox is toggled
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnOpen.setEnabled(isChecked);
            btnClose.setEnabled(isChecked);
        });

        // Set click listeners to show the time picker dialog
        btnOpen.setOnClickListener(v -> showTimePicker(btnOpen));
        btnClose.setOnClickListener(v -> showTimePicker(btnClose));
    }

    /**
     * Displays a TimePickerDialog to allow the user to select a time.
     * The selected time is then set as the text of the provided button.
     * @param btn The button whose text will be updated with the selected time.
     */
    private void showTimePicker(Button btn) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(CreateLibraryActivity.this, (timePicker, selectedHour, selectedMinute) ->
                btn.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)), hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    /**
     * Initializes all the increment and decrement buttons for the loan rule inputs.
     */
    private void initIncDecButtons() {
        // Set up listeners for Max Loan Duration buttons
        setupIncDecListeners(etMaxDuration, findViewById(R.id.btnDurMin5), -5);
        setupIncDecListeners(etMaxDuration, findViewById(R.id.btnDurMin1), -1);
        setupIncDecListeners(etMaxDuration, findViewById(R.id.btnDurPls1), 1);
        setupIncDecListeners(etMaxDuration, findViewById(R.id.btnDurPls5), 5);

        // Set up listeners for Max Loan Count buttons
        setupIncDecListeners(etMaxCount, findViewById(R.id.btnCountMin5), -5);
        setupIncDecListeners(etMaxCount, findViewById(R.id.btnCountMin1), -1);
        setupIncDecListeners(etMaxCount, findViewById(R.id.btnCountPls1), 1);
        setupIncDecListeners(etMaxCount, findViewById(R.id.btnCountPls5), 5);

        // Set up listeners for Reloan Cooldown buttons
        setupIncDecListeners(etCooldown, findViewById(R.id.btnCoolMin5), -5);
        setupIncDecListeners(etCooldown, findViewById(R.id.btnCoolMin1), -1);
        setupIncDecListeners(etCooldown, findViewById(R.id.btnCoolPls1), 1);
        setupIncDecListeners(etCooldown, findViewById(R.id.btnCoolPls5), 5);
    }

    /**
     * Sets up a click listener for an increment or decrement button to modify an EditText value.
     * @param et The EditText to modify.
     * @param btn The Button that triggers the change.
     * @param val The value to add (can be negative).
     */
    private void setupIncDecListeners(EditText et, Button btn, int val) {
        btn.setOnClickListener(v -> {
            try {
                int current = Integer.parseInt(et.getText().toString());
                int newVal = current + val;
                // Prevent negative values
                if (newVal < 0) newVal = 0;
                et.setText(String.valueOf(newVal));
            } catch (NumberFormatException e) {
                // If the EditText is empty or has invalid text, start from 0
                et.setText("0");
            }
        });
    }

    /**
     * Collects and validates the opening days and times from the UI.
     * @return A list of strings representing the opening hours for each selected day, or null if validation fails.
     */
    private List<String> getOpeningDaysTimes() {
        List<String> times = new ArrayList<>();
        CheckBox[] checkBoxes = {cbSun, cbMon, cbTue, cbWed, cbThu, cbFri, cbSat};
        Button[] openButtons = {btnOpenSun, btnOpenMon, btnOpenTue, btnOpenWed, btnOpenThu, btnOpenFri, btnOpenSat};
        Button[] closeButtons = {btnCloseSun, btnCloseMon, btnCloseTue, btnCloseWed, btnCloseThu, btnCloseFri, btnCloseSat};

        for (int i = 0; i < checkBoxes.length; i++) {
            if (checkBoxes[i].isChecked()) {
                String openTime = openButtons[i].getText().toString();
                String closeTime = closeButtons[i].getText().toString();

                // Validate that the closing time is after the opening time
                if (isTimeValid(openTime, closeTime)) {
                    // Format the string as "Day:HH:MM-HH:MM"
                    times.add(checkBoxes[i].getText().toString() + ":" + openTime + "-" + closeTime);
                } else {
                    Toast.makeText(this, "Closing time must be after opening time for " + checkBoxes[i].getText(), Toast.LENGTH_SHORT).show();
                    return null; // Indicates validation failure
                }
            }
        }
        // Ensure at least one day is selected
        if (times.isEmpty()) {
            Toast.makeText(this, "Please select at least one opening day.", Toast.LENGTH_SHORT).show();
            return null;
        }
        return times;
    }

    /**
     * Validates if the closing time is after the opening time.
     * @param open The opening time in "HH:MM" format.
     * @param close The closing time in "HH:MM" format.
     * @return True if the time range is valid, false otherwise.
     */
    private boolean isTimeValid(String open, String close) {
        return open.compareTo(close) < 0;
    }

    /**
     * The main method for creating the library. It performs validation and then triggers the database save.
     */
    private void createLibrary() {
        // Validate library name
        String name = etLibName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a library name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate max loan duration
        int maxDuration = Integer.parseInt(etMaxDuration.getText().toString());
        if (maxDuration <= 0 || maxDuration > 365) {
            Toast.makeText(this, "Max loan duration must be between 1 and 365 days", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate opening days and times
        List<String> openingDaysTimes = getOpeningDaysTimes();
        if (openingDaysTimes == null) {
            return;
        }

        // Validate max loan count
        int maxCount = Integer.parseInt(etMaxCount.getText().toString());
        if (maxCount > 50 || maxCount <= 0) {
            Toast.makeText(this, "Max loan count must between 1 and 50", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate reloan cooldown
        int cooldown = Integer.parseInt(etCooldown.getText().toString());
        if (cooldown > 30 || cooldown <= 0) {
            Toast.makeText(this, "Reloan cooldown must between 1 and 30 days", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for unique library name in Firebase before saving
        Query nameQuery = FBRef.refLibraries.orderByChild("name").equalTo(name);
        nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // A library with this name already exists
                    Toast.makeText(CreateLibraryActivity.this, "A library with this name already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    // Name is unique, proceed to save the library to the database
                    saveLibraryToDatabase(name, openingDaysTimes, maxDuration, maxCount, cooldown);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential database errors
                Toast.makeText(CreateLibraryActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Saves the new library data to the Firebase Realtime Database.
     * @param name The name of the library.
     * @param openingDaysTimes The list of opening days and times.
     * @param maxDuration The maximum loan duration in days.
     * @param maxCount The maximum number of loans per user.
     * @param cooldown The cooldown period between loans in days.
     */
    private void saveLibraryToDatabase(String name, List<String> openingDaysTimes, int maxDuration, int maxCount, int cooldown) {

        // Generate a unique key for the new library
        String key = FBRef.refLibraries.push().getKey();
        if (key == null) {
            Toast.makeText(this, "Failed to create library key.", Toast.LENGTH_SHORT).show();
            return;
        }
        Library newLibrary = new Library();
        newLibrary.setUid(key);
        newLibrary.setName(name);
        newLibrary.setMaxLoanDuration(maxDuration);
        newLibrary.setMaxLoanCount(maxCount);
        newLibrary.setReloanCooldown(cooldown);
        newLibrary.setOpeningDaysTimes(openingDaysTimes);

        // Save the new library object to the database
        FBRef.refLibraries.child(key).setValue(newLibrary).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CreateLibraryActivity.this, "Library created successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(CreateLibraryActivity.this, "Failed to create library.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
