package com.example.mainfunctext;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter  extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a fragment based on position
        switch (position) {
            case 0:
                return new QuickTipsFragment(); // First page
            case 1:
                return new OtherContentFragment(); // Second page
            default:
                throw new IllegalStateException("Unexpected position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of pages
    }
}
