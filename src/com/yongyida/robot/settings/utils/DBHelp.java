package com.yongyida.robot.settings.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by panyzyw on 2016/12/27.
 */

public class DBHelp extends SQLiteOpenHelper {

    public static final String DB_VERSION = "1";
    public static final String TABLE_NAME = "settingsclock";

    private static DBHelp instance;
    private String sql_create_table = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            "(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "title TEXT," +
            "timestamp TEXT," +
            "time TEXT," +
            "days TEXT," +
            "repeat TEXT," +
            "isonpen TEXT" +
            ")";

    public static DBHelp getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelp(context);
        }
        return instance;
    }

    public DBHelp(Context context) {
        super(context, TABLE_NAME, null, Integer.parseInt(DB_VERSION));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql_create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
