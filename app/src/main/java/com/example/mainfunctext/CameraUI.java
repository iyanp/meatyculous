package com.example.mainfunctext;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.camera2.CameraManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.camera.core.CameraControl;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.common.util.concurrent.ListenableFuture;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraUI extends AppCompatActivity {

    private PreviewView cameraview; //for main camera ui
    private ImageCapture imageCapture; //for taking the picture/photo
    private CameraControl cameraControl; // for overall control in camera like flash, focus etc
    private CameraSelector cameraSelector; //for main camera ui
    private ImageView scanButton; //button for scanning
    private ProgressBar progressBar; //loading
    private ImageView gallery; //button for gallery
    private ImageView flashToggle; //button for flash
    private CameraInfo cameraInfo; //for flipping the camera
    private ImageView flipCamera; //button for flipping the camera
    private ImageView exit_cameraUi; //button for exit button
    private boolean isFlashOn = false; //for turning on camera flash
    private boolean isUsingFrontCamera = false; // for flipping the camera
    ActivityResultLauncher<String> mGetContent; // cropping photo function
    private SeekBar zoomSeekBar; //for zoom in and zoom out
    private File currentPhotoFile; //hanapin ang current photofile

    //device back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_ui);


        cameraview = findViewById(R.id.cameraview);
        scanButton = findViewById(R.id.scanButton);
        progressBar = findViewById(R.id.progressBar);
        gallery = findViewById(R.id.gallery);
        flashToggle = findViewById(R.id.flashToggle);
        flipCamera = findViewById(R.id.flipCamera);
        exit_cameraUi = findViewById(R.id.exitCamUi);
        zoomSeekBar = findViewById(R.id.zoomSeekBar);

        //calling the function start camera
        startCamera();

        //for flipping the camera
        flipCamera.setOnClickListener(v -> {
            isUsingFrontCamera = !isUsingFrontCamera; // Toggle camera state
            startCamera(); // Restart camera with the new camera selector
        });

        //to call the scan function
        scanButton.setOnClickListener(view -> {
                    scanButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    scan();

                });
        //exit button
        exit_cameraUi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(CameraUI.this, MainActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
            }
        });

        //flash function
        isFlashOn = false;
        updateFlashToggleIcon();

        //flash function
        flashToggle.setOnClickListener(v -> {
            if (cameraControl != null) {
                isFlashOn = !isFlashOn;
                cameraControl.enableTorch(isFlashOn);
                updateFlashToggleIcon();
            }else{
            }
        });

        //for zoom in and out functionality
        zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && cameraControl != null) {
                    float linearZoom = progress / 100f; // Map SeekBar progress to 0.0 - 1.0
                    cameraControl.setLinearZoom(linearZoom);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Add custom behavior
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Add custom behavior
            }
        });

        //generate the device camera to use in background
        cameraview.setOnTouchListener((v, event) -> {
            if (cameraControl == null) {
                return false;
            }

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    // Single touch for focusing
                    if (event.getPointerCount() == 1) {
                        MeteringPointFactory factory = cameraview.getMeteringPointFactory();
                        MeteringPoint point = factory.createPoint(event.getX(), event.getY());
                        FocusMeteringAction action = new FocusMeteringAction.Builder(point).build();
                        cameraControl.startFocusAndMetering(action);
                        return true;
                    }
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_MOVE:
                    // Multi-touch for pinch-to-zoom
                    if (event.getPointerCount() == 2) {
                        float distance = getPinchDistance(event);
                        float maxZoom = cameraInfo.getZoomState().getValue().getMaxZoomRatio();
                        float minZoom = cameraInfo.getZoomState().getValue().getMinZoomRatio();
                        float zoomRatio = Math.min(maxZoom, Math.max(minZoom, distance / 3000f)); // Adjust scaling as needed
                        cameraControl.setZoomRatio(zoomRatio);
                        return true;
                    }
                    break;
            }
            return false;
        });

        //gallery function
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        //for integrating crop
        mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) { // Check if result is not null
                    Intent intent = new Intent(CameraUI.this, CropperActivity.class);
                    intent.putExtra("DATA", result.toString());
                    startActivityForResult(intent, 101);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupTemporaryFiles();
    }

    private void cleanupTemporaryFiles() {
        File cacheDir = getCacheDir();
        if (cacheDir.isDirectory()) {
            for (File file : cacheDir.listFiles()) {
                file.delete();
            }
        }
    }

    public void tipsOnCapture(View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_snap_tips, null);

        // Create the popup window
        PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setAnimationStyle(R.anim.fade_in);

        // Initialize ViewPager and TabLayout in the popup
        ViewPager2 viewPager = popupView.findViewById(R.id.viewPagerTips);
        TabLayout dotsIndicator = popupView.findViewById(R.id.dotsIndicator);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(dotsIndicator, viewPager, (tab, position) -> {}).attach();

        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);//show popup
    }

    private float getPinchDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    //to reset the disabled
    @Override
    protected void onResume() {
        super.onResume();
        // Reset the button and hide the progress bar
        scanButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        flashToggle.setImageResource(R.drawable.flash_off);
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1 && requestCode == 101) {
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
                Intent intent = new Intent(CameraUI.this, ScanActivity.class);
                intent.putExtra("imageUri", resultUri.toString());
                overridePendingTransition(R.anim.slide_in, 0);
                startActivity(intent);

            }
        }
    }


    private void updateFlashToggleIcon() {
        if (isFlashOn) {
            flashToggle.setImageResource(R.drawable.flash_on);
        } else {
            flashToggle.setImageResource(R.drawable.flash_off);
        }
    }

    //function for starting the camera
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(isUsingFrontCamera ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK)
                        .build();

                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {

            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        imageCapture = new ImageCapture.Builder().build();
        preview.setSurfaceProvider(cameraview.getSurfaceProvider());
        cameraProvider.unbindAll();
        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        cameraControl = camera.getCameraControl();
        cameraInfo = camera.getCameraInfo();

        cameraInfo.getZoomState().observe(this, zoomState -> {
            if (zoomState != null) {
                float linearZoom = zoomState.getLinearZoom();
                zoomSeekBar.setProgress((int) (linearZoom * 100));
            }
        });

        if (cameraInfo.hasFlashUnit() && !isUsingFrontCamera) {
            flashToggle.setVisibility(View.VISIBLE);
        }else{
            flashToggle.setVisibility(View.GONE);
        }
    }

    //scan function using camera
    private void scan() {
        if (imageCapture == null) {
            return;
        }
        //para ma clear ang mga cache
        currentPhotoFile = new File(getCacheDir(),
        new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(currentPhotoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        //Uri savedUri = Uri.fromFile(photoFile);
                        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoFile.getAbsolutePath());
                            try {
                                // Rotate ang image kapag di naka organize
                                Bitmap rotatedBitmap = rotateBitmapIfRequired(bitmap, currentPhotoFile.getAbsolutePath());
                                Bitmap croppedBitmap = cropAndResizeBitmap(rotatedBitmap);
                                File croppedFile = saveBitmapToFile(croppedBitmap);

                                if (croppedFile != null) {
                                    //Uri croppedUri = Uri.fromFile(croppedFile);
                                    Intent intent = new Intent(CameraUI.this, ScanActivity.class);
                                    intent.putExtra("imageUri", Uri.fromFile(croppedFile).toString());
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                } else {
                                    Toast.makeText(CameraUI.this, "Failed to save cropped image", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(CameraUI.this, "Failed to process image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CameraUI.this, "Photo capture failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    //function for arranging the image properly
    private Bitmap rotateBitmapIfRequired(Bitmap bitmap, String photoPath) throws IOException {
        ExifInterface exif = new ExifInterface(photoPath);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateBitmap(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateBitmap(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateBitmap(bitmap, 270);
            default:
                return bitmap;
        }
    }

    //function to get the rotated bitmap result
    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    //funtion for auto cropping
    private Bitmap cropAndResizeBitmap(Bitmap bitmap) {
        // Define target dimensions (adjust as needed)
        int targetWidth = 1700;
        int targetHeight = 1700;
        int cropWidth = Math.min(bitmap.getWidth(), targetWidth);
        int cropHeight = Math.min(bitmap.getHeight(), targetHeight);
        int cropX = (bitmap.getWidth() - cropWidth) / 2; // Center crop horizontally
        int cropY = (bitmap.getHeight() - cropHeight) / 2; // Center crop vertically
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, cropX, cropY, cropWidth, cropHeight);
        return Bitmap.createScaledBitmap(croppedBitmap, targetWidth, targetHeight, false);
    }

    private File saveBitmapToFile(Bitmap bitmap) {

        File file = new File(getCacheDir(),
                new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + "_cropped.jpg");

        try (FileOutputStream out = new FileOutputStream(file)){
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            file = null;
        }
        return file;
    }
}