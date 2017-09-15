package com.yongyida.robot.settings.app;

import android.app.Application;
import android.content.Context;

import com.yongyida.robot.settings.utils.log.LogTool;
import com.yongyida.robot.settings.utils.log.LogcatHelper;

/**
 * @author Bright. Create on 2016/11/8 0008.
 */
public class SettingsApp extends Application {
    private static Context mAppContext;
    public static final String APP_CHANNEL = "YYDSettings";

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;

        // 启动Log工具
        LogcatHelper.getInstance(mAppContext).start();
        LogTool.showLog("SettingsApp", "onCreate", "App Start...");

    }


    public static Context getAppContext() {
        return mAppContext;
    }
}

