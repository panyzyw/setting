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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.utils.QRCodeUtil;
import com.yongyida.robot.settings.utils.VoiceUtils;
import com.yongyida.robot.settings.utils.VersionControl;

/**
 * 语音打开下载客户端二维码界面
 */
public class QrCodeDownloadActivity extends Activity {

    ImageView mIvDownload;

    private BroadcastReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_qr_code_download, null);
        ImageView tv_download = (ImageView) view.findViewById(R.id.iv_download);
        tv_download.setBackgroundResource(VersionControl.mDownloadBg);
        setContentView(view);

        initStopRecver();
        initView();
        initData();
    }

    private void initStopRecver() {

        myReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                QrCodeDownloadActivity.this.finish();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.yydrobot.STOP");
        registerReceiver(myReceiver, filter);
    }

    void initView() {
        mIvDownload = (ImageView) findViewById(R.id.iv_download);
    }

    void initData() {

        //生产下载app的二维码
        //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final String filePathApp = getFileRoot(QrCodeDownloadActivity.this) + File.separator
                    + "qr_app" + System.currentTimeMillis() + ".jpg";

            boolean successApp = QRCodeUtil.createQRImage(VersionControl.mDownloadUrl, 800, 800,
                    BitmapFactory.decodeResource(getResources(), VersionControl.mDownloadBg),
                    filePathApp);
            Log.i("Main", "successApp:" + successApp);
            if (successApp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvDownload.setImageBitmap(BitmapFactory.decodeFile(filePathApp));
                        File fileApp = new File(filePathApp);
                        fileApp.delete();
                    }
                });
            }

            VoiceUtils.startVoice(QrCodeDownloadActivity.this, QrCodeDownloadActivity.this.getString(R.string.download_qrcode));

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
