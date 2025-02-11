package com.example.mainfunctext;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SnapTips extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_tips);


        ViewPager2 viewPager1 = findViewById(R.id.viewPagerTips);
        TabLayout dotsIndicator = findViewById(R.id.dotsIndicator);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager1.setAdapter(adapter);

        new TabLayoutMediator(dotsIndicator, viewPager1, (tab, position) -> {

        }).attach();

    }
}