package com.yongyida.robot.settings.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yongyida.robot.settings.activity.QrCodeBindActivity;
import com.yongyida.robot.settings.activity.QrCodeDownloadActivity;
import com.yongyida.robot.settings.bean.IntentConstant;
import com.yongyida.robot.settings.bean.QrBean;
import com.yongyida.robot.settings.utils.BeanUtils;

public class QrReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {

            if (action.equals(IntentConstant.INTENT_YYDCHAT)) {
                String result = intent.getStringExtra("result");
                QrBean qrBean = BeanUtils.parseQrJson(result);
                if (qrBean.operation.equals("qrcode_download")) {
                    // 打开下载客户端的二维码界面
                    Intent intentDownload = new Intent(context, QrCodeDownloadActivity.class);
                    intentDownload.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentDownload);
                } else if (qrBean.operation.equals("qrcode_bind")) {
                    // 打开绑定机器人的二维码界面
                    Intent intentBind = new Intent(context, QrCodeBindActivity.class);
                    intentBind.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentBind);
                }

            }

        }
    }

}
