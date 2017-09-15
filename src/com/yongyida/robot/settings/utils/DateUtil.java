package com.yongyida.robot.settings.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by panyzyw on 2016/12/27.
 */

public class DateUtil {

    public static String getLocalDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static long getLocalTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取今天是星期几,注意,星期日是7
     *
     * @return int day . 例如:1
     */
    public static int getCurDay() {
        long dateTime = System.currentTimeMillis();
        Date date = new Date(dateTime);
        int day = date.getDay();//0 = Sunday, 1 = Monday, 2 = Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday, 6 = Saturday
        if (day == 0)
            day = 7;
        return day;
    }

    public static long datetimeTotimestamp(String datetime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static long getTimestampForRepeat(String time, String days) {
        long timestamp = datetimeTotimestamp(getLocalDate() + " " + time);
        long curTimestatmp = getLocalTimestamp();
        int curDay = DateUtil.getCurDay();
        String[] arrayDay = days.split(",");
        for (int i = 0; i < arrayDay.length; i++) {
            int day = Integer.parseInt(arrayDay[i]);
            int result = curDay - day;
            if (days.length() == 1) { //重复一天
                if (result == 0) {
                    if (timestamp - curTimestatmp > 0) {
                        return timestamp;
                    } else {
                        return timestamp + 7 * 24 * 60 * 60 * 1000;
                    }
                } else if (result > 0) {
                    return timestamp + (7 - result) * 24 * 60 * 60 * 1000;
                } else if (result < 0) {
                    return timestamp + Math.abs(result) * 24 * 60 * 60 * 1000;
                }
            } else if (days.length() == 7) { //重复每一天
                if (timestamp - curTimestatmp > 0) {
                    return timestamp;
                } else {
                    timestamp = timestamp + 1 * 24 * 60 * 60 * 1000;
                    return timestamp;
                }
            } else { //重复多天
                int firstDay = Integer.parseInt(days.charAt(0) + "");
                int lastDay = Integer.parseInt(days.charAt(days.length() - 1) + "");
                if ((curDay >= firstDay) && (curDay < lastDay)) {
                    if (result == 0) {
                        if (timestamp - curTimestatmp > 0) {
                            return timestamp;
                        } else {
                            continue;
                        }
                    } else if (result > 0) {
                        continue;
                    } else if (result < 0) {
                        return timestamp + Math.abs(result) * 24 * 60 * 60 * 1000;
                    }
                } else if (curDay >= lastDay) {
                    if (timestamp - curTimestatmp > 0) {
                        return timestamp;
                    } else {
                        return timestamp + (7 - curDay + firstDay) * 24 * 60 * 60 * 1000;
                    }
                } else if (curDay < firstDay) {
                    return timestamp + (firstDay - curDay) * 24 * 60 * 60 * 1000;
                }
            }
        }
        return -1;
    }
}
