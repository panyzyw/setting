package com.yongyida.robot.settings.fragment;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.utils.SharedPreferencesUtils;
import com.yongyida.robot.settings.view.KCornerDialog;

/**
 * 恢复出厂设置命令
 * mContext.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
 * 权限 android:sharedUserId="android.uid.system" 和
 * <uses-permission android:name="android.permission.MASTER_CLEAR" />
 * 或者用"android.settings.BACKUP_AND_RESET_SETTINGS"启动系统设置里的恢复出厂设置
 */
public class AdvancedSettingFragment extends Fragment implements View.OnClickListener {
    Context mContext;

    View mBaseView;
    private RelativeLayout mRLayoutFileManage;
    private RelativeLayout mRLayoutFacSet;
    private Switch mElectricitySw;
    private Switch mLocationSw;
    private Switch mSmartFallSw;
    private Switch mBreathingSw;
    private Switch mSoundSw;
    private Switch mAwakenSw;
    private RadioGroup mAwakenModelRg;
    private RadioButton mNearRbt;
    private RadioButton mFarRbt;
    private KCornerDialog mFacSetialog;//修改恢复出厂对话框

    private final String LED_CHEST = "chest";
    private final String LED_EAR = "ear";
    private final String POWER_ON = "on";
    private final String POWER_OFF = "off";
    private boolean electricity_iscleck;
    private boolean smartfall_iscleck;
    private boolean breathing_iscleck;
    private boolean sound_iscleck;
    private boolean location_iscleck;
    private boolean awaken_iscleck;
    private boolean awaken_model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mBaseView = inflater.inflate(R.layout.fragment_advanced_setting, container, false);
        initData();
        initView();
        return mBaseView;
    }

    private void initView() {
        mRLayoutFileManage = (RelativeLayout) mBaseView.findViewById(R.id.RLayout_file_manage);
        mRLayoutFacSet = (RelativeLayout) mBaseView.findViewById(R.id.RLayout_fac_set);
        mElectricitySw = (Switch) mBaseView.findViewById(R.id.electricity_on_off);
        mSmartFallSw = (Switch) mBaseView.findViewById(R.id.fall_on_off);
        mBreathingSw = (Switch) mBaseView.findViewById(R.id.breathing_on_off);
        mSoundSw = (Switch) mBaseView.findViewById(R.id.back_sound_on_off);
        mLocationSw = (Switch) mBaseView.findViewById(R.id.location_on_off);
        mAwakenSw = (Switch) mBaseView.findViewById(R.id.awaken_on_off);
        mAwakenModelRg = (RadioGroup) mBaseView.findViewById(R.id.rg_awaken_model);
        mFarRbt = (RadioButton) mBaseView.findViewById(R.id.rbt_far);
        mNearRbt = (RadioButton) mBaseView.findViewById(R.id.rbt_near);

        mElectricitySw.setChecked(electricity_iscleck);
        mElectricitySw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean iscleck) {
                SharedPreferencesUtils.setParam(mContext, "electricity_switch", iscleck);
                changeElectricityBroadcast();
            }
        });

        mSmartFallSw.setChecked(smartfall_iscleck);
        mSmartFallSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean iscleck) {
                SharedPreferencesUtils.setParam(mContext, "smartfall_switch", iscleck);
            }
        });

        mBreathingSw.setChecked(breathing_iscleck);
        mBreathingSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean iscleck) {
                SharedPreferencesUtils.setParam(mContext, "breathled_switch", iscleck);
                com.yyd.breathled.BreathLed breathLed = new com.yyd.breathled.BreathLed();
                breathLed.openDev();
                Intent ledIntent = new Intent(
                        "com.yydrobot.ACTION_BREATH_LED");
                try {
                    if (iscleck) {
                        int retChest = breathLed.setOnoff(LED_CHEST,
                                POWER_ON);
                        int retEar = breathLed.setOnoff(LED_EAR,
                                POWER_ON);
                        if (retChest < 0 || retEar < 0) {
                            Toast.makeText(mContext, R.string.leds_open_failed, Toast.LENGTH_SHORT).show();
                            mBreathingSw.setChecked(false);
                            return;
                        }
                        ledIntent.putExtra("isChestLedOn", true);
                        ledIntent.putExtra("isEarLedOn", true);
                    } else {
                        int retChest = breathLed.setOnoff(LED_CHEST,
                                POWER_OFF);
                        int retEar = breathLed.setOnoff(LED_EAR,
                                POWER_OFF);
                        if (retChest < 0 || retEar < 0) {
                            Toast.makeText(mContext, R.string.leds_close_failed, Toast.LENGTH_SHORT).show();
                            mBreathingSw.setChecked(true);
                            return;
                        }
                        ledIntent.putExtra("isChestLedOn", false);
                        ledIntent.putExtra("isEarLedOn", false);
                    }
                    ledIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    mContext.sendBroadcast(ledIntent);
                } finally {
                    breathLed.closeDev();
                }
            }
        });

        mSoundSw.setChecked(sound_iscleck);
        mSoundSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean iscleck) {
                SharedPreferencesUtils.setParam(mContext, "backsound_switch", iscleck);
            }
        });

        mLocationSw.setChecked(location_iscleck);
        mLocationSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean iscleck) {
                SharedPreferencesUtils.setParam(mContext, "location_switch", iscleck);
            }
        });

        mAwakenSw.setChecked(awaken_iscleck);
        mAwakenSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean iscleck) {
                SharedPreferencesUtils.setParam(mContext, "awaken_switch", iscleck);
            }
        });

        if (awaken_model) {
            mFarRbt.setChecked(true);
        } else {
            mNearRbt.setChecked(true);
        }
        mAwakenModelRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (R.id.rbt_near == i)
                    SharedPreferencesUtils.setParam(mContext, "awaken_model", false);//近场模式
                else
                    SharedPreferencesUtils.setParam(mContext, "awaken_model", true);//远场模式
            }
        });

        mRLayoutFileManage.setOnClickListener(this);
        mRLayoutFacSet.setOnClickListener(this);
    }

    private void initData() {
        electricity_iscleck = (boolean) SharedPreferencesUtils.getParam(getActivity(), "electricity_switch", true);
        smartfall_iscleck = (boolean) SharedPreferencesUtils.getParam(getActivity(), "smartfall_switch", true);
        breathing_iscleck = (boolean) SharedPreferencesUtils.getParam(getActivity(), "breathled_switch", true);
        sound_iscleck = (boolean) SharedPreferencesUtils.getParam(getActivity(), "backsound_switch", true);
        location_iscleck = (boolean) SharedPreferencesUtils.getParam(getActivity(), "location_switch", true);
        awaken_iscleck = (boolean) SharedPreferencesUtils.getParam(getActivity(), "awaken_switch", true);
        awaken_model = (boolean) SharedPreferencesUtils.getParam(getActivity(), "awaken_model", true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RLayout_file_manage:
                openNewAddFaceActivity();
                break;
            case R.id.RLayout_fac_set:
                showEditNameDialog();
                break;
        }
    }

    /**
     * 当显示电量百分比的开关状态改变时，发送广播
     */
    private void changeElectricityBroadcast() {
        Intent intent = new Intent();
        intent.setAction("yydrobot.ELECTRICITY_ACTION_CHANGE");
        mContext.sendBroadcast(intent);
    }

    /**
     * 显示恢复出厂设置对话框
     */
    public void showEditNameDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog_warning, (ViewGroup) getActivity().findViewById(R.id.alert_root_RLayout));

        Button btnCancle = (Button) dialogLayout.findViewById(R.id.btn_cancle);
        Button btnConfirm = (Button) dialogLayout.findViewById(R.id.btn_confirm);

        mFacSetialog = new KCornerDialog(getActivity(), 0, 0, dialogLayout, R.style.KCornerDialog);
        mFacSetialog.show();
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFacSetialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, getString(R.string.dialog_toast_reboot), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mContext.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
                    }
                }, 2000);
                mFacSetialog.dismiss();
            }
        });
    }

    /**
     * 打开文件管理的界面
     */
    public void openNewAddFaceActivity() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName("com.yongyida.robot.resourcemanager", "com.yongyida.robot.resourcemanager.MainActivity");
        try {
            intent.setComponent(cn);
            startActivity(intent);
        } catch (Exception e) {

        }
    }
}
