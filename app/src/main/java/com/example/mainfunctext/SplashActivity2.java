package com.example.mainfunctext;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
public class SplashActivity2  extends AppCompatActivity{

    private static final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);

        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        Animation fadeout = AnimationUtils.loadAnimation(this, R.anim.fade_in);


        ImageView logo = findViewById(R.id.logo);
        ImageView logo2 = findViewById(R.id.logo2);

        logo.startAnimation(zoomIn);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                logo2.setVisibility(ImageView.VISIBLE);
                logo2.startAnimation(fadeout);
            }
        }, 2000);


        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity2.this, SplashActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();  // Close this activity
            }
        }, 5000);

    }

}
