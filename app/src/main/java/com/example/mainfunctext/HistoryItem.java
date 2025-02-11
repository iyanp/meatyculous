package com.example.mainfunctext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoryItem {
    private int id;
    private String imageUri;
    private String detectedColor;
    private LocalDateTime timestamp;
    private String type;


    public HistoryItem(int id, String imageUri, String detectedColor, String timestamp, String type) {
        this.id = id;
        this.imageUri = imageUri;
        this.detectedColor = detectedColor;
        this.timestamp = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.type = type;
    }

    //para makuha data and time for saving
    public LocalDateTime getTimestamp() {
        return timestamp;
    }


    public String getImageUri() {
        return imageUri;
    }

    //para makuha result
    public String getDetectedColor() {
        return detectedColor;
    }

    //grouping the data
    public String getYearMonth() {
        return timestamp.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
    }

    //for deleting
    public String getType() {
        return type;
    }


    public int getId() {
        return id;
    }


}
