package com.yongyida.robot.settings.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yongyida.robot.settings.activity.MusicActivity;
import com.yongyida.robot.settings.utils.AlarmUtil;
import com.yongyida.robot.settings.utils.SharedPreferencesUtils;


public class SettingReceiver extends BroadcastReceiver {

    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        boolean btnIsOn = (boolean) SharedPreferencesUtils.getParam(context, "resttime_switch", false);
        if (AlarmUtil.ACTION_ALARM.equals(action)) {   //提醒时间到广播
            if (btnIsOn) {//夜间休息模式的开关打开才会有提醒
                Intent dialog = new Intent(context, MusicActivity.class);
                dialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dialog.putExtra("clockInfo", intent.getParcelableExtra("clockInfo"));
                context.startActivity(dialog);
            }
        } else if (ACTION_BOOT.equals(action)) {
            AlarmUtil.setAlram(context);
        }
    }
}
