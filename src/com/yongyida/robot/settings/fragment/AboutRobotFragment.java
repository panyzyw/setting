package com.yongyida.robot.settings.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.utils.ProviderUtils;
import com.yongyida.robot.settings.utils.Utils;
import com.yongyida.robot.settings.view.KCornerDialog;

import android.content.ComponentName;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * /**
 * 关于机器人界面
 * 1. 获取机器人名字
 * ProviderUtils.getInstance().getRobotName(String name);
 * 2. 设置机器人名字
 * ProviderUtils.getInstance().setRobotName(String name);
 * 3. 获取版本号
 * Utils.getInstance().getSystemProperties("robot.os_version", "YOS2.128.001");
 */
public class AboutRobotFragment extends Fragment implements View.OnClickListener {
    private final String OS_VERSION_KEY = "robot.os_version";
    private final String SET_BACKUP_RESET = "android.settings.BACKUP_AND_RESET_SETTINGS";
    Context mContext;
    View mBaseView;
    RelativeLayout rlSystemUpdate;
    RelativeLayout rlName;
    private TextView mRobotName;
    private TextView mRobotModel;
    private TextView mRobotSerialNum;
    private TextView mRobotVersion;
    private TextView mRobotStorage;

    private KCornerDialog mEditNameialog;//修改名称对话框

    private String mNameStr;
    private String mModelStr;
    private String mSerialNumStr;
    private String mVersionStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mBaseView = inflater.inflate(R.layout.fragment_about_setup, container, false);
        initData();
        initView();
        return mBaseView;
    }

    void initView() {
        mRobotName = (TextView) mBaseView.findViewById(R.id.tv_name);
        mRobotModel = (TextView) mBaseView.findViewById(R.id.tv_model);
        mRobotSerialNum = (TextView) mBaseView.findViewById(R.id.tv_serial_num);
        mRobotVersion = (TextView) mBaseView.findViewById(R.id.tv_version);
        mRobotStorage = (TextView) mBaseView.findViewById(R.id.tv_storage);
        mRobotName.setText(mNameStr);
        mRobotModel.setText(mModelStr);
        mRobotSerialNum.append(mSerialNumStr);
        mRobotVersion.setText(mVersionStr);
        mRobotStorage.setText(getAvailableInternalMemorySize() + "/" + getTotalInternalMemorySize());

        rlSystemUpdate = (RelativeLayout) mBaseView.findViewById(R.id.rl_system_update);
        rlSystemUpdate.setOnClickListener(this);

        rlName = (RelativeLayout) mBaseView.findViewById(R.id.RLayout_name);
        rlName.setOnClickListener(this);
    }

    void initData() {
        mNameStr = ProviderUtils.getInstance().getRobotName(getResources().getString(R.string.robot_name));
        mModelStr = android.os.Build.MODEL;
        mSerialNumStr = getAndroidId(mContext);
        mVersionStr = Utils.getInstance().getSystemProperties(OS_VERSION_KEY, "Yos2.22.002.0128");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 调到系统升级app
            case R.id.rl_system_update:
                final Activity act = getActivity();
                if (isApkExist(act, "com.adups.fota")) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName cn = new ComponentName("com.adups.fota", "com.adups.fota.GoogleOtaClient");
                    intent.setComponent(cn);
                    startActivity(intent);
                }
                break;
            case R.id.RLayout_name:
                showEditNameDialog();//显示修改名称对话框
                break;
        }
    }

    private boolean isApkExist(Context ctx, String packageName) {
        PackageManager pm = ctx.getPackageManager();
        PackageInfo packageInfo = null;
        String versionName = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        if (versionName != null) {
            String[] names = versionName.split("\\.");
            if (names.length >= 4 && "9".equals(names[3])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    private String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long size = availableBlocks * blockSize / 1024 / 1024;
        return String.valueOf((size / 1024)) + "."
                + String.valueOf((size % 1024)).substring(0, 2)
                + "G";
    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    private String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long size = totalBlocks * blockSize / 1024 / 1024;
        return String.valueOf((size / 1024)) + "."
                + String.valueOf((size % 1024)).substring(0, 2)
                + "G";
    }

    /**
     * 获取id
     *
     * @param context
     * @return
     */
    private static String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * 发送机器人名称改变广播，通知launcher更新名称
     */
    private void changeNameBroadcast() {
        Intent intent = new Intent();
        intent.setAction("yydrobot.Name_ACTION_CHANGE");
        mContext.sendBroadcast(intent);
    }

    /**
     * 显示修改名称对话框
     */
    public void showEditNameDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog_edit, (ViewGroup) getActivity().findViewById(R.id.alert_root_RLayout));
        ImageView ivClose = (ImageView) dialogLayout.findViewById(R.id.iv_dialog_close);
        final TextView tvTitle = (TextView) dialogLayout.findViewById(R.id.tv_title);
        final EditText etContent = (EditText) dialogLayout.findViewById(R.id.et_content);
        etContent.setInputType(InputType.TYPE_CLASS_TEXT);
        Button btnConfirm = (Button) dialogLayout.findViewById(R.id.btn_confirm);

        tvTitle.setText(getText(R.string.dialog_edit_name));
        etContent.setText(mNameStr);
        etContent.setSelection(mNameStr.length());

        mEditNameialog = new KCornerDialog(getActivity(), 0, 0, dialogLayout, R.style.KCornerDialog);
        mEditNameialog.show();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditNameialog.dismiss();
            }
        });
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 12)
                {
                    Toast.makeText(mContext, getString(R.string.dialog_toast_nickname_length), Toast.LENGTH_SHORT).show();
                    String str = charSequence.toString();
                    //截取新字符串
                    String newStr = str.substring(0,12);
                    etContent.setText(newStr);
                    etContent.setSelection(newStr.length());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = etContent.getText().toString().trim();
                ProviderUtils.getInstance().setRobotName(newName);
                mRobotName.setText(newName);
				mNameStr=newName;
                mEditNameialog.dismiss();
                changeNameBroadcast();
            }
        });
    }
}
