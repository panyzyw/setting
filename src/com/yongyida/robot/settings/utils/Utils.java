package com.yongyida.robot.settings.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.yongyida.robot.settings.app.SettingsApp;
import com.yongyida.robot.settings.bean.Constants;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Bright. Create on 2016/11/8 0008.
 */

public class Utils {
    final String TAG = Utils.class.getSimpleName();
    private String idUri = "content://com.yongyida.robot.idprovider/id";
    private static Utils utils;
    private static Context mContext;
    private String nameUri = "content://com.yongyida.robot.nameprovider/name";

    private Utils() {
    }

    public static Utils getInstance() {
        if (utils == null) {
            utils = new Utils();
            mContext = SettingsApp.getAppContext();
        }
        return utils;
    }

    /**
     * 读取系统属性build.prop
     *
     * @param key      属性的key
     * @param defValue 属性默认值
     * @return 属性值
     */
    public String getSystemProperties(String key, String defValue) {
        Class<?> clazz;
        try {
            clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getDeclaredMethod("get", String.class);
            return (String) method.invoke(clazz.newInstance(), key);
        } catch (Exception e) {
            return defValue;
        }
    }

    /**
     * 禁止下拉通知栏
     *
     * @param activity
     */
    public void hideStatusBar(final Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(Constants.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    View decorView = activity.getWindow().getDecorView();
                    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);
                    decorView.setSystemUiVisibility(Constants.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                } else {
                    View decorView = activity.getWindow().getDecorView();
                    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);
                    decorView.setSystemUiVisibility(Constants.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });
    }

    public String getSerialNumber(Context context) {
        try {
            Uri uri = Uri.parse(idUri);
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String sid = cursor.getString(cursor.getColumnIndex("sid"));
                //Log.e("TAG",id+"-"+sid);
                cursor.close();
                return id + "-" + sid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查网络连接情况
     *
     * @return true表示已连接，false未连接
     */
    public boolean checkNetworkStatus(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = conMan.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            NetworkInfo.State state = activeNetworkInfo.getState();
            int type = activeNetworkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI
                    && state != null
                    && state == NetworkInfo.State.CONNECTED) {
                // 网络是WIFI类型，并且已连接
                return true;
            } else {
                // 断网了
                return false;
            }
        } else {
            // 断网了
            return false;
        }
    }

    /**
     * 获取WIFI加密类型
     *
     * @param scanResult
     * @return
     */
    public String getEncryptionType(ScanResult scanResult) {
        if (!TextUtils.isEmpty(scanResult.SSID)) {
            String capabilities = scanResult.capabilities;
            Log.i(TAG, "[" + scanResult.SSID + "]" + capabilities);
            if (!TextUtils.isEmpty(capabilities)) {
                if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                    return "wpa";
                } else if (capabilities.contains("WEP")
                        || capabilities.contains("wep")) {
                    return "wep";
                } else {
                    return "";
                }
            }
        }
        return scanResult.capabilities.toString();
    }

    /**
     * 连接一个WPA带密码的WIFI
     *
     * @param SSID
     * @param Password
     * @return
     */
    public boolean setWifiParamsPassword(WifiManager mWifiManager, String SSID, String Password) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"" + SSID + "\"";
        configuration.preSharedKey = "\"" + Password + "\"";
        configuration.hiddenSSID = true;
        configuration.status = WifiConfiguration.Status.ENABLED;
        configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        configuration.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        configuration.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        return mWifiManager.enableNetwork(mWifiManager.addNetwork(configuration), true);
    }

    /**
     * 连接一个WPA不带密码的WIFI
     *
     * @param ssid
     * @return
     */
    public boolean setWifiParamsNoPassword(WifiManager mWifiManager, String ssid) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"" + ssid + "\"";
        configuration.status = WifiConfiguration.Status.ENABLED;

        configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        configuration.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        configuration.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        return mWifiManager.enableNetwork(mWifiManager.addNetwork(configuration), true);
    }

    /**
     * 删除某个存在的wifi
     */
    private void removeWifi(WifiManager mWifiManager, String ssid) {
        List<WifiConfiguration> configurations = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration configuration : configurations) {
            if (configuration.SSID.equals("\"" + ssid + "\"")) {
                mWifiManager.removeNetwork(configuration.networkId);
            }
        }
    }

}
