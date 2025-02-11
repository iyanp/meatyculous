package com.example.mainfunctext;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Button;
import android.content.SharedPreferences;
import android.widget.CompoundButton;
import android.util.Log;
import android.content.Intent;
import android.widget.Toast;
import android.text.Html;
import android.widget.TextView;
import com.codesgood.views.JustifiedTextView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.core.widget.NestedScrollView;

public class TermsAndCondition extends AppCompatActivity {

    private CheckBox myCheckBox;
    private Button myButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);


        myCheckBox = findViewById(R.id.myCheckBox);
        myButton = findViewById(R.id.myButton);




        myCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            myButton.setEnabled(isChecked);

            if (myCheckBox.isChecked()){
                myButton.setTextColor(Color.WHITE);
            }

            else {
                myButton.setTextColor(Color.GRAY);
            }
        });


        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(TermsAndCondition.this, MainTourMode.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            }

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
    }

}