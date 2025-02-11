package com.example.mainfunctext;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private DatabaseHelper databaseHelper;
    private String type;
    private List<HistoryItem> historyItems;
    private RecyclerView recyclerView;
    private View thumb;


    private int totalScrollRange;
    private int visibleHeight;
    private int thumbHeight;


    public static HistoryFragment newInstance(String type) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
        databaseHelper = new DatabaseHelper(getContext());
        setHasOptionsMenu(true);
    }


    //edit here for grouping the data
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        FrameLayout clear = view.findViewById(R.id.clearAll);
        TextView empty = view.findViewById(R.id.tvEmptyPlaceholder);


        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setScrollbarFadingEnabled(true);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setAdapter(new HistoryAdapter(getContext(), getHistoryItems()));

        recyclerView.setAdapter((RecyclerView.Adapter) historyItems); //para call function

        thumb = view.findViewById(R.id.thumb);

        //clear all items
        clear.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete all items? This include PSE, RFN, and DFD.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Perform the delete action
                        recyclerView.animate().alpha(0f).setDuration(300).withEndAction(() -> {

                            // Clear all data for all types
                            databaseHelper.clearAllData("PSE");
                            databaseHelper.clearAllData("RFN");
                            databaseHelper.clearAllData("DFD");

                            // Clear the list and notify the adapter
                            historyItems.clear();
                            recyclerView.getAdapter().notifyDataSetChanged();

                            recyclerView.setAlpha(1f);

                            cleanupTemporaryFiles();

                            // Update UI visibility
                            empty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            clear.setVisibility(View.GONE);

                            Toast.makeText(getContext(), "All items cleared.", Toast.LENGTH_SHORT).show();
                        }).start();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Dismiss the dialog if the user cancels
                        dialog.dismiss();
                    })
                    .show();
        });

        historyItems = new ArrayList<>();
        if ("PSE".equals(type)) {
            historyItems = getPSEData();
        } else if ("RFN".equals(type)) {
            historyItems = getRFNData();
        } else if ("DFD".equals(type)) {
            historyItems = getDFDData();
        }

        //conditon para sa placeholder at clear button
         if (historyItems.isEmpty()) {
         empty.setVisibility(View.VISIBLE);
         recyclerView.setVisibility(View.GONE);
         clear.setVisibility(View.GONE);
         } else {
         empty.setVisibility(View.GONE);
         recyclerView.setVisibility(View.VISIBLE);
         clear.setVisibility(View.VISIBLE);


         Map<String, List<HistoryItem>> groupedItems = groupItemsByMonth(historyItems);
         HistoryAdapter adapter = new HistoryAdapter(requireContext(), groupedItems);
         recyclerView.setAdapter(adapter);
         }


        Map<String, List<HistoryItem>> groupedItems = groupItemsByMonth(historyItems);
        HistoryAdapter adapter = new HistoryAdapter(requireContext(), groupedItems);
        recyclerView.setAdapter(adapter);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Calculate the current scroll offset (how far the user has scrolled)
                int scrollY = recyclerView.computeVerticalScrollOffset();
                float ratio = (float) scrollY / (totalScrollRange - visibleHeight);
                int thumbTop = (int) (ratio * (visibleHeight - thumbHeight));

                // Update the thumb's position relative to scroll
                thumb.setTranslationY(thumbTop);
            }
        });
        return view;
    }

    private void cleanupTemporaryFiles() {
        // Obtain the cache directory for the app
        File cacheDir = requireContext().getCacheDir();

        // Verify that the cache directory is a valid directory
        if (cacheDir != null && cacheDir.isDirectory()) {
            // List all files in the directory and delete them if they exist
            for (File file : cacheDir.listFiles()) {
                if (file.isFile()) {
                    file.delete(); // Delete the file
                }
            }
        }
    }
    //edit for each historyitem
    private List<HistoryItem> getPSEData() {
        List<HistoryItem> historyItems = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllDataPSE();
        if (cursor != null){
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String imageUri = cursor.getString(1);
                String detectedColor = cursor.getString(2);
                String timestamp = cursor.getString(3);
                String type = "PSE";
                historyItems.add(new HistoryItem(id,imageUri, detectedColor, timestamp, type));
            }
            cursor.close();
        }
        Collections.sort(historyItems, Comparator.comparing(HistoryItem::getTimestamp).reversed());
        return historyItems;
    }

    private List<HistoryItem> getRFNData() {
        List<HistoryItem> historyItems = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllDataRFN();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String imageUri = cursor.getString(1);
                String detectedColor = cursor.getString(2);
                String timestamp = cursor.getString(3);
                String type = "RFN";
                historyItems.add(new HistoryItem(id, imageUri, detectedColor, timestamp, type));
            }
            cursor.close();

        }
        Collections.sort(historyItems, Comparator.comparing(HistoryItem::getTimestamp).reversed());
        return historyItems;
    }

    private List<HistoryItem> getDFDData() {
        List<HistoryItem> historyItems = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllDataDFD();
        if(cursor != null){
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String imageUri = cursor.getString(1);
                String detectedColor = cursor.getString(2);
                String timestamp = cursor.getString(3);
                String type = "DFD";
                historyItems.add(new HistoryItem(id, imageUri, detectedColor, timestamp, type));
            }
            cursor.close();
        }

        Collections.sort(historyItems, Comparator.comparing(HistoryItem::getTimestamp).reversed());
        return historyItems;
    }

    //for grouping the data
    private Map<String, List<HistoryItem>> groupItemsByMonth(List<HistoryItem> historyItems) {
        Map<String, List<HistoryItem>> groupedMap = new LinkedHashMap<>();

        for (HistoryItem item : historyItems) {
            String yearMonth = item.getYearMonth();

            if (!groupedMap.containsKey(yearMonth)) {
                groupedMap.put(yearMonth, new ArrayList<>());
            }
            groupedMap.get(yearMonth).add(item);
        }
        return groupedMap;
    }
}
