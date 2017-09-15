package com.yongyida.robot.settings.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.activity.MusicActivity;
import com.yongyida.robot.settings.bean.ClockInfo;
import com.yongyida.robot.settings.utils.AlarmUtil;
import com.yongyida.robot.settings.view.AddClockDialog;
import com.yongyida.robot.settings.utils.DB;
import com.yongyida.robot.settings.utils.DateUtil;
import com.yongyida.robot.settings.utils.SharedPreferencesUtils;

import java.io.IOException;

/**
 * 家长设置页面
 */
public class ParentsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ParentsFragment.class.getSimpleName();

    private final int MAX_BRIGHTNESS = 255;
    private final int MIN_BRIGHTNESS = 1;

    private String[] num;
    private boolean mChildLock = false;
    private boolean mRest = false;
    private Context mContext;
    private ClockInfo mSleep;
    private ClockInfo mGetUp;

    private View mBaseView;
    private RelativeLayout mMianLayout, mPasswordLayout;
    private SeekBar mBrightnessBar;
    private TextView mMultiplier1, mMultiplier2;
    private TextView mNum0, mNum1, mNum2, mNum3, mNum4, mNum5, mNum6, mNum7, mNum8, mNum9;
    private EditText mEtResult;
    private TextView mSleepTv, mGetUpTv;
    private Switch mSwRest;
    private Switch mSwLock;
    private LinearLayout mRestSleepLayout;
    private RelativeLayout mProgManageLayout;

    private ContentObserver observer = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (mBrightnessBar != null) {
                mBrightnessBar.setProgress(getBrightness());
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mBaseView = inflater.inflate(R.layout.fragment_parents_setup, container, false);
        initData();
        initView();
        mContext.getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true, observer);
        return mBaseView;
    }

    private void initView() {
        mMianLayout = (RelativeLayout) mBaseView.findViewById(R.id.RLayout_main);
        mPasswordLayout = (RelativeLayout) mBaseView.findViewById(R.id.RLayout_password);
        mProgManageLayout = (RelativeLayout) mBaseView.findViewById(R.id.RLayout_prog_manage);
        mRestSleepLayout = (LinearLayout) mBaseView.findViewById(R.id.RLayout_rest_sleep);
        mBrightnessBar = (SeekBar) mBaseView.findViewById(R.id.brightnessBar);
        mMultiplier1 = (TextView) mBaseView.findViewById(R.id.tv_multiplier1);
        mMultiplier2 = (TextView) mBaseView.findViewById(R.id.tv_multiplier2);
        mNum0 = (TextView) mBaseView.findViewById(R.id.tv_0);
        mNum1 = (TextView) mBaseView.findViewById(R.id.tv_1);
        mNum2 = (TextView) mBaseView.findViewById(R.id.tv_2);
        mNum3 = (TextView) mBaseView.findViewById(R.id.tv_3);
        mNum4 = (TextView) mBaseView.findViewById(R.id.tv_4);
        mNum5 = (TextView) mBaseView.findViewById(R.id.tv_5);
        mNum6 = (TextView) mBaseView.findViewById(R.id.tv_6);
        mNum7 = (TextView) mBaseView.findViewById(R.id.tv_7);
        mNum8 = (TextView) mBaseView.findViewById(R.id.tv_8);
        mNum9 = (TextView) mBaseView.findViewById(R.id.tv_9);
        mNum0.setOnClickListener(this);
        mNum1.setOnClickListener(this);
        mNum2.setOnClickListener(this);
        mNum3.setOnClickListener(this);
        mNum4.setOnClickListener(this);
        mNum5.setOnClickListener(this);
        mNum6.setOnClickListener(this);
        mNum7.setOnClickListener(this);
        mNum8.setOnClickListener(this);
        mNum9.setOnClickListener(this);
        mEtResult = (EditText) mBaseView.findViewById(R.id.et_result);
        mSwLock = (Switch) mBaseView.findViewById(R.id.lock_on_off);
        mSwRest = (Switch) mBaseView.findViewById(R.id.rest_on_off);
        mSleepTv = (TextView) mBaseView.findViewById(R.id.tv_sleep_time);
        mGetUpTv = (TextView) mBaseView.findViewById(R.id.tv_get_up_time);

        if (mSleep != null && mGetUp != null) {
            mSleepTv.setText(mSleep.getTime().substring(0, 5));
            mGetUpTv.setText(mGetUp.getTime().substring(0, 5));
        }

        //加儿童锁了才去会显示锁界面
        if (!mChildLock) {
            mMianLayout.setVisibility(View.VISIBLE);
            mPasswordLayout.setVisibility(View.GONE);
        } else {
            mMianLayout.setVisibility(View.GONE);
            mPasswordLayout.setVisibility(View.VISIBLE);
            mMultiplier1.setText(num[0]);
            mMultiplier2.setText(num[1]);
        }

        mBrightnessBar.setMax(MAX_BRIGHTNESS);
        mBrightnessBar.setProgress(getBrightness());
        mBrightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int index, boolean b) {
                setBrightness(index);
                saveBrightness(index);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mEtResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    if (s.toString().equals(num[2].substring(0, 1))) {//第一个数就输错，字体为红色
                        mEtResult.setTextColor(Color.rgb(0, 255, 0));
                    } else {
                        mEtResult.setTextColor(Color.rgb(255, 0, 0));
                        startAnimation(mEtResult);
                    }
                } else if (s.length() == 2) {
                    if (s.toString().equals(num[2])) {//输入正确，进入家长控制界面
                        mEtResult.setTextColor(Color.rgb(0, 255, 0));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mMianLayout.setVisibility(View.VISIBLE);
                                mPasswordLayout.setVisibility(View.GONE);
                                mEtResult.setText("");
                            }
                        }, 500);
                    } else {//输入错误，输入框抖动并且清除输入结果
                        mEtResult.setTextColor(Color.rgb(255, 0, 0));
                        startAnimation(mEtResult);
                        new Handler().postDelayed(new Runnable() {//动画结束后清空结果输入框
                            @Override
                            public void run() {
                                mEtResult.setText("");
                            }
                        }, 500);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSwLock.setChecked(mChildLock);
        mSwLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    SharedPreferencesUtils.setParam(getActivity(), "childlock_switch", true);
                else
                    SharedPreferencesUtils.setParam(getActivity(), "childlock_switch", false);
            }
        });

        mSwRest.setChecked(mRest);
        mSwRest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferencesUtils.setParam(getActivity(), "resttime_switch", true);
                    mRest = true;
                } else {
                    SharedPreferencesUtils.setParam(getActivity(), "resttime_switch", false);
                    mRest = false;
                }
            }
        });
        mProgManageLayout.setOnClickListener(this);
        mRestSleepLayout.setOnClickListener(this);
    }

    private void initData() {
        mChildLock = (boolean) SharedPreferencesUtils.getParam(getActivity(), "childlock_switch", false);
        mRest = (boolean) SharedPreferencesUtils.getParam(getActivity(), "resttime_switch", false);
        if (!mRest) {//夜间休息模式是关的时候判断数据库里面有没有数据，没有的话则是第一次安装
            if (DB.getInstanc(getActivity()).query().size() == 0) {
                createCLock("起床", "06:00:00");
                createCLock("睡觉", "22:00:00");
                AlarmUtil.setAlram(mContext);
            }
        }
        mGetUp = DB.getInstanc(mContext).queryById("1");
        mSleep = DB.getInstanc(mContext).queryById("2");
        //加儿童锁了才去生成随机乘数
        if (mChildLock)
            num = randomNum();
    }

    /**
     * 获得当前亮度
     */
    private int getBrightness() {
        int brightness = 0;
        ContentResolver resolver = mContext.getContentResolver();

        try {
            brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return brightness;
    }

    /**
     * 设置当前亮度
     *
     * @param brightness
     */
    private void setBrightness(int brightness) {
        if (brightness < MIN_BRIGHTNESS) {
            brightness = MIN_BRIGHTNESS;
        } else if (brightness > MAX_BRIGHTNESS) {
            brightness = MAX_BRIGHTNESS;
        }
        String cmds = "echo " + brightness + " > /sys/class/leds/lcd-backlight/brightness";
        try {
            Runtime.getRuntime().exec(cmds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止自动亮度调节
     */
    public void stopAutoBrightness() {
        Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 保存亮度设置状态
     *
     * @param brightness
     */
    public void saveBrightness(int brightness) {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
        android.provider.Settings.System.putInt(resolver, "screen_brightness", brightness);
        resolver.notifyChange(uri, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RLayout_rest_sleep:
                if (mRest) {
                    AddClockDialog dialog = new AddClockDialog(getActivity(), mGetUp, mSleep);
                    dialog.setDialogCallback(new AddClockDialog.Dialogcallback() {
                        @Override
                        public void onConfirm() {
                            mGetUp = DB.getInstanc(mContext).queryById("1");
                            mSleep = DB.getInstanc(mContext).queryById("2");
                            if (mSleep != null && mGetUp != null) {
                                mSleepTv.setText(mSleep.getTime().substring(0, 5));
                                mGetUpTv.setText(mGetUp.getTime().substring(0, 5));
                            }
                            Toast.makeText(mContext, getText(R.string.clock_update_success), Toast.LENGTH_SHORT).show();
                            AlarmUtil.setAlram(mContext);
                        }
                    });
                    dialog.show();
                }
                break;
            case R.id.RLayout_prog_manage:
                break;
            case R.id.tv_0:
                mEtResult.append("0");
                break;
            case R.id.tv_1:
                mEtResult.append("1");
                break;
            case R.id.tv_2:
                mEtResult.append("2");
                break;
            case R.id.tv_3:
                mEtResult.append("3");
                break;
            case R.id.tv_4:
                mEtResult.append("4");
                break;
            case R.id.tv_5:
                mEtResult.append("5");
                break;
            case R.id.tv_6:
                mEtResult.append("6");
                break;
            case R.id.tv_7:
                mEtResult.append("7");
                break;
            case R.id.tv_8:
                mEtResult.append("8");
                break;
            case R.id.tv_9:
                mEtResult.append("9");
                break;
        }
    }

    /**
     * 随机生成乘数，被乘数计算得到乘积
     *
     * @return
     */
    private String[] randomNum() {
        boolean flag = true;
        int multiplier1, multiplier2, result;
        String[] num = new String[3];
        while (flag) {
            multiplier1 = (int) (Math.random() * 10);
            multiplier2 = (int) (Math.random() * 10);
            result = multiplier1 * multiplier2;
            if (result > 10) {
                num[0] = String.valueOf(multiplier1);
                num[1] = String.valueOf(multiplier2);
                num[2] = String.valueOf(result);
                flag = false;
            }
        }
        return num;
    }

    /**
     * 抖动动画
     *
     * @param view
     */
    private void startAnimation(View view) {
        TranslateAnimation animation = new TranslateAnimation(0, -5, 0, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(10);
        animation.setRepeatCount(5);
        animation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(animation);
    }

    private void createCLock(String title, String time) {
        String days = "1234567";
        ClockInfo clockInfo = new ClockInfo();
        clockInfo.setTitle(title);
        clockInfo.setTime(time);
        clockInfo.setDays(days);
        clockInfo.setTimestamp(DateUtil.getTimestampForRepeat(time, days) + "");
        clockInfo.setRepeat("1");
        clockInfo.setIsonpen("1");
        DB.getInstanc(getActivity()).insert(clockInfo);
    }

}
