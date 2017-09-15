package com.yongyida.robot.settings.bean;

/**
 * @author Bright. Create on 2016/11/7 0007.
 */

public class Constants {
    public static final boolean DEBUG = true;
    public static final String CONTENT_URI =
            "content://com.yongyida.robot.settingsprovider/robot_settings";

    public static final String INTENT_PERSON_ID = "intent_person_id";
    public static final String INTENT_MODIFY_NAME = "intent_modify_name";
    public static final String PORTRAIT_LOCATION = "/sdcard/face/portrait/";

    // 禁止下拉通知栏
    public static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00002000;
}
