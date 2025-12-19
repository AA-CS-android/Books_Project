package com.hw.books_project;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import com.hw.books_project.models.Library;

public class CreateLibraryActivity extends AppCompatActivity {

    private TextInputEditText etLibName;
    private EditText etMaxDuration, etMaxCount, etCooldown;
    private Button btnCreateLibrary;

    // Day CheckBoxes
    private CheckBox cbSun, cbMon, cbTue, cbWed, cbThu, cbFri, cbSat;
    
    // Time Buttons
    private Button btnOpenSun, btnCloseSun;
    private Button btnOpenMon, btnCloseMon;
    private Button btnOpenTue, btnCloseTue;
    private Button btnOpenWed, btnCloseWed;
    private Button btnOpenThu, btnCloseThu;
    private Button btnOpenFri, btnCloseFri;
    private Button btnOpenSat, btnCloseSat;

    // Increment/Decrement Buttons
    private Button btnDurMin5, btnDurMin1, btnDurPls1, btnDurPls5;
    private Button btnCountMin5, btnCountMin1, btnCountPls1, btnCountPls5;
    private Button btnCoolMin5, btnCoolMin1, btnCoolPls1, btnCoolPls5;

    // Tmp lib
    private Library lib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_library);
        init();
    }

    private void init() {
        lib = new Library();
        etLibName = findViewById(R.id.etLibName);
        etMaxDuration = findViewById(R.id.etMaxDuration);
        etMaxCount = findViewById(R.id.etMaxCount);
        etCooldown = findViewById(R.id.etCooldown);
        btnCreateLibrary = findViewById(R.id.btnCreateLibrary);

        // Initialize Day CheckBoxes and Time Buttons
        initDayViews();

        // Initialize Increment/Decrement Buttons
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
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(CreateLibraryActivity.this, (timePicker, selectedHour, selectedMinute) ->
                btn.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)), hour, minute, true);
                //getting warnings when removing "Locale.getDefault()" so left it there
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void initIncDecButtons() {
        // Loan duration
        btnDurMin5 = findViewById(R.id.btnDurMin5);
        btnDurMin1 = findViewById(R.id.btnDurMin1);
        btnDurPls1 = findViewById(R.id.btnDurPls1);
        btnDurPls5 = findViewById(R.id.btnDurPls5);
        setupIncDecListeners(etMaxDuration, btnDurMin5, -5);
        setupIncDecListeners(etMaxDuration, btnDurMin1, -1);
        setupIncDecListeners(etMaxDuration, btnDurPls1, 1);
        setupIncDecListeners(etMaxDuration, btnDurPls5, 5);

        // Books count
        btnCountMin5 = findViewById(R.id.btnCountMin5);
        btnCountMin1 = findViewById(R.id.btnCountMin1);
        btnCountPls1 = findViewById(R.id.btnCountPls1);
        btnCountPls5 = findViewById(R.id.btnCountPls5);
        setupIncDecListeners(etMaxCount, btnCountMin5, -5);
        setupIncDecListeners(etMaxCount, btnCountMin1, -1);
        setupIncDecListeners(etMaxCount, btnCountPls1, 1);
        setupIncDecListeners(etMaxCount, btnCountPls5, 5);

        // Loan cooldown
        btnCoolMin5 = findViewById(R.id.btnCoolMin5);
        btnCoolMin1 = findViewById(R.id.btnCoolMin1);
        btnCoolPls1 = findViewById(R.id.btnCoolPls1);
        btnCoolPls5 = findViewById(R.id.btnCoolPls5);
        setupIncDecListeners(etCooldown, btnCoolMin5, -5);
        setupIncDecListeners(etCooldown, btnCoolMin1, -1);
        setupIncDecListeners(etCooldown, btnCoolPls1, 1);
        setupIncDecListeners(etCooldown, btnCoolPls5, 5);
    }

    private void setupIncDecListeners(EditText et, Button btn, int val) {
        btn.setOnClickListener(v -> {
            try {
                int current = Integer.parseInt(et.getText().toString());
                int newVal = current + val;
                if (newVal < 0) newVal = 0; // Prevent negative values
                et.setText(String.valueOf(newVal));
            } catch (NumberFormatException e) {
                et.setText("0");
            }
        });
    }
    private List<String> getOpeningDaysTimes() {
        List<String> times = null;
        //TODO: get the time and date that the library is open for each day
        return times;
    }
    private void createLibrary() {
        boolean success = false;
        String name = etLibName.getText().toString();
        List<String> openingDaysTimes = getOpeningDaysTimes();
        int maxDuration = Integer.parseInt(etMaxDuration.getText().toString());
        int maxCount = Integer.parseInt(etMaxCount.getText().toString());
        int cooldown = Integer.parseInt(etCooldown.getText().toString());
        //region ERROR_HANDLING
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a library name", Toast.LENGTH_SHORT).show();
        }
        /// TODO: check remote database, if library name already in use then prompt user to change name
        /// TODO: make sure closing time can't be before opening time
        /// TODO: make sure that the users can't loan books for more then a year
        //endregion
        //region CREATE_LIBRARY
        /// TODO: create library, maybe change it so that instead of creating a new lib, just update the lib we created when initializing the class
        //endregion
        /// TODO: try and create a new library on remote database
        if (success)
            finish();
        else
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
    }
}
