package com.yongyida.robot.settings.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yongyida.robot.settings.bean.ClockInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by panyzyw on 2016/12/28.
 */

public class AlarmUtil {

    private static final String TAG = "AlarmUtil";
    public static final String ACTION_ALARM = "com.yongyida.SettingsClockBroadcast";

    public static void setAlram(Context context) {
        ArrayList<ClockInfo> list = refreshNotice(context);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(getPendingIntent(context, null));
        if (list.size() > 0) {
            am.set(AlarmManager.RTC_WAKEUP, Long.parseLong(list.get(0).getTimestamp()), getPendingIntent(context, list.get(0)));
        }
    }

    public static PendingIntent getPendingIntent(Context context, ClockInfo clockInfo) {
        Intent alarmIntent = new Intent();
        alarmIntent.setAction(ACTION_ALARM);
        alarmIntent.putExtra("clockInfo", clockInfo);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static ArrayList<ClockInfo> refreshNotice(Context context) {
        ArrayList<ClockInfo> list = DB.getInstanc(context).query();
        long curTimestamp = DateUtil.getLocalTimestamp();
        if (list.size() > 0) {
            for (ClockInfo clockInfo : list) {
                long timestamp = Long.parseLong(clockInfo.getTimestamp());
                long setTimestamp;
                if (timestamp - curTimestamp < 0) {
                    setTimestamp = DateUtil.getTimestampForRepeat(clockInfo.getTime(), clockInfo.getDays());
                    clockInfo.setTimestamp(setTimestamp + "");
                    DB.getInstanc(context).update(clockInfo);
                }
            }
        }
        list = DB.getInstanc(context).query();
        return sortNoticeBytimestamp(list);
    }

    public static ArrayList<ClockInfo> sortNoticeBytimestamp(ArrayList<ClockInfo> list) {
        Collections.sort(list, new Comparator<ClockInfo>() {
            @Override
            public int compare(ClockInfo o1, ClockInfo o2) {
                int result = o1.getTimestamp().compareTo(o2.getTimestamp());
                if (result < 0) {
                    return -1;
                } else if (result > 0) {
                    return 1;
                }
                return 0;
            }
        });
        return list;
    }

}
