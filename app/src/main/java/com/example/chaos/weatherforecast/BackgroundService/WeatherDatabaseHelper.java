package com.example.chaos.weatherforecast.BackgroundService;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chaos on 2016/2/15.
 */
public class WeatherDatabaseHelper extends SQLiteOpenHelper {
    public WeatherDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table wea_hour(" +
                "id integer primary key autoincrement," +
                "temp text," +
                "tianqi text" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
