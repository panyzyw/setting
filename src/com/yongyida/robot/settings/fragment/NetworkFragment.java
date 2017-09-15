package com.yongyida.robot.settings.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.adapter.WifiListAdapter;
import com.yongyida.robot.settings.view.KCornerDialog;
import com.yongyida.robot.settings.utils.Utils;

import java.util.List;

public class NetworkFragment extends Fragment implements View.OnClickListener {

    private KCornerDialog mWifiDialog;//连接wifi对话框

    private View mBaseView;
    private Button mBtnRefresh;
    private ListView mLvWifi;

    private WifiManager mWifiManager;
    private List<ScanResult> mWifiList;
    private String mCurWifiSSID;
    private WifiListAdapter mWifiListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_network_setup, container, false);
        initData();
        initView();
        registerNetWorkChangedReceiver();
        return mBaseView;
    }

    private void initView() {
        mBtnRefresh = (Button) mBaseView.findViewById(R.id.btn_refresh);
        mLvWifi = (ListView) mBaseView.findViewById(R.id.lv_wifi);
        mBtnRefresh.setOnClickListener(this);
        mLvWifi.setAdapter(mWifiListAdapter);
        mLvWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScanResult scanResult = mWifiList.get(position);
                showDeleteDialog(scanResult);
            }
        });
    }

    private void initData() {
        mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiList = mWifiManager.getScanResults();
        mCurWifiSSID = mWifiManager.getConnectionInfo().getSSID().replaceAll("\"", "");
        mWifiListAdapter = new WifiListAdapter(getActivity(), mWifiList, mCurWifiSSID);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_refresh:
                Toast.makeText(getActivity(), getText(R.string.network_refreshing), Toast.LENGTH_SHORT).show();
                refreshWifi();
                break;
        }
    }

    /**
     * 显示删除闹钟对话框
     */
    public void showDeleteDialog(final ScanResult scanResult) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog_edit, (ViewGroup) getActivity().findViewById(R.id.alert_root_RLayout));
        ImageView ivClose = (ImageView) dialogLayout.findViewById(R.id.iv_dialog_close);
        final TextView tvTitle = (TextView) dialogLayout.findViewById(R.id.tv_title);
        final EditText etContent = (EditText) dialogLayout.findViewById(R.id.et_content);
        Button btnConfirm = (Button) dialogLayout.findViewById(R.id.btn_confirm);

        tvTitle.setText(scanResult.SSID);

        mWifiDialog = new KCornerDialog(getActivity(), 0, 0, dialogLayout, R.style.KCornerDialog);
        mWifiDialog.show();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWifiDialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"".equals(Utils.getInstance().getEncryptionType(scanResult))) {//判断wifi有没有加密
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.getInstance().setWifiParamsPassword(mWifiManager, tvTitle.getText().toString(), etContent.getText().toString());
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.getInstance().setWifiParamsPassword(mWifiManager, tvTitle.getText().toString(), etContent.getText().toString());
                        }
                    }).start();
                    Utils.getInstance().setWifiParamsNoPassword(mWifiManager, scanResult.SSID);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mWifiDialog != null && mWifiDialog.isShowing()) {
                            Toast.makeText(getActivity(), getText(R.string.security_activity_confirm_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 5000);
            }
        });
    }

    private void refreshWifi() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWifiList = mWifiManager.getScanResults();
                mCurWifiSSID = mWifiManager.getConnectionInfo().getSSID().replaceAll("\"", "");
                mWifiListAdapter.updateWifiList(mWifiList, mCurWifiSSID);
            }
        }, 2000);
    }

    private void registerNetWorkChangedReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(new NetworkConnectChangedReceiver(), filter);
    }

    class NetworkConnectChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    if (state == NetworkInfo.State.CONNECTED) {
                        if (mWifiDialog != null) {
                            refreshWifi();
                            mWifiDialog.dismiss();
                        }
                    }
                }
            }
        }
    }
}
