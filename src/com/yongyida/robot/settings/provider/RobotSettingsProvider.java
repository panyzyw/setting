package com.yongyida.robot.settings.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import com.yongyida.robot.settings.utils.SharedPreferencesUtils;

import java.util.Map;
import java.util.Set;

import static com.yongyida.robot.settings.utils.SharedPreferencesUtils.getAll;


public class RobotSettingsProvider extends ContentProvider {

    private static UriMatcher uriMatcher;
    private static final String authority = "com.yongyida.robot.settingsprovider";
    private static final String CURSOR_COLUMN_VALUE = "cursor_value";
    private static final int QUERY_CODE = 0;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //添加uri
        uriMatcher.addURI(authority, "query", QUERY_CODE);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        boolean isChecked;
        MatrixCursor cursor = new MatrixCursor(new String[]{CURSOR_COLUMN_VALUE});
        Object[] rows = new Object[1];
        if ("resttime_switch".equals(selection))
            isChecked = (boolean) SharedPreferencesUtils.getParam(getContext(), selection, false);
        else
            isChecked = (boolean) SharedPreferencesUtils.getParam(getContext(), selection, true);
        if (isChecked) {
            rows[0] = 1;
        } else {
            rows[0] = 0;
        }
        cursor.addRow(rows);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        insert(uri, values);
        return 0;
    }
}