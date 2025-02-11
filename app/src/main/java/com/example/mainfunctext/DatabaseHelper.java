package com.example.mainfunctext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.format.DateTimeFormatter;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CapstoneDatabase.db";
    private static final int DATABASE_VERSION = 5;

    //table names for history
    private static final String TABLE_PSE = "PSE";
    private static final String TABLE_RFN = "RFN";
    private static final String TABLE_DFD = "DFD";


    // column for each history table (PSE, RFN, DFD)
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE_URI = "imageUri";
    private static final String COLUMN_DETECTED_COLOR = "detectedColor";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //for history tables
        // create table for PSE
        String CREATE_PSE_TABLE = "CREATE TABLE " + TABLE_PSE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_IMAGE_URI + " TEXT, "
                + COLUMN_DETECTED_COLOR + " TEXT, "
                + COLUMN_TIMESTAMP + " TEXT)";
        db.execSQL(CREATE_PSE_TABLE);
        //end of PSE table

        // create table for rfn
        String CREATE_RFN_TABLE = "CREATE TABLE " + TABLE_RFN + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_IMAGE_URI + " TEXT, "
                + COLUMN_DETECTED_COLOR + " TEXT, "
                + COLUMN_TIMESTAMP + " TEXT)";
        db.execSQL(CREATE_RFN_TABLE);
        //end of RFN table

        //create table for dfd
        String CREATE_DFD_TABLE = "CREATE TABLE " + TABLE_DFD + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_IMAGE_URI + " TEXT, "
                + COLUMN_DETECTED_COLOR + " TEXT, "
                + COLUMN_TIMESTAMP + " TEXT)";
        db.execSQL(CREATE_DFD_TABLE);
        //end of DFD table
        //end of history's tables

        //color spaces for quality

    }

    //for updating the table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RFN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DFD);
        onCreate(db);
    }

    //for inserting data
    public boolean insertPSEData(String imageUri, String detectedColor, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IMAGE_URI, imageUri);
        contentValues.put(COLUMN_DETECTED_COLOR, detectedColor);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);

        long result = db.insert(TABLE_PSE, null, contentValues);
        return result != -1;
    }

    // Insert data into RFN table
    public boolean insertRFNData(String imageUri, String detectedColor, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IMAGE_URI, imageUri);
        contentValues.put(COLUMN_DETECTED_COLOR, detectedColor);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);

        long result = db.insert(TABLE_RFN, null, contentValues);
        return result != -1;
    }

    // Insert data into DFD table
    public boolean insertDFDData(String imageUri, String detectedColor, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IMAGE_URI, imageUri);
        contentValues.put(COLUMN_DETECTED_COLOR, detectedColor);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);

        long result = db.insert(TABLE_DFD, null, contentValues);
        return result != -1;
    }

    //pse
    public Cursor getAllDataPSE() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PSE, null);
    }

    //rfn
    public Cursor getAllDataRFN() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RFN, null);
    }

    //dfd
    public Cursor getAllDataDFD() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_DFD, null);
    }

    //deletion of items in HistoryFragment (clear all)
    public void clearAllData(String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String tableName;

        switch (type) {
            case "PSE":
                tableName = TABLE_PSE;
                break;
            case "RFN":
                tableName = TABLE_RFN;
                break;
            case "DFD":
                tableName = TABLE_DFD;
                break;
            default:
                tableName = null;
        }

        if (tableName != null) {
            db.execSQL("DELETE FROM " + tableName);
        }
        db.close();
    }

    //for deletion of column
    public boolean deleteHistoryItem(HistoryItem item, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        String tableName;
        switch (type) {
            case "PSE":
                tableName = TABLE_PSE;
                break;
            case "RFN":
                tableName = TABLE_RFN;
                break;
            case "DFD":
                tableName = TABLE_DFD;
                break;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }


        // Convert LocalDateTime to String

        String timestampString = item.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return db.delete(tableName, COLUMN_ID + " = ?", new String[]{String.valueOf(item.getId())}) > 0;
    }

    //to log the contents of tables (history)
    public void logTableContents(String tableName) {
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null)) {

            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int imageUriIndex = cursor.getColumnIndex(COLUMN_IMAGE_URI);
            int detectedColorIndex = cursor.getColumnIndex(COLUMN_DETECTED_COLOR);
            int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(idIndex);
                    String imageUri = cursor.isNull(imageUriIndex) ? null : cursor.getString(imageUriIndex);
                    String detectedColor = cursor.isNull(detectedColorIndex) ? null : cursor.getString(detectedColorIndex);
                    String timestamp = cursor.isNull(timestampIndex) ? null : cursor.getString(timestampIndex);

                    StringBuilder sb = new StringBuilder();
                    sb.append("Row - ID: ").append(id).append(", Image URI: ").append(imageUri)
                            .append(", Detected Color: ").append(detectedColor).append(", Timestamp: ").append(timestamp);

                } while (cursor.moveToNext());
            } else {
            }
        } catch (Exception e) {

        }
    }

    //delete history kasama data


}
