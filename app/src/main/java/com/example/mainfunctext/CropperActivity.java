package com.example.mainfunctext;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public class CropperActivity extends AppCompatActivity {

    String result;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readIntent();

        String dest_uri=new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.ucrop_color_toolbar));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.ucrop_color_statusbar));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.white));
        options.setCropGridStrokeWidth(2);
        options.setCompressionQuality(100);
        options.setToolbarTitle("Crop Image");
        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.ucrop_color_bg));



        UCrop.of(fileUri, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                .withOptions(options)
                .withMaxResultSize(4000, 4000)
                .start(CropperActivity.this);

    }

    private void readIntent() {
        Intent intent=getIntent();

        if(intent.getExtras()!=null)
        {
            result=intent.getStringExtra("DATA");
            fileUri=Uri.parse(result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode==UCrop.REQUEST_CROP){

            final Uri resultUri=UCrop.getOutput(data);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT", resultUri+"");
            setResult(-1, returnIntent);
            finish();

        }
        else if(resultCode==UCrop.RESULT_ERROR)
        {
            final Throwable cropError= UCrop.getError(data);

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
