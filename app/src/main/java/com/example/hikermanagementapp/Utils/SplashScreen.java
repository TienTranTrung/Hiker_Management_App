package com.example.hikermanagementapp.Utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hikermanagementapp.Authentication.LoginActivity;
import com.example.hikermanagementapp.Hike.AddHikeActivity;
import com.example.hikermanagementapp.Hike.HikeActivity;
import com.example.hikermanagementapp.R;

public class SplashScreen extends AppCompatActivity {
    // Declare Variables
    ImageView image;
    TextView app_name, slogan;
    Animation text_animation, image_animation;
    SharedPreferences onBoardingScreen;
    private static final int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        image = findViewById(R.id.imageLogo);
        slogan = findViewById(R.id.slogan);
        app_name= findViewById(R.id.app_name);

        text_animation = AnimationUtils.loadAnimation(this, R.anim.translate_anim);
        image_animation = AnimationUtils.loadAnimation(this, R.anim.top_animation);

        image.setAnimation(image_animation);
        app_name.setAnimation(text_animation);
        slogan.setAnimation(text_animation);

        //Calling New Activity after SPLASH_SCREEN seconds 3s = 3000
        new Handler().postDelayed(() -> {
            onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);

            // Check isFirstTime to show OnBoarding or not
            boolean isFirstTime = onBoardingScreen.getBoolean("firstTime",true);
            if (isFirstTime){
                SharedPreferences.Editor editor = onBoardingScreen.edit();
                editor.putBoolean("firstTime",false);
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME);

    }
}