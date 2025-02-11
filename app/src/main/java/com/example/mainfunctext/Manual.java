package com.example.mainfunctext;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.text.Html;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;


public class Manual extends AppCompatActivity {

    private FrameLayout viewTutorMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        viewTutorMode = findViewById(R.id.tutorialModeManual);

        viewTutorMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(Manual.this, MainTourMode.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });

    }
    //pop up
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
    }
}