package com.yongyida.robot.settings.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.bean.ClockInfo;
import com.yongyida.robot.settings.service.MusicService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MusicActivity extends Activity {

    private ClockInfo mClockInfo;
    private Handler mHandler;

    private TextView mTvtip;
    private Button mBtnStop;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        getWindow().setBackgroundDrawable(new BitmapDrawable());
        mClockInfo = getIntent().getParcelableExtra("clockInfo");
        mHandler = new Handler();
        //启动播放音乐的service
        intent = new Intent(this, MusicService.class);
        startService(intent);

        mTvtip = (TextView) findViewById(R.id.tv_tip);
        if (mClockInfo != null)
            mTvtip.setText(mClockInfo.getTitle() + getText(R.string.dialog_time_up));

        mBtnStop = (Button) findViewById(R.id.bt_stop);
        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //停止播放音乐
                stopService(intent);
                mHandler.removeMessages(0);
                finish();
            }
        });
        //播放5分钟音乐后，停止播放
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopService(intent);
            }
        }, 5 * 60 * 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(intent);
    }
}
