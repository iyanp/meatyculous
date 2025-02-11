package com.example.mainfunctext;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class HistoryUI extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ImageView returnHome;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_ui);
        databaseHelper = new DatabaseHelper(this);

        //setting ng variables
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        returnHome = findViewById(R.id.returnHomeHistoryUi);
        //setting ng adapter
        viewPager.setAdapter(new HistoryPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("PSE");
                    break;
                case 1:
                    tab.setText("RFN");
                    break;
                case 2:
                    tab.setText("DFD");
                    break;
            }
        }).attach();

        returnHome.setOnClickListener(v -> {
            Intent mainIntent = new Intent(HistoryUI.this, MainActivity.class);
            startActivity(mainIntent);
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
        });
        //para makuha historyItems mula sa HistoryFragment
    }
    //for tab and pager
    private class HistoryPagerAdapter extends FragmentStateAdapter {

        public HistoryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return HistoryFragment.newInstance("PSE");
                case 1:
                    return HistoryFragment.newInstance("RFN");
                case 2:
                    return HistoryFragment.newInstance("DFD");
                default:
                    return HistoryFragment.newInstance("PSE"); //para default na nasa pse lagi kapag nag start activity
            }
        }
        @Override
        public int getItemCount() {
            return 3; // three counts kasi PSE, RFN at DFD
        }
    }
    //end of tab and pager
}