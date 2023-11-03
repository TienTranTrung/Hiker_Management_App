package com.example.hikermanagementapp.Authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hikermanagementapp.Database.MyDbHelper;
import com.example.hikermanagementapp.Hike.HikeActivity;
import com.example.hikermanagementapp.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    // UI elements
    public static String currentUser;
    EditText username, password;
    Button btnLogin;
    MyDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // find all elements
        findAllElements();
        // Set status bar color
        setStatusColor();
        btnLogin.setOnClickListener(v -> checkInput());
    }

    private void findAllElements() {
        username = findViewById(R.id.txtUsername);
        password = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void setStatusColor() {
        // set status bar color
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    private void checkInput() {
        db = new MyDbHelper(this); // create database
        String Username = username.getText().toString().trim(); // get username from input field and trim it
        String Password = password.getText().toString().trim(); // get password from input field and trim it
        if (Username.isEmpty()) {
            showError(username); // if username is empty, show error
        } else if (Password.isEmpty()) {
            showError(password); // if password is empty, show error
        }
        Boolean checkData = db.checkUserPass(Username, Password); // check if username and password are correct
        if (checkData) {
            Toast.makeText(LoginActivity.this, "" +
                    "" +
                    "" +
                    "Log in is successfully !", Toast.LENGTH_SHORT).show(); // if username and password are correct, show message
            currentUser = Username; // set current user to display in HikeActivity
            Intent intent = new Intent(getApplicationContext(), HikeActivity.class); // create intent to go to HikeActivity
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Username or Password is incorrect !", Toast.LENGTH_SHORT).show();
        }
    }

    private void showError(EditText input) {
        input.setError("This field cannot be empty");
        input.requestFocus();
    }
}