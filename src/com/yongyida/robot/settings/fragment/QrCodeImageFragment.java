package com.yongyida.robot.settings.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.utils.log.LogTool;
import com.yongyida.robot.settings.activity.QrCodeBindActivity;
import com.yongyida.robot.settings.activity.QrCodeDownloadActivity;
import com.yongyida.robot.settings.utils.QRCodeUtil;
import com.yongyida.robot.settings.utils.VersionControl;
import com.yongyida.robot.settings.utils.Utils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 机器人二维码显示界面，有两个二维码
 */
public class QrCodeImageFragment extends Fragment {

    private final static String TAG = QrCodeImageFragment.class.getSimpleName();
    private String mDevideId;
    private TextView codeID;
    private TextView codeSID;
    private String sdKey;
    private String str;
    private ImageView qrIDCode;
    private ImageView qrAppCode;
    private TextView downloadTip;
    private TextView bindTip;

    private Context mContext;
    private Activity mActivity;
    private View mBaseView;

    /**
     * 生成二维码图片的线程池
     */
    private ExecutorService mCreateQrCodeThreadPool = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mActivity = getActivity();
        mBaseView = inflater.inflate(R.layout.fragment_qrcode_image, container, false);
        init();
        return mBaseView;
    }

    private void init() {
//        str = Utils.getInstance().getSystemProperties("gsm.serial", "Y50B-566");
        mDevideId = Utils.getInstance().getSerialNumber(mContext);
        try {

            // 机器人ID
            codeID = (TextView) mBaseView.findViewById(R.id.create_qr_id);
            codeSID = (TextView) mBaseView.findViewById(R.id.create_qr_sid);

            // 显示机器人下载客户端的二维码图片
            qrAppCode = (ImageView) mBaseView.findViewById(R.id.download_qr_iv);
            qrAppCode.setBackgroundResource(VersionControl.mDownloadBg);

            // 显示机器人ID二维码图片
            qrIDCode = (ImageView) mBaseView.findViewById(R.id.create_qr_iv);
            qrIDCode.setBackgroundResource(VersionControl.mBindBg);

            downloadTip = (TextView) mBaseView.findViewById(R.id.scan_tv);
            downloadTip.setText(getString(VersionControl.mDownloadTip));

            // 绑定二维码下方的提示文本
            bindTip = (TextView) mBaseView.findViewById(R.id.create_qr_cdKeyword);
            bindTip.setText(getString(VersionControl.mBindQrcodeTV));

            qrAppCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, QrCodeDownloadActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });

            qrIDCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, QrCodeBindActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });

            // 二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在线程池中
            getThreadPool().execute(runnable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                final String filePath = getFileRoot(mContext) + File.separator
                        + "qr_" + System.currentTimeMillis() + ".jpg";
                final String filePathApp = getFileRoot(mContext) + File.separator
                        + "qr_app" + System.currentTimeMillis() + ".jpg";

                boolean successApp = QRCodeUtil.createQRImage(VersionControl.mDownloadUrl, 400, 400,
                        BitmapFactory.decodeResource(getResources(), VersionControl.mDownloadBg),
                        filePathApp);
                Log.i(TAG, "successApp:" + successApp);
                if (successApp) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qrAppCode.setImageBitmap(BitmapFactory.decodeFile(filePathApp));
                            File fileApp = new File(filePathApp);
                            fileApp.delete();
                        }
                    });
                }


/*                if (str == null || str.equals("")) {
                    return;
                }
                sdKey = str.substring(0, 32);*/
                if (mDevideId == null || mDevideId.equals("")) {
                    return;
                }
                sdKey = mDevideId;
                if (TextUtils.isEmpty(sdKey)) {
                    return;
                }
                boolean success = QRCodeUtil.createQRImage(sdKey, 400, 400,
                        BitmapFactory.decodeResource(getResources(), VersionControl.mDownloadBg),
                        filePath);
                if (success) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String key1 = sdKey.substring(0, sdKey.indexOf("-"));
                                String key2 = sdKey.substring(sdKey.indexOf("-") + 1, sdKey.length());
                                Resources res = getResources();
                                String text = res.getString(R.string.cdkeyid) + key1.trim();
                                String text2 = res.getString(R.string.cdkeysid) + key2.trim();
                                codeID.setText(text);
                                codeSID.setText(text2);
                                qrIDCode.setImageBitmap(BitmapFactory.decodeFile(filePath));
                                File file = new File(filePath);
                                file.delete();
                            } catch (Exception e) {
                                LogTool.showLog(TAG, "runnable", "Error: " + e.getMessage());
                            }
                        }
                    });
                }
            } catch (Exception e) {
                LogTool.showLog(TAG, "runnable", "Error: " + e.getMessage());
            }
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

    /**
     * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
     *
     * @return
     */
    public ExecutorService getThreadPool() {
        if (mCreateQrCodeThreadPool == null) {
            synchronized (ExecutorService.class) {
                if (mCreateQrCodeThreadPool == null) {
                    //为了生成图片更加的流畅，我们用了2个线程来生成图片
                    mCreateQrCodeThreadPool = Executors.newFixedThreadPool(2);
                }
            }
        }
        return mCreateQrCodeThreadPool;
    }
}