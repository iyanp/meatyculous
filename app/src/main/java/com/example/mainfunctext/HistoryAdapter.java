package com.example.mainfunctext;


import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //for grouping datas
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    private List<Object> historyItems;
    private DatabaseHelper databaseHelper;
    private Context context;

    public HistoryAdapter(Context context, Map<String, List<HistoryItem>> groupedItems) {
        this.context = context;
        this.databaseHelper = new DatabaseHelper(context);
        historyItems = new ArrayList<>();
        for (Map.Entry<String, List<HistoryItem>> entry : groupedItems.entrySet()) {
            historyItems.add(entry.getKey()); //para makuha ang header papunta sa string
            historyItems.addAll(entry.getValue()); //list ng HistoryItem
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (historyItems.get(position) instanceof String) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //second edit for grouping data
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_item, parent, false); //ito yung layout para sa header
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_item_layout, parent, false); //ito yung layout para sa historyItems
            return new ItemViewHolder(view);
        }
    }

    //delete item method
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            String headerTitle = (String) historyItems.get(position);
            ((HeaderViewHolder) holder).headerTitle.setText(headerTitle); //para ma display ang header title date 
        } else {
            HistoryItem item = (HistoryItem) historyItems.get(position);
            ((ItemViewHolder) holder).bind(item); //para ma display ang historyItem Data


        }
    }

    private void cleanupTemporaryFiles() {
        File cacheDir = context.getCacheDir(); // Use `context` if available or pass it in your adapter constructor
        if (cacheDir.isDirectory()) {
            for (File file : cacheDir.listFiles()) {
                if (file != null && file.isFile()) {
                    file.delete();
                }
            }
        }
    }


    //tatanggalin toh on final debug
    private void refreshData(String type) {
        List<HistoryItem> newHistoryItems = new ArrayList<>();
        if ("PSE".equals(type)) {
            newHistoryItems = getPSEData();
        } else if ("RFN".equals(type)) {
            newHistoryItems = getRFNData();
        } else if ("DFD".equals(type)) {
            newHistoryItems = getDFDData();
        }

        // Group the newly fetched items by month
        Map<String, List<HistoryItem>> groupedItems = groupItemsByMonth(newHistoryItems);

        // Clear the existing historyItems list
        historyItems.clear();

        // Update the adapter's historyItems with the new grouped data
        for (Map.Entry<String, List<HistoryItem>> entry : groupedItems.entrySet()) {
            historyItems.add(entry.getKey()); // Add the month-year header
            historyItems.addAll(entry.getValue()); // Add the list of HistoryItems
        }

        // Notify the adapter about the updated data
        notifyDataSetChanged();
    }



    private List<HistoryItem> getPSEData() {
        List<HistoryItem> historyItems = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllDataPSE();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String imageUri = cursor.getString(1);
            String detectedColor = cursor.getString(2);
            String timestamp = cursor.getString(3);
            String type = "PSE";
            historyItems.add(new HistoryItem(id,imageUri, detectedColor, timestamp, type));
        }
        cursor.close();
        Collections.sort(historyItems, Comparator.comparing(HistoryItem::getTimestamp).reversed());
        return historyItems;
    }

    private List<HistoryItem> getRFNData() {
        List<HistoryItem> historyItems = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllDataRFN();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String imageUri = cursor.getString(1);
            String detectedColor = cursor.getString(2);
            String timestamp = cursor.getString(3);
            String type = "RFN";
            historyItems.add(new HistoryItem(id, imageUri, detectedColor, timestamp, type));
        }
        cursor.close();

        Collections.sort(historyItems, Comparator.comparing(HistoryItem::getTimestamp).reversed());
        return historyItems;
    }

    private List<HistoryItem> getDFDData() {
        List<HistoryItem> historyItems = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllDataDFD();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String imageUri = cursor.getString(1);
            String detectedColor = cursor.getString(2);
            String timestamp = cursor.getString(3);
            String type = "DFD";
            historyItems.add(new HistoryItem(id, imageUri, detectedColor, timestamp, type));
        }
        cursor.close();
        Collections.sort(historyItems, Comparator.comparing(HistoryItem::getTimestamp).reversed());
        return historyItems;
    }

    //para ma sort by date
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




    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    // ViewHolder for the headers
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.headerTitle); // TextView to show "September 2024"
        }
    }

    // ViewHolder for the history items
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageUri;
        TextView detectedColor, timestamp, text1, text2, text3, text4, text5, text6, textureText;

        FrameLayout deleteButton, colorFrame;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUri = itemView.findViewById(R.id.imageUri);
            detectedColor = itemView.findViewById(R.id.detectedColor);
            timestamp = itemView.findViewById(R.id.timestamp);
            text1 = itemView.findViewById(R.id.text1);
            text2 = itemView.findViewById(R.id.text2);
            text3 = itemView.findViewById(R.id.text3);
            text4 = itemView.findViewById(R.id.text4);
            text5 = itemView.findViewById(R.id.text5);
            text6 = itemView.findViewById(R.id.text6);
            textureText = itemView.findViewById(R.id.textureText);
            colorFrame = itemView.findViewById(R.id.colorFrame);
            //deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(HistoryItem item) {
            String detectedColorText = item.getDetectedColor();
            Glide.with(imageUri.getContext())
                    .load(item.getImageUri())
                    .into(imageUri);

            detectedColor.setText(detectedColorText);

            // Formatting date
            try {
                DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("MMMM dd yyyy, h:mm a");
                String formattedDate = item.getTimestamp().format(outputFormat);
                timestamp.setText(formattedDate);
            } catch (Exception e) {
                e.printStackTrace();
                timestamp.setText(item.getTimestamp().toString());
            }

            // Switch to handle different detected colors (PSE, RFN, DFD)
            switch (detectedColorText) {
                case "PSE":
                    text1.setText("PSE (PALE - SOFT - EXUDATIVE)");
                    text3.setText("Color: Pale");
                    colorFrame.setBackgroundColor(Color.parseColor("#e7ae99"));
                    textureText.setText("Soft");
                    text5.setText("Water-Holding Capacity: Low (high exudate)");
                    text6.setText("Cause: Rapid post-mortem pH decline while the carcass temperature is still high, often due to pre-slaughter stress.");
                    break;

                case "RFN":
                    text1.setText("RFN (RED - FIRM - NON-EXUDATIVE)");
                    text3.setText("Color: Reddish");
                    colorFrame.setBackgroundColor(Color.parseColor("#d2806c"));
                    textureText.setText("Firm");
                    text5.setText("Water-Holding Capacity: Normal (minimal exudate)");
                    text6.setText("Cause: Ideal post-mortem conditions, with a balanced pH decline.");
                    break;

                case "DFD":
                    text1.setText("DFD (DARK - FIRM - DRY)");
                    text3.setText("Color: Dark");
                    colorFrame.setBackgroundColor(Color.parseColor("#bc5f54"));
                    textureText.setText("Firm");
                    text5.setText("Water-Holding Capacity: High (low exudate)");
                    text6.setText("Cause: Glycogen depletion in muscles before slaughter, often due to long-term stress or exhaustion.");
                    break;

                default:
                    text1.setText("Color unknown");
                    text3.setText("Unknown Color");
                    text4.setText("Unknown Texture");
                    text5.setText("Unknown Water Holding");
                    break;
            }
        }
    }

    //refresh data


}
