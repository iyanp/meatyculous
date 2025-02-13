package com.example.mainfunctext;

//import static kotlin.text.ScreenFloatValueRegEx.value;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.transition.ArcMotion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DisplayActivity extends AppCompatActivity {

    private ImageView imageView, saveIconPic, information, porkindicator1;
    private TextView colorTextView, moreDetails, moretips, qualityDescripHeading, textSave, qualityScore;
    private FrameLayout exit_displayUi, save, home, saveIcon, manual, saveOverlay, indicatorLayout, porkindicator;
    private Uri imageUri;

    //exit confirmation
    private void showExitConfirmationDialogDis() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close the app
                        finishAffinity();
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

    //for animating the back press
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DisplayActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
        finish();

    }

    //to get current time
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    //to reset buttons
    @Override
    protected void onResume() {
        super.onResume();
        saveIconPic.setImageResource(R.drawable.save_ic);
        textSave.setText("Save");
        save.setEnabled(true);
        saveIcon.setEnabled(true);
        saveOverlay.setVisibility(View.GONE);
        save.setEnabled(true);
        saveIconPic.clearColorFilter();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispaly);

        imageView = findViewById(R.id.imageView); //image scanned
        colorTextView = findViewById(R.id.colorTextView); //quality result text
        save = findViewById(R.id.save_result); //button for saving the results
        saveIcon = findViewById(R.id.save_result); //for ui purposes
        saveIconPic = findViewById(R.id.save_result_ic); //for ui purposes
        exit_displayUi = findViewById(R.id.exitDisplay); //button for exiting the app
        home = findViewById(R.id.returnHome); //button for returning home
        moreDetails = findViewById(R.id.moreDetails); //textview for more details
        moretips = findViewById(R.id.moretips); //another textView for more details
        qualityDescripHeading = findViewById(R.id.qualityTitle);
        textSave = findViewById(R.id.save);
        manual = findViewById(R.id.manual_ic);
        saveOverlay = findViewById(R.id.save_result_overlay);
        information = findViewById(R.id.info_icon);
        porkindicator = findViewById(R.id.porkindicator);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        qualityScore = findViewById(R.id.qualityScore);
        porkindicator1 = findViewById(R.id.porkindicator1);


        //to get the data from previous page
        Intent intent = getIntent();
        final String imageUriString = intent.getStringExtra("imageUri");
        final String finalresult = intent.getStringExtra("finalresult");
        double avgL = intent.getDoubleExtra("lightness", 0.0);

        indicatorLayout.post(() -> {
            int frameWidth = indicatorLayout.getWidth();
            int imageWidth = porkindicator.getWidth();

            double percentage = avgL / 100.0; //100
            int marginLeft = (int) ((1.0 - percentage) * (frameWidth - imageWidth)); //1.0

            int marginLeftNew;

            if (avgL > 50.0){
                marginLeftNew = marginLeft - 180;
            } else if (avgL >= 41.0 && avgL < 50.0){
                marginLeftNew = marginLeft - 0;
            } else if (avgL < 42){
                marginLeftNew = marginLeft + 180;
            } else {
                marginLeftNew = marginLeft;
            }

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) porkindicator.getLayoutParams();
            params.leftMargin = marginLeftNew;
            params.topMargin = (indicatorLayout.getHeight() - imageWidth) / 2; // Vertically center the ImageView
            porkindicator.setLayoutParams(params);

            if (avgL > 50.0){
                porkindicator1.setImageResource(R.drawable.porkindicator);
            } else if (avgL >= 41.0 && avgL < 50.0){
                porkindicator1.setImageResource(R.drawable.porkindicatorrfn);
            } else {
                porkindicator1.setImageResource(R.drawable.porkindicatordfd);
            }
        });


        //infotmation button
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(DisplayActivity.this, Information.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            }
        });

        //manual button
        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(DisplayActivity.this, Manual.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
            }
        });

        //exit button
        exit_displayUi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmationDialogDis();
            }

        });
        //exit hover
        exit_displayUi.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        v.setBackgroundColor(getResources().getColor(R.color.transparent_gray));
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        v.setBackgroundColor(getResources().getColor(R.color.transparent_gray));
                        break;
                }
                return true;
            }
        });

        //home button
        home.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent mainIntent = new Intent(DisplayActivity.this, MainActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_left,R.anim.slide_right);

            }

        });
        //home hover
        home.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        v.setBackgroundColor(getResources().getColor(R.color.transparent_gray));
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        v.setBackgroundColor(getResources().getColor(R.color.transparent_gray));
                        break;
                }
                return true;
            }
        });

        //if try again is meet
        colorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
            }
        });

        String description = "null";
        String contentfortips ="null";
        colorTextView.setText(finalresult);

        //pork quality
        if (colorTextView.getText().toString().equals("PSE")){
            porkindicator.setVisibility(View.VISIBLE);
            colorTextView.setText("PSE (Pale, pinkish gray, Soft and Exudative)");
            description =
                    "Appearance: The meat is pale in color.<br><br>" +
                    "Texture: Soft and Mushy.<br><br>" +
                    "Moisture: Loses a significant amount of water, making it exudative (watery).<br><br>" +
                    "Cause: Typically occurs due to stress before slaugther, leading to rapid pH decline in the meat while still warm.<br><br>" +
                    "Quality: Undesirable for consumers and processors as it is less flavorful, visually unappealing and has poor water-holding capacity, which appect the processing yield.<br><br>";
            contentfortips = "<b>Tip:</b> Consider using this pork meat in dishes where texture is less critical, or marinate it to improve its juiciness. Avoid using dry cooking methods.";

        }else if (colorTextView.getText().toString().equals("RFN")){
            porkindicator.setVisibility(View.VISIBLE);
            colorTextView.setText("RFN (Red, FIRM, non-exudative)");
            description =
                    "Appearance: Ideal reddish-pink color.<br><br>" +
                    "Texture: Firm and elastic to the touch.<br><br>" +
                    "Moisture: Retains water well, making it non-exudative.<br><br>" +
                    "Cause: Results from proper handling of animals before slaughter and optimal pH decline.<br><br>" +
                    "Quality: Most desirable meat quality, It is visually appealing, retains juices, and has a good texture, making it ideal for both direct consumption and processing.<br><br>";

            contentfortips = "<b>Tip:</b> This is high-quality pork meat, ideal for all types of cooking methods. Store it properly to maintain its excellent condition.";

        } else if (colorTextView.getText().toString().equals("DFD")) {
            porkindicator.setVisibility(View.VISIBLE);
            colorTextView.setText("DFD (Dark purplish red, Firm and Dry)");
            description =
                    "Appearance: Dark & purpulish-red<br><br>" +
                    "Texture: Firm and Sticky to the touch.<br><br>" +
                    "Moisture: Appears dry because it retains water within the muscle tissue.<br><br>" +
                    "Cause: Occurs due to long-term stress or exhaustion in animals before slaughter, leading to glycogen depletion and a higher ultimate pH in the meat.<br><br>" +
                    "Quality: Meat has a short shelf life and is prone to bacterial qrowth due to its higher pH. However, it's suitable for some specialized meat products.<br><br>";

            contentfortips = "<b>Tip:</b> Use this pork meat quality and store it properly to avoid spoilage.It is best suited for moist cooking methods to maintain its juiciness";

        } else{

            colorTextView.setText("Try again");
            saveIconPic.setColorFilter(Color.GRAY);
            description = "Possible errors: .<br><br>" + "Meat might be spoiled" + "Wrong angle.<br><br>" + "Meat no detected.<br><br>" + "Image too small";
            qualityDescripHeading.setVisibility(View.INVISIBLE);
            moretips.setVisibility(View.INVISIBLE);
            porkindicator.setVisibility(View.INVISIBLE);
            if(colorTextView.getText().toString().equals("Try again")){
                colorTextView.setEnabled(true);
            }
            qualityScore.setVisibility(View.GONE);

            contentfortips = "<br><br>If it is confirmed spoiled do not try to cook it, It is not consumable.";

        }
        moreDetails.setText(Html.fromHtml(description));
        moretips.setText(Html.fromHtml(contentfortips));

        //quality score
        if (finalresult.equals("Noresult") || colorTextView.equals("Try again")){
            qualityScore.setText("No Result found.");
        } else {

            if (avgL >= 43 && avgL <= 50) {
                qualityScore.setText("High Quality");
            } else if (avgL >= 30 && avgL < 43) {
                qualityScore.setText("Moderate Quality");
            } else if (avgL > 50) {
                qualityScore.setText("Low Quality");
            }

        }

        //for displaying the captured photo
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
            try {
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Call saveDataToDatabase when the Save button is clicked

        DatabaseHelper dbhelper = new DatabaseHelper(this);
        //save function here

        //save
        if(colorTextView.getText().toString().equals("Try again")){
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DisplayActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveIconPic.setImageResource(R.drawable.check);
                    textSave.setText("Saved");
                    save.setEnabled(false);
                    saveOverlay.setVisibility(View.VISIBLE);

                    try {
                        String currentDataTime = getCurrentDateTime();

                        if (imageUriString == null || finalresult == null) {
                            Toast.makeText(DisplayActivity.this, "Invalid data", Toast.LENGTH_SHORT).show();
                            return; // Exit early to avoid further processing
                        }

                        boolean isInserted = false;

                        // Check the detected color and insert data into the respective table
                        switch (finalresult.toUpperCase()) { // Convert to uppercase to avoid case sensitivity
                            case "PSE":
                                isInserted = dbhelper.insertPSEData(imageUriString, finalresult, currentDataTime);
                                break;

                            case "RFN":
                                isInserted = dbhelper.insertRFNData(imageUriString, finalresult, currentDataTime);
                                break;

                            case "DFD":
                                isInserted = dbhelper.insertDFDData(imageUriString, finalresult, currentDataTime);
                                break;

                            default:
                                save.setEnabled(false);
                                return;
                        }

                        if (isInserted) {
                            Toast.makeText(DisplayActivity.this, "Result Saved Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DisplayActivity.this, "Error saving result", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(DisplayActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                }

            });

            saveOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DisplayActivity.this, "Saved Already", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}