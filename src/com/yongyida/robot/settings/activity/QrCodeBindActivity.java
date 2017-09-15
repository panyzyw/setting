package com.yongyida.robot.settings.activity;

import java.io.File;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.utils.QRCodeUtil;
import com.yongyida.robot.settings.utils.VoiceUtils;
import com.yongyida.robot.settings.utils.VersionControl;
import com.yongyida.robot.settings.utils.Utils;

/**
 * 语音打开绑定二维码界面
 */
public class QrCodeBindActivity extends Activity {

    ImageView mIvBind;

    private String sdKey;
    private String str;

    private BroadcastReceiver myReceiver;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        init();
    }

    private void init() {
        View view = View.inflate(this, R.layout.activity_qr_code_bind, null);
        ImageView tv_bind = (ImageView) view.findViewById(R.id.iv_bind);
        tv_bind.setBackgroundResource(VersionControl.mBindBg);
        setContentView(view);
        initStopRecver();
        initView();
        initData();
    }

    private void initStopRecver() {
        myReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                QrCodeBindActivity.this.finish();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.yydrobot.STOP");
        registerReceiver(myReceiver, filter);
    }

    private void initView() {
        mIvBind = (ImageView) findViewById(R.id.iv_bind);
    }

    private void initData() {
        str = Utils.getInstance().getSystemProperties("gsm.serial", "Y50B-566");

        //生产下载app的二维码
        //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            final String filePath = getFileRoot(QrCodeBindActivity.this) + File.separator
                    + "qr_" + System.currentTimeMillis() + ".jpg";

            if (str == null || str.equals("")) {
                VoiceUtils.startVoice(QrCodeBindActivity.this, QrCodeBindActivity.this.getString(R.string.id_err));
                return;
            }
            sdKey = str.substring(0, 32);
            if (TextUtils.isEmpty(sdKey)) {
                VoiceUtils.startVoice(QrCodeBindActivity.this, QrCodeBindActivity.this.getString(R.string.id_err));
                return;
            }
            boolean success = QRCodeUtil.createQRImage(sdKey, 800, 800,
                    BitmapFactory.decodeResource(getResources(), VersionControl.mBindBg),
                    filePath);
            if (success) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvBind.setImageBitmap(BitmapFactory.decodeFile(filePath));
                        File file = new File(filePath);
                        file.delete();
                    }
                });
            }

            VoiceUtils.startVoice(QrCodeBindActivity.this, QrCodeBindActivity.this.getString(R.string.bind_qrcode));
        }

    };

    //文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
        super.onDestroy();
    }

}
