package com.yongyida.robot.settings.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by panyzyw on 2016/12/27.
 */

public class ClockInfo implements Parcelable {
    private String id;
    private String title;
    private String timestamp;
    private String time;
    private String days;
    private String repeat;
    private String isonpen;

    public ClockInfo() {
    }

    public ClockInfo(String id, String title, String timestamp, String time, String days, String repeat, String isonpen) {
        this(title, timestamp, time, days, repeat, isonpen);
        this.id = id;
    }

    public ClockInfo(String title, String timestamp, String time, String days, String repeat, String isonpen) {
        this.title = title;
        this.timestamp = timestamp;
        this.time = time;
        this.days = days;
        this.repeat = repeat;
        this.isonpen = isonpen;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(timestamp);
        dest.writeString(time);
        dest.writeString(days);
        dest.writeString(repeat);
        dest.writeString(isonpen);
    }

    protected ClockInfo(Parcel in) {
        id = in.readString();
        title = in.readString();
        timestamp = in.readString();
        time = in.readString();
        days = in.readString();
        repeat = in.readString();
        isonpen = in.readString();
    }

    public static final Creator<ClockInfo> CREATOR = new Creator<ClockInfo>() {
        @Override
        public ClockInfo createFromParcel(Parcel in) {
            return new ClockInfo(in);
        }

        @Override
        public ClockInfo[] newArray(int size) {
            return new ClockInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public void setIsonpen(String isonpen) {
        this.isonpen = isonpen;
    }

    public String getIsonpen() {
        return isonpen;
    }

}
