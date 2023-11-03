package com.example.hikermanagementapp.Hike;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.hikermanagementapp.Database.Hike;
import com.example.hikermanagementapp.Database.MyDbHelper;
import com.example.hikermanagementapp.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class UpdateHikeActivity extends AppCompatActivity {
    // UI elements
    TextInputEditText name, location, description, length, terrain, difficultyLevel, weatherCondition;
    EditText dateTime;
    Hike selectedHike;
    Button btnSave;
    Calendar calendar;
    RadioGroup radioGroup;
    RadioButton yes, no, selectedRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_hike);

        // set status bar color
        setStatusColor();
        // find all elements
        findAllElements();

        getAndDisplayInfo(); // get selected hike info and display it
        // set date picker
        dateTimePicker();
        // when click save button
        whenClickSave();
    }

    private void whenClickSave() {
        btnSave.setOnClickListener(view -> checkCredentials());
    }

    private void checkCredentials() {
        String hikeName = Objects.requireNonNull(name.getText()).toString().trim();
        String hikeLocation = Objects.requireNonNull(location.getText()).toString().trim();
        String dateT = dateTime.getText().toString().trim();
        String hikeLength = Objects.requireNonNull(length.getText()).toString().trim();
        String terrainType = Objects.requireNonNull(terrain.getText()).toString().trim();
        String weather_condition = Objects.requireNonNull(weatherCondition.getText()).toString().trim();
        String difficulty_level = Objects.requireNonNull(difficultyLevel.getText()).toString().trim();

        radioGroup = findViewById(R.id.radioGroup);
        selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());

        if (hikeName.isEmpty()) {
            showError(name);
        } else if (hikeLocation.isEmpty()) {
            showError(location);
        } else if (dateT.isEmpty()) {
            showError(dateTime);
        } else if (hikeLength.isEmpty()) {
            showError(length);
        } else if (terrainType.isEmpty()) {
            showError(terrain);
        } else if (weather_condition.isEmpty()) {
            showError(weatherCondition);
        } else if (difficulty_level.isEmpty()) {
            showError(difficultyLevel);
        } else {
            updateHike();
        }
    }

    private void updateHike() {
        MyDbHelper myDB = new MyDbHelper(this);
        radioGroup = findViewById(R.id.radioGroup);
        selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        String parkingAvailable = selectedRadioButton.getText().toString();

        selectedHike.setName(Objects.requireNonNull(name.getText()).toString().trim());
        selectedHike.setLocation(Objects.requireNonNull(location.getText()).toString().trim());
        selectedHike.setDate(dateTime.getText().toString().trim());
        selectedHike.setDescription(Objects.requireNonNull(description.getText()).toString().trim());
        selectedHike.setLength(Objects.requireNonNull(length.getText()).toString().trim());
        selectedHike.setTerrainType(Objects.requireNonNull(terrain.getText()).toString().trim());
        selectedHike.setWeatherCondition(Objects.requireNonNull(weatherCondition.getText()).toString().trim());
        selectedHike.setDifficulty(Objects.requireNonNull(difficultyLevel.getText()).toString().trim());
        selectedHike.setParkingAvailable(parkingAvailable);

        long result = myDB.updateHike(selectedHike);
        if (result == -1) {
            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Update Successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UpdateHikeActivity.this, HikeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void showError(EditText input) {
        input.setError("This is a required field");
        input.requestFocus();
    }

    private void dateTimePicker() {
        calendar = Calendar.getInstance(); // get current date
        //Date Picker for EditText Date From
        DatePickerDialog.OnDateSetListener datePickerStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateCalendar();
            }

            private void updateCalendar() {
                String format = "dd MMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                dateTime.setText(sdf.format(calendar.getTime()));
            }
        };
        dateTime.setOnClickListener(view -> new DatePickerDialog(UpdateHikeActivity.this, datePickerStart, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void findAllElements() {
        name = findViewById(R.id.hikeName);
        location = findViewById(R.id.hikeLocation);
        description = findViewById(R.id.description);
        dateTime = findViewById(R.id.dateTime);
        length = findViewById(R.id.hikeLength);
        terrain = findViewById(R.id.terrainType);
        weatherCondition = findViewById(R.id.hikeWeather);
        difficultyLevel = findViewById(R.id.hikeDifficulty);
        yes = findViewById(R.id.radioYes);
        no = findViewById(R.id.radioNo);
        btnSave = findViewById(R.id.hikeBtnUpdate);
    }

    private void setStatusColor() {
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    private void getAndDisplayInfo() {
        // Display the selected hike's information
        Intent intent = getIntent();
        selectedHike = (Hike) intent.getSerializableExtra("selectedHike");
        name.setText(selectedHike.getName());
        location.setText(selectedHike.getLocation());
        dateTime.setText(selectedHike.getDate());
        description.setText(selectedHike.getDescription());
        length.setText(String.valueOf(selectedHike.getLength()));
        terrain.setText(selectedHike.getTerrainType());
        weatherCondition.setText(selectedHike.getWeatherCondition());
        difficultyLevel.setText(selectedHike.getDifficulty());

        if (selectedHike.getParkingAvailable().equals("Yes")) {
            yes.setChecked(true);
        } else {
            no.setChecked(true);
        }
        Objects.requireNonNull(getSupportActionBar()).setTitle("Update \"" + selectedHike.getName() + "\"");


    }
}