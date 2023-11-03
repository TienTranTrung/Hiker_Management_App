package com.example.hikermanagementapp.Observation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hikermanagementapp.Database.Hike;
import com.example.hikermanagementapp.Database.MyDbHelper;
import com.example.hikermanagementapp.Database.Observation;
import com.example.hikermanagementapp.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class UpdateObservationActivity extends AppCompatActivity {
    // UI elements
    Observation selectedObservation;
    ImageView buttonCamera, buttonDelete, imagePreview;
    TextInputEditText observationName, observationComment;
    EditText observationDate;
    Button btnSave;
    TextView capturedImagePath;
    Calendar calendar;

    // private static final int PERMISSION_CODE = 1234;
    private static final int CAPTURE_CODE = 1001;

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_observation);

        Intent intent = getIntent();
        selectedObservation = (Observation) intent.getSerializableExtra("selectedObservation");
        // set status bar color
        setStatusColor();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update \"" + selectedObservation.getObservationName() + "\"");

        // find all elements
        findAllElements();
        // get selected trip info and display it
        getAndDisplayInfo();
        datePicker();
        // when click camera button
        whenClickCamera();
        // when click delete button
        whenClickDelete();
        // when click save button
        whenClickSave();
    }

    public void setStatusColor() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add observation for \"" + selectedObservation.getObservationName() + "\"");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        Window window = this.getWindow(); // set status bar color
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  // clear FLAG_TRANSLUCENT_STATUS flag
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    private void findAllElements() {
        observationDate = findViewById(R.id.observationDate);
        observationComment = findViewById(R.id.observationComment);
        observationName = findViewById(R.id.observationName);
        btnSave = findViewById(R.id.btnSave);
        buttonCamera = findViewById(R.id.button_camera);
        buttonDelete = findViewById(R.id.button_delete);
        imagePreview = findViewById(R.id.imagePreview);
        capturedImagePath = findViewById(R.id.capturedImagePath);
    }

    private void datePicker() {
        calendar = Calendar.getInstance(); // get current date
        // Date picker dialog for date field
        DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
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
                observationDate.setText(sdf.format(calendar.getTime()));
            }
        };
        observationDate.setOnClickListener(view -> new DatePickerDialog(UpdateObservationActivity.this, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void whenClickDelete() {
        buttonDelete.setOnClickListener(v -> {
            capturedImagePath.setText("");
            imagePreview.setImageResource(R.drawable.image_preview);
            // delete image from gallery
            if (imageUri != null) {
                getContentResolver().delete(imageUri, null, null);
            }
        });
    }

    private void whenClickCamera() {
        buttonCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(UpdateObservationActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UpdateObservationActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else if (!checkStoragePermission()) {
                requestStoragePermission();
            } else {
                openCamera();
            }
        });
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestManageStoragePermission();
        } else {
            ActivityCompat.requestPermissions(UpdateObservationActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }
    }

    private void requestManageStoragePermission() {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
            startActivityForResult(intent, REQUEST_STORAGE_PERMISSION);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, REQUEST_STORAGE_PERMISSION);
        }
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int write = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, CAPTURE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_CODE) {
            String path = getPathFromURI(imageUri);
            File file = new File(path);
            if (file.exists()) {
                Glide.with(this).load(path).into(imagePreview);
                capturedImagePath.setText(path);
            }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void whenClickSave() {
        btnSave.setOnClickListener(v -> checkCredentials());
    }

    private void checkCredentials() {
        String name_observation = observationName.getText().toString().trim();
        String date_observation = observationDate.getText().toString().trim();
        if (name_observation.isEmpty()) {
            observationName.setError("This is a required field");
            observationName.requestFocus();
        } else if (date_observation.isEmpty()) {
            showError(observationDate);
        } else {
            observationName.setError(null);
            observationDate.setError(null);
            updateObservation();
        }
    }

    private void updateObservation() {
        MyDbHelper myDB = new MyDbHelper(this);
        // get data from input
        selectedObservation.setObservationName(observationName.getText().toString().trim());
        selectedObservation.setTime(observationDate.getText().toString().trim());
        selectedObservation.setComments(Objects.requireNonNull(observationComment.getText()).toString().trim());
        selectedObservation.setObservationImage(capturedImagePath.getText().toString().trim());

        long result = myDB.updateObservation(selectedObservation);
        if (result == -1) {
            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Update Successfully!", Toast.LENGTH_SHORT).show();
            onBackPressed();
            finishActivity(1);
        }
    }

    private void showError(EditText input) {
        input.setError("This is a required field");
        input.requestFocus();
    }

    private void getAndDisplayInfo() {
        observationName.setText(selectedObservation.getObservationName());
        observationDate.setText(selectedObservation.getTime());
        observationComment.setText(selectedObservation.getComments());
        capturedImagePath.setText(selectedObservation.getObservationImage());
        if (selectedObservation.getObservationImage() != null) {
            imagePreview.setImageURI(Uri.parse(selectedObservation.getObservationImage()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}