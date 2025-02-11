package com.example.mainfunctext;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import com.codesgood.views.JustifiedTextView;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.content.Intent;


public class Information extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
    }
}