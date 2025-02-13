package com.example.mainfunctext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainTourMode extends AppCompatActivity {

    private TextView context;
    private FrameLayout[] frames;
    private int currentFrame = 0;
    private FrameLayout next, prev,qualityDescriptions,cameraEnable, contentFrame, exitTour;
    private static final int delay = 500;
    private Animation popUp,fadeIn, fadeOut;


    //skip confirmation

    private void skiptutorialmodeconfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to skip this tutorial")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close the app
                        Intent mainIntent = new Intent(MainTourMode.this, MainActivity.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        Toast.makeText(MainTourMode.this, "Tutorial Mode on Manual section.", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tour_mode);

        frames = new FrameLayout[] {
                findViewById(R.id.history_icon1),
                findViewById(R.id.gallery_icon1),
                findViewById(R.id.camera_icon1),
                findViewById(R.id.manual_icon1),
                findViewById(R.id.exitHomeUi1),
                findViewById(R.id.pqe),
                findViewById(R.id.camera_icon1)
        };

        exitTour = findViewById(R.id.exitTutor);
        contentFrame = findViewById(R.id.centerFrame);
        cameraEnable = findViewById(R.id.camera_icon1);
        qualityDescriptions = findViewById(R.id.pqe);
        context = findViewById(R.id.tutor);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.previous);
        frames[currentFrame].setVisibility(View.VISIBLE);

        //simple animation for contentFrame and Exit Tutorial text
        popUp = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                contentFrame.setVisibility(View.VISIBLE);
                contentFrame.startAnimation(popUp);
            }
        }, delay);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                exitTour.setVisibility(View.VISIBLE);
                exitTour.startAnimation(fadeIn);
            }
        }, delay);
        //end of simple animation for contentFrame and Exit tutorial text


        //buttons/frameviewButtons
        //for exiting the tour mode
        exitTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skiptutorialmodeconfirmation();
            }
        });
        //for exiting the tour mode end

        //next and previous button
        next.setOnClickListener(v -> nextFrame());
        prev.setOnClickListener(v -> previousFrame());
        //end of next and previous button

        //camera/proceed to next activity
        cameraEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mainIntent = new Intent(MainTourMode.this, CameraTutorMode.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);

            }
        });
        //end of camera

        //end of buttons


        for (int i = 1; i < frames.length - 1; i++) {
            frames[i].setVisibility(View.INVISIBLE);
        }

        cameraEnable.setEnabled(false);
        qualityDescriptions.setVisibility(View.VISIBLE);

    }



    private void nextFrame() {
        if (currentFrame < frames.length - 1) {
            frames[currentFrame].startAnimation(fadeOut);
            frames[currentFrame].setVisibility(View.INVISIBLE);

            currentFrame++;

            frames[currentFrame].setVisibility(View.VISIBLE);
            frames[currentFrame].startAnimation(fadeIn);
            updateContextText();
        }
    }

    private void previousFrame() {
        if (currentFrame > 0) {
            frames[currentFrame].startAnimation(fadeOut);
            frames[currentFrame].setVisibility(View.INVISIBLE);

            currentFrame--;

            frames[currentFrame].setVisibility(View.VISIBLE);
            frames[currentFrame].startAnimation(fadeIn);
            updateContextText();
        }
    }

    private void updateContextText() {
        switch (currentFrame + 1) {

            case 1:
                context.setText("This button will show History Result.");
                prev.setVisibility(View.INVISIBLE);
                qualityDescriptions.setVisibility(View.VISIBLE);
                qualityDescriptions.setElevation(0f);
                break;
            case 2:
                context.setText("This button allows you to access gallery.");
                prev.setVisibility(View.VISIBLE);
                qualityDescriptions.setVisibility(View.VISIBLE);
                qualityDescriptions.setElevation(0f);
                break;
            case 3:
                context.setText("This button allows you to access camera to start scanning.");
                prev.setVisibility(View.VISIBLE);
                qualityDescriptions.setVisibility(View.VISIBLE);
                qualityDescriptions.setElevation(0f);
                break;
            case 4:
                context.setText("This button will view the Users Manual.");
                prev.setVisibility(View.VISIBLE);
                qualityDescriptions.setVisibility(View.VISIBLE);
                qualityDescriptions.setElevation(0f);
                break;
            case 5:
                context.setText("This button allows you to close the Application.");
                prev.setVisibility(View.VISIBLE);
                qualityDescriptions.setElevation(0f);
                qualityDescriptions.setVisibility(View.VISIBLE);
                break;

            case 6:
                context.setText("This set of buttons allows you to show three different quality descriptions.");
                prev.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);

                qualityDescriptions.setElevation(8f);
                break;


            case 7:
                context.setText("Tap to open camera (Proceed to Next page)");
                prev.setVisibility(View.VISIBLE);
                qualityDescriptions.setVisibility(View.VISIBLE);
                qualityDescriptions.setElevation(0f);
                cameraEnable.setEnabled(true);
                next.setVisibility(View.INVISIBLE);
                break;


            default:
                context.setText("This is the History screen.");
                qualityDescriptions.setVisibility(View.VISIBLE);
        }
    }

}