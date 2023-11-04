package com.example.hikermanagementapp.Hike;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.hikermanagementapp.Authentication.LoginActivity;
import com.example.hikermanagementapp.Database.Hike;
import com.example.hikermanagementapp.Database.MyDbHelper;
import com.example.hikermanagementapp.R;
import com.example.hikermanagementapp.Utils.MapActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddHikeActivity extends AppCompatActivity {
    // UI elements
    private static final int PLACE_PICKER_REQUEST = 1;
    private String apiKey;
    TextInputEditText name, location, description, length, terrain, difficultyLevel, weatherCondition;
    EditText dateTime;
    Button btnAdd;
    Calendar calendar;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    String parkingStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hike);
        // set status bar color
        setStatusColor();
        // find all elements
        findAllElements();
        // set date picker
        dateTimePicker();
        // when click add button
        whenClickAdd();
        ImageView buttonLocation = findViewById(R.id.button_location);
        try {
            ApplicationInfo app = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            apiKey = bundle.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        // Initialize the SDK
        Places.initialize(getApplicationContext(), apiKey);
        buttonLocation.setOnClickListener(v -> {
            // Start the MapActivity
            Intent mapIntent = new Intent(AddHikeActivity.this, MapActivity.class);
            mapIntent.putExtra("activityClass", AddHikeActivity.class);
//            mapIntent.putExtra("selectedHike", selectedHike);
            startActivityForResult(mapIntent, 100);
        });


//        buttonLocation.setOnClickListener(v -> {
//            // Start the Place Picker activity
//            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
//            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(AddHikeActivity.this);
//            startActivityForResult(intent, PLACE_PICKER_REQUEST);
//        });
    }

    private void whenClickAdd() {
        //setOnClickListener to show date picker
        btnAdd.setOnClickListener(v -> checkCredentials());
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
        dateTime.setOnClickListener(view -> new DatePickerDialog(AddHikeActivity.this, datePickerStart, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
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

    private void findAllElements() {
        name = findViewById(R.id.hikeName);
        location = findViewById(R.id.hikeLocation);
        description = findViewById(R.id.description);
        dateTime = findViewById(R.id.dateTime);
        length = findViewById(R.id.hikeLength);
        terrain = findViewById(R.id.terrainType);
        weatherCondition = findViewById(R.id.hikeWeather);
        difficultyLevel = findViewById(R.id.hikeDifficulty);
        btnAdd = findViewById(R.id.hikeBtnAdd);
    }

    private void checkCredentials() {
        String hikeName = Objects.requireNonNull(name.getText()).toString().trim();
        String hikeLocation = Objects.requireNonNull(location.getText()).toString().trim();
        String dateT = dateTime.getText().toString().trim();
        String weather_condition = Objects.requireNonNull(weatherCondition.getText()).toString().trim();
        String terrain_type = Objects.requireNonNull(terrain.getText()).toString().trim();
        String hikeLength = Objects.requireNonNull(length.getText()).toString().trim();
        String difficulty_level = Objects.requireNonNull(difficultyLevel.getText()).toString().trim();
        radioGroup = findViewById(R.id.radioGroup);
        selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        parkingStatus = selectedRadioButton.getText().toString();

        if (hikeName.isEmpty()) {
            showError(name, "Hike name cannot be empty");
        } else if (hikeLocation.isEmpty()) {
            showError(location, "Hike location cannot be empty");
        } else if (dateT.isEmpty()) {
            showErrorDate(dateTime);
        } else if (weather_condition.isEmpty()) {
            showError(weatherCondition, "Weather condition cannot be empty");
        } else if (terrain_type.isEmpty()) {
            showError(terrain, "Terrain type cannot be empty");
        } else if (hikeLength.isEmpty()) {
            showError(length, "Hike length cannot be empty");
        } else if (difficulty_level.isEmpty()) {
            showError(difficultyLevel, "Difficulty level cannot be empty");
        } else {
            confirmDataHike();
        }
    }

    public void confirmDataHike() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String hikeName = Objects.requireNonNull(name.getText()).toString().trim();
        String hikeLocation = Objects.requireNonNull(location.getText()).toString().trim();
        String dateT = dateTime.getText().toString().trim();
        String hikeDescription = Objects.requireNonNull(description.getText()).toString().trim();
        String weather_condition = Objects.requireNonNull(weatherCondition.getText()).toString().trim();
        String terrain_type = Objects.requireNonNull(terrain.getText()).toString().trim();
        String hikeLength = Objects.requireNonNull(length.getText()).toString().trim();
        String difficulty_level = Objects.requireNonNull(difficultyLevel.getText()).toString().trim();

        builder.setTitle("Do you want to add this hike?");
        builder.setMessage("Hike Name: " + hikeName + "\nHike Location: " + hikeLocation + "\nDate Time : " + dateT + "\nDescription: " + hikeDescription + "\nWeather Condition: " + weather_condition + "\nTerrain Type: " + terrain_type + "\nHike Length: " + hikeLength + "\nDifficulty Level: " + difficulty_level + "\nParking Status: " + parkingStatus);
        builder.setPositiveButton("Add", (dialogInterface, i) -> addHike());
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
        });
        builder.create().show();
    }

    private void addHike() {
        String hikeName = Objects.requireNonNull(name.getText()).toString().trim();
        String hikeLocation = Objects.requireNonNull(location.getText()).toString().trim();
        String dateT = dateTime.getText().toString().trim();
        String weather_condition = Objects.requireNonNull(weatherCondition.getText()).toString().trim();
        String terrain_type = Objects.requireNonNull(terrain.getText()).toString().trim();
        String hikeLength = Objects.requireNonNull(length.getText()).toString().trim();
        String difficulty_level = Objects.requireNonNull(difficultyLevel.getText()).toString().trim();
        String hikeDescription = Objects.requireNonNull(description.getText()).toString().trim();
        String parkingStatus = selectedRadioButton.getText().toString();

        Hike hike = new Hike();
        hike.setName(hikeName);
        hike.setLocation(hikeLocation);
        hike.setDate(dateT);
        hike.setWeatherCondition(weather_condition);
        hike.setTerrainType(terrain_type);
        hike.setLength(hikeLength);
        hike.setDifficulty(difficulty_level);
        hike.setDescription(hikeDescription);
        hike.setParkingAvailable(parkingStatus);
        hike.setHikeUser(LoginActivity.currentUser);
        MyDbHelper dbHelper = new MyDbHelper(this);
        long result = dbHelper.addHike(hike);

        if (result != -1) {
            Toast.makeText(this, "Hike added successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // set the result code
            finish(); // close this activity
        } else {
            Toast.makeText(this, "Failed to add hike", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                String selectedPlaceName = data.getStringExtra("selectedPlaceName");
                location.setText(selectedPlaceName);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle the error.
                Log.i(TAG, "Error to get user location");
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "User Cancel");
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                location.setText(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "User Cancel");
            }
        }
    }

    private void showError(TextInputEditText field, String text) {
        field.setError(text);
        field.requestFocus();
    }

    private void showErrorDate(EditText input) {
        input.setError("This is a required field");
        input.requestFocus();
    }
}