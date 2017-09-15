package com.yongyida.robot.settings.bean;

/**
 * Created by tianchunming on 2017/8/9.
 */

public class Speaker {

    private String mTitle;//命令类型
    private String mContent;//命令词

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getContent() {
        return mContent;
    }
}
