package com.yongyida.robot.settings.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yongyida.robot.settings.bean.ClockInfo;

import java.util.ArrayList;


/**
 * Created by panyzyw on 2016/12/27.
 */

public class DB {
    private static final String TAG = "DB";
    private static DBHelp dbHelp;
    private static SQLiteDatabase db;
    private static DB instance;

    public long insert(ClockInfo clockInfo) {
        return db.insert(DBHelp.TABLE_NAME, null, getContentValues(clockInfo));
    }

    public int update(ClockInfo clockInfo) {
        return db.update(DBHelp.TABLE_NAME, getContentValues(clockInfo), "_id=?", new String[]{clockInfo.getId()});
    }

    public ArrayList<ClockInfo> query() {
        ArrayList<ClockInfo> list = new ArrayList<ClockInfo>();
        Cursor cursor = db.query(DBHelp.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ClockInfo clockInfo = getNotice(cursor);
                list.add(clockInfo);
            }
            cursor.close();
        }
        return list;
    }

    public Cursor queryAll() {
        ArrayList<ClockInfo> list = new ArrayList<ClockInfo>();
        Cursor cursor = db.query(DBHelp.TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }

    public ClockInfo queryById(String id) {
        Cursor cursor = db.query(DBHelp.TABLE_NAME, null, "_id=?", new String[]{id}, null, null, null);
        ClockInfo clockInfo = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                clockInfo = getNotice(cursor);
            }
            cursor.close();
        }
        return clockInfo;
    }


    public ContentValues getContentValues(ClockInfo noticeNew) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", noticeNew.getTitle());
        contentValues.put("timestamp", noticeNew.getTimestamp());
        contentValues.put("time", noticeNew.getTime());
        contentValues.put("days", noticeNew.getDays());
        contentValues.put("repeat", noticeNew.getRepeat());
        contentValues.put("isonpen", noticeNew.getIsonpen());
        return contentValues;
    }

    public ClockInfo getNotice(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex("_id"));
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
        String time = cursor.getString(cursor.getColumnIndex("time"));
        String days = cursor.getString(cursor.getColumnIndex("days"));
        String repeat = cursor.getString(cursor.getColumnIndex("repeat"));
        String isonpen = cursor.getString(cursor.getColumnIndex("isonpen"));
        ClockInfo noticeNew = new ClockInfo(id, title, timestamp, time, days, repeat, isonpen);
        return noticeNew;
    }

    public DB(Context context) {
        if (dbHelp == null)
            dbHelp = DBHelp.getInstance(context);
        db = dbHelp.getWritableDatabase();
    }

    public static DB getInstanc(Context context) {
        if (instance == null) {
            instance = new DB(context);
        }
        return instance;
    }

}
