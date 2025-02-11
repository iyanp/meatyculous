package com.example.mainfunctext;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DisplayTourMode extends AppCompatActivity {

    private TextView context;
    private FrameLayout[] frames;
    private int currentFrames = 0;
    private FrameLayout next, prev, exit, exitTourmode;
    private Animation popUp,fadeIn, fadeOut;



    //for extra case
    private static final int extra_index = 4;

    private ImageView imageView, saveIcon;
    private TextView colorTextView, moreDetails, moretips, qualityDescripHeading, textSave;
    //focus sa frame layouts

    private void skiptutorialmodeconfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Skip this Tutorial")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close the app
                        Intent mainIntent = new Intent(DisplayTourMode.this, MainActivity.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        Toast.makeText(DisplayTourMode.this, "Tutorial Mode on Manual section.", Toast.LENGTH_SHORT).show();

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

    //added functionality
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left,R.anim.slide_right);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_tour_mode);

        imageView = findViewById(R.id.imageView);
        colorTextView = findViewById(R.id.colorTextView);
        moreDetails = findViewById(R.id.moreDetails);
        moretips = findViewById(R.id.moretips);
        qualityDescripHeading = findViewById(R.id.qualityTitle);
        textSave = findViewById(R.id.save);
        saveIcon = findViewById(R.id.save_result_ic);
        exit = findViewById(R.id.exitTur);
        exitTourmode = findViewById(R.id.exitTutorDisplay);

        popUp = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);


        frames = new FrameLayout[] {
                findViewById(R.id.returnHome1),
                findViewById(R.id.manual_ic1),
                findViewById(R.id.save_result1),
                findViewById(R.id.exitDisplay1),
                findViewById(R.id.tryagain)
        };

        frames[currentFrames].setVisibility(View.VISIBLE);
        context = findViewById(R.id.tutorCam);
        next = findViewById(R.id.nextCam);
        prev = findViewById(R.id.previousCam);

        next.setOnClickListener(v -> nextFrame());
        prev.setOnClickListener(v -> previousFrame());

        for (int i = 1; i < frames.length - 1; i++) {
            frames[i].setVisibility(View.INVISIBLE);
        }


        //exit tur
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent mainIntent = new Intent(DisplayTourMode.this, MainActivity.class);
            startActivity(mainIntent);
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            Toast.makeText(DisplayTourMode.this, "Tutorial Mode on Manual section.", Toast.LENGTH_SHORT).show();

            }
        });

        exitTourmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skiptutorialmodeconfirmation();
            }
        });


    }

    private void nextFrame() {
        if (currentFrames < frames.length - 1) {


            frames[currentFrames].startAnimation(fadeOut);
            frames[currentFrames].setVisibility(View.INVISIBLE);

            currentFrames++;

            frames[currentFrames].setVisibility(View.VISIBLE);
            frames[currentFrames].startAnimation(fadeIn);
            updateContextText();

        }
        else {
        }
    }

    private void previousFrame() {
        if (currentFrames > 0) {


            frames[currentFrames].startAnimation(fadeOut);
            frames[currentFrames].setVisibility(View.INVISIBLE);

            currentFrames--;

            frames[currentFrames].setVisibility(View.VISIBLE);
            frames[currentFrames].startAnimation(fadeIn);
            updateContextText();
        }

        else {

        }
    }

    private void updateContextText() {


        switch (currentFrames + 1) { // +1 to match 1-based indexing of cases
            case 1:
                context.setText("This button allows you to return to Homepage");
                prev.setVisibility(View.INVISIBLE); // Hide 'prev' button on first frame


                break;
            case 2:
                context.setText("Click this button to view App manual.");
                prev.setVisibility(View.VISIBLE); // Show 'prev' button from this frame onward


                break;
            case 3:
                context.setText("This button allows you to save result");
                prev.setVisibility(View.VISIBLE);

                break;
            case 4:
                context.setText("This button will close the application");
                prev.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                exit.setVisibility(View.INVISIBLE);

                break;


            case 5:
                context.setText("If you see this text click it to try again.");
                prev.setVisibility(View.VISIBLE);
                next.setVisibility(View.INVISIBLE); // Hide 'next' button on the last frame
                exit.setVisibility(View.VISIBLE);

                break;

            default:
                context.setText("This is the History screen.");
                prev.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
        }

    }
}