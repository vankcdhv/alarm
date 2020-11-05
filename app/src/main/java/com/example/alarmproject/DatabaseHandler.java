package com.example.alarmproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "Alarm.db";
    private static final String TABLE_NAME = "Alarm";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_RINGTONE = "ringtone";
    private static final String COLUMN_VIBRATE = "vibrate";
    private static final String COLUMN_ACTIVE = "active";


    public DatabaseHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_PRODUCT = " CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_TITLE + " TEXT, " + COLUMN_TIME + " TEXT, " + COLUMN_DATE + " TEXT, " + COLUMN_RINGTONE + " TEXT, " + COLUMN_VIBRATE + " BOOLEAN, " + COLUMN_ACTIVE + " BOOLEAN)";
        db.execSQL(CREATE_TABLE_PRODUCT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public long addAlarm(Alarm alarm) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, alarm.getTitle());
        values.put(COLUMN_TIME, alarm.getTime());
        values.put(COLUMN_DATE, alarm.getDate());
        values.put(COLUMN_RINGTONE, alarm.getRingtone());
        values.put(COLUMN_VIBRATE, alarm.isVibrate());
        values.put(COLUMN_ACTIVE, alarm.isActive());
        SQLiteDatabase db = getWritableDatabase();
        Long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result;
    }

    public int updateAlarm(Alarm alarm) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, alarm.getId());
        values.put(COLUMN_TITLE, alarm.getTitle());
        values.put(COLUMN_TIME, alarm.getTime());
        values.put(COLUMN_DATE, alarm.getDate());
        values.put(COLUMN_RINGTONE, alarm.getRingtone());
        values.put(COLUMN_VIBRATE, alarm.isVibrate());
        values.put(COLUMN_ACTIVE, alarm.isActive());
        SQLiteDatabase db = getWritableDatabase();
        int result = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{alarm.getId()});
        db.close();
        return result;
    }

    public ArrayList<Alarm> getListAlarm() {
        ArrayList<Alarm> list = new ArrayList<>();
        String query = " SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String title = cursor.getString(1);
            String time = cursor.getString(2);
            String date = cursor.getString(3);
            String ringtone = cursor.getString(4);
            int vibrate = cursor.getInt(5);
            int active = cursor.getInt(6);
            Alarm alarm = new Alarm(id, title, time, date, ringtone, vibrate == 1, active == 1);
            list.add(alarm);
        }
        cursor.close();
        db.close();
        return list;
    }

    public int removeAlarm(Alarm alarm) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_ID + " =?", new String[]{alarm.getId()});
        db.close();
        return result;
    }

}
