package com.example.mainfunctext;

import static android.content.ContentValues.TAG;
import static org.opencv.android.Utils.matToBitmap;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import org.jetbrains.annotations.Nullable;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


public class MainActivity extends AppCompatActivity {

    //buttons imageView
    private ImageView cameraIcon;
    private ImageView infoIcon;
    private ImageView imageView;

    private static final int CAMERA_REQUEST_CODE = 100;

    //frame layouts
    private FrameLayout galleryIcon;
    private FrameLayout manualIcon;
    private FrameLayout exitHome;
    private FrameLayout historyIcon;
    private FrameLayout history;

    ActivityResultLauncher<String> mGetContent;
    private DatabaseHelper databaseHelper;


    //exit confirmation
    private void showExitConfirmationDialog() {
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

    //exit when back press
    @Override
    public void onBackPressed() {

        // Show the confirmation dialog
        super.onBackPressed();
        finishAffinity();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(this);

        //check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }

        //to intregate OpenCV
        initializeOpenCV(this);

        //variables
        exitHome = findViewById(R.id.exitHomeUi);
        cameraIcon = findViewById(R.id.camera_icon);
        galleryIcon = findViewById(R.id.gallery_icon);
        manualIcon = findViewById(R.id.manual_icon);
        infoIcon = findViewById(R.id.info_icon);
        historyIcon = findViewById(R.id.history_icon);
        imageView = findViewById(R.id.pork_parts);
        history = findViewById(R.id.history_icon);



        //for viewing the pork parts image//button history
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the dialog with the zoomable image
                ZoomableImageDialog dialog = new ZoomableImageDialog(MainActivity.this, R.drawable.pork_parts);
                dialog.show();
            }
        });

        //history hover
        historyIcon.setOnHoverListener(new View.OnHoverListener() {
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


        //button manual
        manualIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, Manual.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }

        });

        //manual hover
        manualIcon.setOnHoverListener(new View.OnHoverListener() {
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

        //button history
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, HistoryUI.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });

        //button information
        infoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, Information.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);

            }
        });

        //button exit
        exitHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmationDialog();
            }

        });

        //exit hover
        exitHome.setOnHoverListener(new View.OnHoverListener() {
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

        //button icon
        cameraIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent mainIntent = new Intent(MainActivity.this, CameraUI.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);

            }

        });

        //button to start function (cropping image)
        galleryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        //hover effect
        galleryIcon.setOnHoverListener(new View.OnHoverListener() {
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

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) { // Check if result is not null
                    Intent intent = new Intent(MainActivity.this, CropperActivity.class);
                    intent.putExtra("DATA", result.toString());

                    startActivityForResult(intent, 101);
                }
            }
        });
    }

    //pop up functionality
    public void popUpPse(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pse, null);
        builder.setView(dialogView);
        builder.show();
    }

    public void popUpRfn(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rfn, null);
        builder.setView(dialogView);
        builder.show();
    }

    public void popUpDfd(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_dfd, null);
        builder.setView(dialogView);
        builder.show();
    }


    //function para sa openCV initialization
    private void initializeOpenCV(Context context) {
        if (!OpenCVLoader.initDebug()) {
            showToast(context, "OpenCV initialization failed");
        } else {

        }
    }


    private void showToast(Context context, String message) {
        //pag check connection
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }



    //request permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast(this, "Camera permission granted");
            } else {
                showToast(this, "Camera permission denied");
            }
        }
    }


    //nandito algorithm at color segmentation
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 101) {

            String result = data.getStringExtra("RESULT");
            Uri resultUri = null;

            if (result != null) {
                resultUri = Uri.parse(result);

                Mat image = Imgcodecs.imread(resultUri.getPath());
                if (image.empty()) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    return;
                }

                //passing to ScanActivity
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                intent.putExtra("imageUri", resultUri.toString());
                overridePendingTransition(R.anim.slide_in, 0);
                startActivity(intent);

            }

        }
    }
}
