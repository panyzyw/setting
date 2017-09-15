package com.yongyida.robot.settings.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.bean.ClockInfo;
import com.yongyida.robot.settings.utils.DB;
import com.yongyida.robot.settings.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2016/12/12 0012.
 */

public class AddClockDialog {
    private Dialogcallback mDialogcallback;

    private ClockInfo mSleep;
    private ClockInfo mGetUp;
    private int mGetUpTotalProgress = 0;
    private int mSleepTotalProgress = 0;
    private int mLastGetUpTotalProgress = 0;
    private int mLastSleepTotalProgress = 0;
    private String mSleepTime;
    private String mGetUpTime;

    private Context mContext;
    private Dialog mDialog;
    private RadioGroup mClockRg;
    private RelativeLayout mSleepRLayout;
    private RelativeLayout mGetUpRLayout;
    private TextView mSleepTimeTv;
    private TextView mGetUpTimeTv;
    private CircleSeekBar mSleepCs;
    private CircleSeekBar mGetUpCs;
    private Button mConfirmButton;

    /**
     * init the dialog
     *
     * @return
     */
    public AddClockDialog(Context context, ClockInfo getUpClock, ClockInfo sleepClock) {
        this.mContext = context;
        mGetUp = getUpClock;
        mSleep = sleepClock;

        mGetUpTime = getUpClock.getTime();
        mSleepTime = sleepClock.getTime();

        Calendar getUpTime = Calendar.getInstance();
        Calendar sleepTime = Calendar.getInstance();
        getUpTime.setTimeInMillis(DateUtil.datetimeTotimestamp("2000-01-01 " + mGetUp.getTime()));
        sleepTime.setTimeInMillis(DateUtil.datetimeTotimestamp("2000-01-01 " + mSleep.getTime()));

        mDialog = new Dialog(mContext, R.style.dialog_add_clock);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_add_clock);
        WindowManager.LayoutParams lay = mDialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        View view = ((Activity) context).getWindow().getDecorView();//decorView是window中的最顶层view，可以从window中获取到decorView
        view.getWindowVisibleDisplayFrame(rect);
        lay.height = dm.heightPixels - rect.top;
        lay.width = dm.widthPixels;

        mClockRg = (RadioGroup) mDialog.findViewById(R.id.rg_clock_select);
        mSleepRLayout = (RelativeLayout) mDialog.findViewById(R.id.RLayout_select_sleep);
        mGetUpRLayout = (RelativeLayout) mDialog.findViewById(R.id.RLayout_select_get_up);
        mSleepTimeTv = (TextView) mDialog.findViewById(R.id.tv_select_sleep);
        mGetUpTimeTv = (TextView) mDialog.findViewById(R.id.tv_select_get_up);
        mSleepCs = (CircleSeekBar) mDialog.findViewById(R.id.cs_select_sleep);
        mGetUpCs = (CircleSeekBar) mDialog.findViewById(R.id.cs_select_get_up);
        mConfirmButton = (Button) mDialog.findViewById(R.id.bt_confirm);

        setSeekBarProgress(getUpTime, mGetUpCs, mGetUpTimeTv);
        setSeekBarProgress(sleepTime, mSleepCs, mSleepTimeTv);

        mClockRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_sleep) {
                    mSleepRLayout.setVisibility(View.VISIBLE);
                    mGetUpRLayout.setVisibility(View.INVISIBLE);
                } else if (checkedId == R.id.rb_get_up) {
                    mSleepRLayout.setVisibility(View.INVISIBLE);
                    mGetUpRLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        mSleepCs.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar seekbar, int curNum, int curValue) {
                mLastSleepTotalProgress = mSleepTotalProgress;
                mSleepTotalProgress = curNum * 144 + curValue;
                //SeekBar滑动的时候因为在一定的角度返回的progress是相同，避免相同的progress重复计算时间，加条件判断
                if (mSleepTotalProgress != mLastSleepTotalProgress) {
                    int hourOfDay = (mSleepTotalProgress * 5) / 60;
                    int minute = (mSleepTotalProgress * 5) % 60;
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(1970, 1, 1, hourOfDay, minute, 0);
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    mSleepTime = format.format(calendar.getTime());
                    mSleepTimeTv.setText(mSleepTime.substring(0, 5));
                }
            }
        });

        mGetUpCs.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar seekbar, int curNum, int curValue) {
                mLastGetUpTotalProgress = mGetUpTotalProgress;
                mGetUpTotalProgress = curNum * 144 + curValue;
                if (mGetUpTotalProgress != mLastGetUpTotalProgress) {
                    int hourOfDay = (mGetUpTotalProgress * 5) / 60;
                    int minute = (mGetUpTotalProgress * 5) % 60;
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(1970, 1, 1, hourOfDay, minute, 0);
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    mGetUpTime = format.format(calendar.getTime());
                    mGetUpTimeTv.setText(mGetUpTime.substring(0, 5));
                }
            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updtaeClock("1", "起床", mGetUpTime);
                updtaeClock("2", "睡觉", mSleepTime);
                if (mDialogcallback != null) {
                    mDialogcallback.onConfirm();
                }
                dismiss();
            }
        });
    }

    public interface Dialogcallback {
        void onConfirm();
    }

    public void setDialogCallback(Dialogcallback dialogcallback) {
        this.mDialogcallback = dialogcallback;
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    private void setSeekBarProgress(Calendar calendar, CircleSeekBar circleSeekBar, TextView textView) {
        String time;
        int progress = 0;
        int hour = calendar.get(calendar.HOUR_OF_DAY);
        int min = calendar.get(calendar.MINUTE);
        if (hour >= 12) {
            progress = ((hour - 12) * 60 + min) / 5;
            circleSeekBar.setCurProcess(progress, 1);
        } else {
            progress = ((hour * 60) + min) / 5;
            circleSeekBar.setCurProcess(progress, 0);
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        time = format.format(calendar.getTime());
        textView.setText(time.substring(0, 5));
    }

    private void updtaeClock(String id, String title, String time) {
        String days = "1234567";
        ClockInfo clockInfo = new ClockInfo();
        clockInfo.setId(id);
        clockInfo.setTitle(title);
        clockInfo.setTime(time);
        clockInfo.setDays(days);
        clockInfo.setTimestamp(DateUtil.getTimestampForRepeat(time, days) + "");
        clockInfo.setRepeat("1");
        clockInfo.setIsonpen("1");
        DB.getInstanc(mContext).update(clockInfo);
    }

}
