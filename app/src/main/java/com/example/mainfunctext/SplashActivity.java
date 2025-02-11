package com.example.mainfunctext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.transition.Fade;
import androidx.transition.Transition;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        FrameLayout welcome = findViewById(R.id.welcome);

        MotionLayout motionLayout = findViewById(R.id.motionLayoutSplash);
        Button getStarted = findViewById(R.id.startScan);

        new Handler().postDelayed(() -> {

            new Handler().postDelayed(() -> {

                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                boolean isFirstLaunch = preferences.getBoolean("firstLaunch", true);
                Intent intent;
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                if (isFirstLaunch) {

                    getStarted.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent mainIntent = new Intent(SplashActivity.this, TermsAndCondition.class);
                            preferences.edit().putBoolean("firstLaunch", false).apply();
                            startActivity(mainIntent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        }
                    });

                    motionLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            motionLayout.transitionToEnd();
                        }
                    },2000);

                } else {

                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    welcome.setVisibility(View.INVISIBLE);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }

            }, fadeIn.getDuration());

        },fadeOut.getDuration());

    }
}
