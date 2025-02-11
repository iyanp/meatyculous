package com.example.mainfunctext;

import static org.opencv.android.NativeCameraView.TAG;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

//libraries for algorithm
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class ScanActivity extends AppCompatActivity {

    private ImageView imageView, next, again;
    private ProgressBar progressBar;
    private TextView loading;


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.INVISIBLE);
        next.setVisibility(View.VISIBLE);
        again.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        next = findViewById(R.id.next);
        again = findViewById(R.id.again);
        loading = findViewById(R.id.loadingText);

        String imageUriString = getIntent().getStringExtra("imageUri");

        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);

            //
            imageView = findViewById(R.id.displayPic);
            imageView.setImageURI(imageUri);
            progressBar = findViewById(R.id.progressBar);

            new AlertDialog.Builder(this)
                    .setTitle("Check Image")
                    .setMessage("Is this a photo of pork meat?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        setupAnalysis(imageUri, imageUriString);
                    })
                    .setNegativeButton("No", ((dialog, which) -> {
                        Toast.makeText(this, "Please capture of import pork meat photo only.", Toast.LENGTH_SHORT).show();
                        deleteCachedFile(imageUri.getPath());
                        finish();
                    })).setCancelable(false).show();

        } else {
            Toast.makeText(this, "No image data found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void setupAnalysis(Uri imageUri, String imageUriString) {
        next.setOnClickListener(v -> {
            // Show the progress bar and reset progress
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            next.setVisibility(View.INVISIBLE);
            again.setVisibility(View.INVISIBLE);

            // Use CountDownLatch to synchronize threads
            CountDownLatch latch = new CountDownLatch(1);

            // Animate the progress smoothly
            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
            progressAnimator.setDuration(3000); // 3 seconds (adjust as needed)
            progressAnimator.setInterpolator(new DecelerateInterpolator());

            // Notify when animation is finished
            progressAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    latch.countDown(); // Release the latch when the animation ends
                }
            });

            progressAnimator.start();
            loading.setVisibility(View.VISIBLE);

            Animation animationUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            loading.setAnimation(animationUp);

            // Start a background thread to perform the analysis
            new Thread(() -> {
                try {
                    // Wait for the animation to finish
                    latch.await(); // Wait until latch.countDown() is called
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Perform the analysis after the animation finishes
                Pair<String, Integer> result = analyzeMeatQuality2(imageUri.getPath(), this);
                String qualityCategory = result.first;
                double avgL = result.second;

                runOnUiThread(() -> {
                    // Hide the progress bar after completion
                    progressBar.setVisibility(View.INVISIBLE);

                    // Start the next activity with results

                    if(qualityCategory == "CROP AGAIN"){
                        LayoutInflater inflater = LayoutInflater.from(this);
                        View dialogView = inflater.inflate(R.layout.activity_reminders, null);

                        // Create and show the dialog using the custom layout
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setView(dialogView)
                                .setCancelable(true);

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        next.setVisibility(View.VISIBLE);
                        again.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                    }else if (qualityCategory.equals("tosmall")) {
                        Toast.makeText(this, "Image too Small, please try again.", Toast.LENGTH_SHORT).show();
                        next.setVisibility(View.VISIBLE);
                        again.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                    }else if (qualityCategory.equals("Nomeat")) {
                        LayoutInflater inflater = LayoutInflater.from(this);
                        View dialogView = inflater.inflate(R.layout.nomeatdetection, null);

                        // Create and show the dialog using the custom layout
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setView(dialogView)
                                .setCancelable(true);

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        next.setVisibility(View.VISIBLE);
                        again.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                    } else{
                    Intent intent = new Intent(ScanActivity.this, DisplayActivity.class);
                    intent.putExtra("imageUri", imageUriString);
                    intent.putExtra("finalresult", qualityCategory);
                    intent.putExtra("freshnessResult", "N/A");
                    intent.putExtra("lightness", avgL);
                    overridePendingTransition(R.anim.slide_in, 0);
                    startActivity(intent);
                    }
                });
            }).start();
        });

        again.setOnClickListener(v -> {
            // Clear the cache associated with the image
            deleteCachedFile(imageUri.getPath());
            finish(); // Close the activity
        });
    }

    private void deleteCachedFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    Log.d("CacheClear", "Cache file deleted successfully.");
                } else {
                    Log.d("CacheClear", "Failed to delete cache file.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CacheClear", "Error while clearing cache: " + e.getMessage());
        }
    }

    public static Pair<String, Integer> analyzeMeatQuality2(String imagePath, Context context) {
        // Load the Image
        final double THRESHOLD_A = 5.0;
        double avgL = 0.0;
        int avgLight = 0;

        Mat image;
        try {
            image = Imgcodecs.imread(imagePath);
            if (image.empty()) {
                return new Pair<>("Error: Invalid or empty image.", avgLight);
            }
        } catch (Exception e) {
            return new Pair<>("Error: Unable to process the image.", avgLight);
        }

        // Validate the image dimensions
        if (image.rows() < 100 || image.cols() < 100) {
            return new Pair<>("tosmall",avgLight);
        }

        try {
            Mat labImage = new Mat();
            Imgproc.cvtColor(image, labImage, Imgproc.COLOR_BGR2Lab);

            double totalL = 0, totalA = 0, totalB = 0; // remove totalB
            int totalPixels = labImage.rows() * labImage.cols();
            int totalRed = 0, totalNonRed = 0;


            int totalDarkPixels = 0, totalLightPixels = 0;
            int totalGreen = 0;

            /** for debugging purposes **/
            /**
            int totalBlue = 0;
            int totalYellow = 0;
             **/


            for (int row = 0; row < labImage.rows(); row++) {
                for (int col = 0; col < labImage.cols(); col++) {
                    double[] labValues = labImage.get(row, col);
                    double lValue = labValues[0] / 2.55; // convert sa 0-100 range
                    double aValue = labValues[1] - 128; // centralize
                    totalB += labValues[2] - 128;

                    totalL += lValue;
                    totalA += aValue;


                    if (lValue < 50) {
                        totalDarkPixels++;
                    } else {
                        totalLightPixels++;
                    }

                    if (aValue >= 5) {
                        totalRed++;
                    } else if (aValue >= 0 && aValue < 5){
                        totalNonRed++;
                    } else {
                        totalGreen++;
                    }

                }
            }

            avgL = totalL / totalPixels;
            double avgA = totalA / totalPixels;
            avgLight = (int)avgL;


            double percentageRed = (double) totalRed / totalPixels * 100;
            double percentageNonRed = (double) totalNonRed / totalPixels * 100;

            /** for debugging purposes **/
            /**
             double avgB = totalB / totalPixels; //variable pang debug
            double percentageDark = (double) totalDarkPixels / totalPixels * 100;
            double percentageLight = (double) totalLightPixels / totalPixels * 100;
            double percentageGreen = (double) totalGreen /totalPixels * 100;
            double percentageYellow = (double) totalYellow / totalPixels * 100;
            double percentageBlue = (double) totalBlue / totalPixels * 100;
            **/
            /** for debugging purposes **/

            //for testing only remove all logs in finishing app
            /**
            Log.d("MeatQualityAnalyzer", "Average L*: " + avgL);
            Log.d("MeatQualityAnalyzer", "Average a*: " + avgA);
            Log.d("MeatQualityAnalyzer", "Average b*: " + avgB);
            Log.d("PercentageAnalyzer", "Percentage of Darkness: " + percentageDark);
            Log.d("PercentageAnalyzer", "Percentage of Lightness: " + percentageLight);
            Log.d("PercentageAnalyzer", "Percentage of Redness: " + percentageRed);
            Log.d("PercentageAnalyzer", "Percentage of NonRed: " + percentageNonRed);
            Log.d("MeatQualityAnalyzer", "Total Green Pixels: " + percentageGreen);
            Log.d("MeatQualityAnalyzer", "Total Yellow Pixels: " + percentageYellow);
            Log.d("MeatQualityAnalyzer", "Total Blue Pixels: " + percentageBlue);
            Log.d("MeatQualityAnalyzer", "Average L* IN INT: " + avgLight);
             **/


            String qualityCategory = "";

            //try catch here:
            try {
                if (percentageRed > 50) {
                    if (percentageNonRed > 10) {
                        // set condition, kapag red at non red ay mataas parehas

                        qualityCategory = "CROP AGAIN";
                        return new Pair<>(qualityCategory, avgLight); // return value
                    }
                }
                if (avgA > THRESHOLD_A) {
                    if (avgLight > 50) {
                        qualityCategory = "PSE"; // Pale, Soft, and Exudative
                    } else if (avgLight > 40 && avgLight <= 50) {
                        qualityCategory = "RFN"; // Red, Firm, and Non-Exudative
                    } else if (avgLight > 24  && avgLight <= 40) {
                        qualityCategory = "DFD"; // Dark, Firm, and Dry
                    } else {
                        qualityCategory = "Noresult";
                    }
                    Log.d("ITO ANG RESULT SA SCAN ACTIVITY", "RESULT: " + qualityCategory);
                    return new Pair<>(qualityCategory, avgLight);
                }else if (avgA < THRESHOLD_A){
                    qualityCategory = "Nomeat"; //if mababa ang redness non meat ang lalabas
                    return new Pair<>(qualityCategory, avgLight);
                }
                else {
                    return new Pair<>("Error: The image does not match typical pork characteristics.", avgLight);
                }


            } catch (IllegalArgumentException e){
                return new Pair<>("Error: " +e.getMessage(), avgLight);
            }

        } catch (Exception e) {
            return new Pair<>("Error: Unable to complete analysis.", avgLight);
        }

    }

    public void viewReminders(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_quick_tips, null);
        builder.setView(dialogView);
        builder.show();
    }
}