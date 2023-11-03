package com.example.hikermanagementapp.Observation;
import static android.os.Environment.getExternalStoragePublicDirectory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hikermanagementapp.Database.Observation;
import com.example.hikermanagementapp.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class DisplayDataActivity extends AppCompatActivity {

    TextView displayData, title;
    ArrayList<String> savedList = new ArrayList<>();
    static String fileF = ObservationActivity.fullFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);
        // set status bar color
        setStatusColor();
        displayData = findViewById(R.id.dataDisplay);
        title = findViewById(R.id.title);


        try {
            fileF = ObservationActivity.fullFileName;
            readFile();
        } catch (IOException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void readFile() throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileF);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream); // create an input stream reader object
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  // create a buffered reader object to read the file
            String lineData = bufferedReader.readLine(); // read the first line
            while (lineData != null) { // check if the line is not null
                savedList.add(lineData); // add the URL to the list
                lineData = bufferedReader.readLine(); // read the next line
                // display the data to text view
                displayData.setText(savedList.toString());
                title.setText(fileF);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
        }
    }

    public void setStatusColor() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        Window window = this.getWindow(); // set status bar color
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  // clear FLAG_TRANSLUCENT_STATUS flag
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.openFile) {
            openFile();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFile() {
        // Open the file using an external application
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()), "*/*");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_display, menu);
        return super.onCreateOptionsMenu(menu);
    }
}