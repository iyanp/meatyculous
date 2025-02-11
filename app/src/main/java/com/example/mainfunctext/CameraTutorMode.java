package com.example.mainfunctext;

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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CameraTutorMode extends AppCompatActivity {

    private TextView context;
    private ImageView[] images;
    private int currentImage = 0;
    private FrameLayout next, prev, skip;
    private ImageView gallery, flipcam,pointer,info,flash,exit,scan;
    private SeekBar zoomFunction;
    private FrameLayout frame, exitTourmode;


    //skip tutorial
    private void skiptutorialmodeconfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Skip this Tutorial")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close the app
                        Intent mainIntent = new Intent(CameraTutorMode.this, MainActivity.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        Toast.makeText(CameraTutorMode.this, "Tutorial Mode on Manual section.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Dismiss the dialog3
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scan.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_tutor_mode);

        gallery = findViewById(R.id.galleryTutor);
        flipcam = findViewById(R.id.flipCameraTutor);
        pointer = findViewById(R.id.pointerTutor);
        info = findViewById(R.id.informationTutor);
        flash = findViewById(R.id.flashToggleTutor);
        exit = findViewById(R.id.exitCamUiTutor);
        scan = findViewById(R.id.scanButtonTutor);
        frame = findViewById(R.id.framelayout);
        exitTourmode = findViewById(R.id.exitTutorCam);
        skip = findViewById(R.id.exitTutorCam);
        zoomFunction = findViewById(R.id.zoomSeekBarTutor);

        images = new ImageView[] {gallery, flipcam,pointer,info,flash,exit,scan};


        context = findViewById(R.id.tutorCam);
        next = findViewById(R.id.nextCam);
        prev = findViewById(R.id.previousCam);



        next.setOnClickListener(v -> nextImage());
        prev.setOnClickListener(v -> previousImage());

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(CameraTutorMode.this, DisplayTourMode.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });

        exitTourmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skiptutorialmodeconfirmation();
            }
        });

    }


    private void nextImage() {
        if (currentImage < images.length - 1) {
            images[currentImage].setElevation(1f);
            currentImage++;
            images[currentImage].setElevation(3f);
            updateContextText();
        }
    }

    private void previousImage() {
        if (currentImage > 0) {
            images[currentImage].setElevation(1f);
            currentImage--;
            images[currentImage].setElevation(3f);
            updateContextText();
        }

    }

    private void updateContextText() {
        // Log the current image
        //Log.d("CameraTutor", "Current Image Index: " + currentImage);

        // Reset the elevation of all images
        for (ImageView img : images) {
            img.setElevation(0f);
            zoomFunction.setElevation(0f);
            next.setVisibility(View.VISIBLE);
            frame.setElevation(1f);// Reset all to 0f to ensure only the active one has elevation

        }

        // Elevate the current image and set the context text
        switch (currentImage + 1) {
            case 1:
                context.setText("This button allows you to access gallery.");
                prev.setVisibility(View.INVISIBLE);
                gallery.setElevation(3f);
                zoomFunction.setElevation(0f);
                break;
            case 2:
                context.setText("This button allows you to flip camera.");
                prev.setVisibility(View.VISIBLE);
                flipcam.setElevation(3f);
                zoomFunction.setElevation(0f);
                break;
            case 3:
                context.setText("This button is to view the Camera Information.");
                prev.setVisibility(View.VISIBLE);
                info.setElevation(3f);
                zoomFunction.setElevation(0f);
                break;
            case 4:
                context.setText("This button allows you to turn on/off the flash.");
                prev.setVisibility(View.VISIBLE);
                flash.setElevation(3f);
                zoomFunction.setElevation(0f);
                skip.setVisibility(View.VISIBLE);
                break;
            case 5:
                context.setText("Tap this button to return to Main Page.");
                prev.setVisibility(View.VISIBLE);
                exit.setElevation(3f);
                zoomFunction.setElevation(0f);
                skip.setVisibility(View.GONE);
                break;
            case 6:
                context.setText("This pointer will guide you for a perfect shot. And the tool below is for zooming out the Camera");
                prev.setVisibility(View.VISIBLE);
                pointer.setElevation(3f);
                zoomFunction.setElevation(3f);
                skip.setVisibility(View.VISIBLE);
                //Log.d("CameraTutor", "Elevating pointer to 3f");
                break;
            case 7:
                context.setText("Tap to capture (Proceed to Next page)");
                prev.setVisibility(View.VISIBLE);
                scan.setElevation(3f);
                scan.setEnabled(true);
                zoomFunction.setElevation(0f);
                next.setVisibility(View.INVISIBLE);
                //Log.d("CameraTutor", "Elevating scan to 3f");
                break;
            default:
                context.setText("This is the History screen.");
                //Log.d("CameraTutor", "No elevation applied");
        }

    }
}