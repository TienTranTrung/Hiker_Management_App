package com.example.hikermanagementapp.Observation;
import static android.os.Environment.getExternalStoragePublicDirectory;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hikermanagementapp.Database.Hike;
import com.example.hikermanagementapp.Database.MyDbHelper;
import com.example.hikermanagementapp.Database.Observation;
import com.example.hikermanagementapp.R;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
public class ObservationActivity extends AppCompatActivity {
    // UI elements
    TextView hikeName, location, date, parkingStatus, length;
    Hike selectedHike;
    ImageView empty_imageview, btnAdd;
    List<Observation> observations;
    ObservationAdapter observationAdapter;
    RecyclerView recyclerView;
    TextView no_data;

    MyDbHelper myDB; // database helper class
    static String filename;
    static String fileExtension = ".json";
    static String fullFileName = filename + fileExtension;
    ArrayList<String> savedList = new ArrayList<>(); // list of saved observation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);
        // set status bar color
        setStatusColor();
        // find all elements
        findAllElements();
        // Display data from database
        displayObservation();
        // recycler view for observation list
        recyclerViewHike();
        // when click add button
        whenClickAdd();
        // display hike details
        getDetails();
    }

    private void recyclerViewHike() {
        observationAdapter = new ObservationAdapter(ObservationActivity.this, this, observations); // adapter for recycler view
        recyclerView.setAdapter(observationAdapter); // set adapter to recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(ObservationActivity.this)); // set layout manager to recycler view
    }

    private void whenClickAdd() {
        btnAdd.setOnClickListener(view -> {
            Intent intents = new Intent(ObservationActivity.this, AddObservationActivity.class);
            intents.putExtra("selectedHike", selectedHike);
            startActivity(intents);
        });
    }

    private void setStatusColor() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Observation Management");
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
        recyclerView = findViewById(R.id.recyclerView_observation);
        empty_imageview = findViewById(R.id.empty_imageview);
        no_data = findViewById(R.id.no_data);
        hikeName= findViewById(R.id.hikeName);
        location = findViewById(R.id.hikeLocation);
        date = findViewById(R.id.dateTime);
        length = findViewById(R.id.hikeLength);
        parkingStatus = findViewById(R.id.hikeParking);
        btnAdd = findViewById(R.id.add_button_observation);
    }

    @SuppressLint("SetTextI18n")
    private void getDetails() {
        // get data from database
        hikeName.setText(selectedHike.getName());
        location.setText(selectedHike.getLocation());
        date.setText(selectedHike.getDate());
        length.setText(selectedHike.getLength());
        parkingStatus.setText(selectedHike.getParkingAvailable());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    void displayObservation() {
        Intent intent = getIntent();
        selectedHike = (Hike) intent.getSerializableExtra("selectedHike"); // get selected hike
        myDB = new MyDbHelper(this);
        observations = new ArrayList<>();
        observations= myDB.getAllObservations(selectedHike.getId()); // get all observation of selected hike
        if (observations.size() == 0) {
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        } else {
            empty_imageview.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_observation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            if (observations.size() == 0) {
                Toast.makeText(this, "No data to delete", Toast.LENGTH_SHORT).show();
            } else {
                confirmDialogDelete();
            }
        }
        if (item.getItemId() == R.id.export_data) {
            // check table observation is null
            if (observations.size() == 0) {
                Toast.makeText(this, "No data to export !", Toast.LENGTH_SHORT).show();
            } else {
                confirmDialogExport();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportData(int id) {
        savedList.clear(); // clear list before add new data
        observations = myDB.getAllObservations(id); // get all observation of selected hike
        Gson gson = new Gson(); // create gson object
        String json = gson.toJson(observations); // convert list to json
        savedList.add(json); // add json to list

        if (savedList(fullFileName, savedList)) {
            Toast.makeText(this, "Exported to " + "Download/" + fullFileName, Toast.LENGTH_LONG).show();
            startActivity(new Intent(ObservationActivity.this, DisplayDataActivity.class));
        } else {
            Toast.makeText(this, "Export is failed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean savedList(String fullFileName, ArrayList<String> savedList) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fullFileName)); // create file
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream); // create an output stream writer object
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter); // create a buffered writer object to write to the file
            bufferedWriter.write(String.valueOf(savedList)); // write the URL to the file
            bufferedWriter.newLine(); // write a new line
            bufferedWriter.flush(); // flush the buffer
            bufferedWriter.close(); // close the buffered writer
            outputStreamWriter.close(); // close the output stream writer
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Export is failed", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void confirmDialogDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All?");
        builder.setMessage("Are you sure you want to delete all observations?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            MyDbHelper myDB = new MyDbHelper(ObservationActivity.this);
            myDB.deleteAllObservation();
            // Refresh Activity
            Toast.makeText(ObservationActivity.this, "Delete All Observatoions Successfully! ", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(getIntent());
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {
        });
        builder.create().show();
    }

    private void confirmDialogExport() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Export All?");
        builder.setMessage("Do you want to export all observations?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            // timespan
            Date date = new Date();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
            // get hike name
            String hikeName = selectedHike.getName();
            fullFileName = hikeName + "_" + timeStamp + fileExtension; // create new file nameName));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(getIntent());
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            exportData(selectedHike.getId());
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {
        });
        builder.create().show();
    }
}