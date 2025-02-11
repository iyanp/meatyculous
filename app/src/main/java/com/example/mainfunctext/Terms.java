package com.example.mainfunctext;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.text.Html;
import android.widget.TextView;

import android.view.View;

import android.os.Bundle;

import com.codesgood.views.JustifiedTextView;

public class Terms extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
    }
}