package com.yongyida.robot.settings.utils;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.app.SettingsApp;
import com.yongyida.robot.settings.bean.Constants;
import com.yongyida.robot.settings.utils.log.LogTool;

/**
 * @author Bright. Create on 2016/11/14 0014.
 */
public class ProviderUtils {
    final String TAG = ProviderUtils.class.getSimpleName();
    private static ProviderUtils utils;
    private static Context mContext;
    private String nameUri = "content://com.yongyida.robot.nameprovider/name";
    private final String ACTION_SETTINGS_CHANGED = "com.yydrobot.settings.ACTION_SETTINGS_CHANGED";

    private ProviderUtils() {
    }

    public static ProviderUtils getInstance() {
        if (utils == null) {
            utils = new ProviderUtils();
            mContext = SettingsApp.getAppContext();
        }
        return utils;
    }

    /**
     * 获得机器人名字
     */
    public String getRobotName(String defName) {
        try {
            String name = defName;
            Uri uri = Uri.parse(nameUri);
            ContentResolver resolver = mContext.getContentResolver();
            Cursor cursor = resolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex("name"));
            }
            cursor.close();

            return name;
        } catch (Exception e) {
            return defName;
        }
    }

    /**
     * 修改机器人名字
     */
    public void setRobotName(String newName) {
        try {
            newName = newName.trim();
            if (!newName.equals("") && newName.length() != 0) {
                Uri uri = Uri.parse(nameUri);
                ContentResolver resolver = mContext.getContentResolver();
                ContentValues values = new ContentValues();
                values.put("name", newName);
                resolver.update(uri, values, null, null);
            } else {
                new AlertDialog.Builder(mContext)
                        .setTitle(mContext.getResources().getString(R.string.change_name_dialog_str_error))
                        .setMessage(mContext.getResources().getString(R.string.change_name_dialog_str_error_msg))
                        .setPositiveButton(mContext.getResources().getString(R.string.change_name_dialog_str_btn_ok),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                        .create()
                        .show();
            }
        } catch (Exception e) {
            LogTool.showLog(TAG, "setRobotName", "Error: " + e.getLocalizedMessage());
        }
    }

    /**
     * 获取密码
     */
    public String getPassword() {
        try {
            Uri uri = Uri.parse(Constants.CONTENT_URI);
            ContentResolver resolver = mContext.getContentResolver();
            Cursor cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String password = cursor.getString(cursor.getColumnIndex("password"));
                cursor.close();
                return password;
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 设置密码
     *
     * @param password 新密码
     */
    public int setPassword(String password) {
        try {
            Uri uri = Uri.parse(Constants.CONTENT_URI);
            ContentValues values = new ContentValues();
            values.put("password", password);
            ContentResolver resolver = mContext.getContentResolver();
            int ret = resolver.update(Uri.withAppendedPath(uri, "password"), values, null, null);
            if (ret >= 0) {
                settingsChangedBroadcast("password", password, -1);
            }
            return ret;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 通用方法，获取provider数据值
     *
     * @param key 获取某值的key
     * @return int型，-1 错误。
     */
    int getProviderIntValue(String key) {
        try {
            Uri uri = Uri.parse(Constants.CONTENT_URI);
            ContentResolver resolver = mContext.getContentResolver();
            Cursor cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int value = cursor.getInt(cursor.getColumnIndex(key));
                cursor.close();
                return value;
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 通用方法，修改provider数据值
     *
     * @param key   需要修改的key
     * @param value 设置修改的值
     * @return int型，-1 错误。
     */
    int setProviderIntValue(String key, int value) {
        try {
            Uri uri = Uri.parse(Constants.CONTENT_URI);
            ContentValues values = new ContentValues();
            values.put(key, value);
            ContentResolver resolver = mContext.getContentResolver();
            int ret = resolver.update(Uri.withAppendedPath(uri, key), values, null, null);
            if (ret >= 0) {
                settingsChangedBroadcast(key, null, value);
            }
            return ret;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 获取机器人性别配置，1 : 男孩; 0 : 女孩. -1 : 错误
     */
    public int getGendar() {
        return getProviderIntValue("gendar");
    }

    /**
     * 设置机器人性别
     *
     * @param gendar 1 : 男孩; 0 : 女孩.
     * @return 返回-1 : 错误
     */
    public int setGendar(int gendar) {
        return setProviderIntValue("gendar", gendar);
    }

    /**
     * 获取语音语速
     *
     * @return 1 - 快， 2 - 中， 3 - 慢
     */
    public int getVoiceSpeed() {
        return getProviderIntValue("voice_speed");
    }

    /**
     * 设置语音语速
     *
     * @param speed 1 - 快， 2 - 中， 3 - 慢
     * @return -1 失败
     */
    public int setVoiceSpeed(int speed) {
        return setProviderIntValue("voice_speed", speed);
    }

    /**
     * 获取语音语调
     *
     * @return 1 - 高， 2 - 中， 3 - 低
     */
    public int getVoiceTone() {
        return getProviderIntValue("voice_tone");
    }

    /**
     * 设置语音语调
     *
     * @param tone 1 - 高， 2 - 中， 3 - 低
     * @return -1 失败
     */
    public int setVoiceTone(int tone) {
        return setProviderIntValue("voice_tone", tone);
    }

    /**
     * 获取发音人声
     *
     * @return 1 男, 2 女
     */
    public int getVoiceSpeaker() {
        return getProviderIntValue("voice_speaker");
    }

    /**
     * 设置发音人声
     *
     * @param speaker 1 男, 2 女
     * @return -1 失败
     */
    public int setVoiceSpeaker(int speaker) {
        return setProviderIntValue("voice_speaker", speaker);
    }

    /**
     * 获取表情
     *
     * @return 1, 2, 3
     */
    public int getFacing() {
        return getProviderIntValue("facing");
    }

    /**
     * 设置表情
     *
     * @param facing 1 男, 2 女
     * @return -1 失败
     */
    public int setFacing(int facing) {
        return setProviderIntValue("facing", facing);
    }

    /**
     * 配置更改后，发广播通知其他应用
     *
     * @param key 对应配置的字段名
     * @param password 当设置int值时，请把密码设置为null；当设置密码时，请把value设置为-1
     * @param value 改变后的值
     */
    public void settingsChangedBroadcast(String key, String password, int value) {
        Intent intent = new Intent();
        intent.setAction(ACTION_SETTINGS_CHANGED);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        if (password != null) {
            intent.putExtra("key", key);
            intent.putExtra("value", password);
        } else if(value != -1) {
            intent.putExtra("key", key);
            intent.putExtra("value", value);
        }
        SettingsApp.getAppContext().sendBroadcast(intent);
    }

}
