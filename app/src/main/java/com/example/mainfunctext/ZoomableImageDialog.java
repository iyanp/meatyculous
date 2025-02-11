package com.example.mainfunctext;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;

import com.github.chrisbanes.photoview.PhotoView;


public class ZoomableImageDialog extends Dialog {

    private Context context;
    private int imageResId;

    public ZoomableImageDialog(@NonNull Context context, int imageResId) {
        super(context);
        this.context = context;
        this.imageResId = imageResId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoomable_image_view);

        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Set the PhotoView with the image resource
        PhotoView photoView = findViewById(R.id.photo_view);
        photoView.setImageResource(imageResId);
    }


}
