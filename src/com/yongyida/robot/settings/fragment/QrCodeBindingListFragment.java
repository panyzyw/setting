package com.yongyida.robot.settings.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.utils.log.LogTool;
import com.yongyida.robot.settings.adapter.BindingUserListAdapter;
import com.yongyida.robot.settings.bean.IntentConstant;
import com.yongyida.robot.settings.bean.User;
import com.yongyida.robot.settings.utils.JsonUtils;
import com.yongyida.robot.settings.utils.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * 查看已经绑定的用户列表
 */
public class QrCodeBindingListFragment extends Fragment {
    private static final String TAG = QrCodeBindingListFragment.class.getSimpleName();
    private Context mContext;

    private View mBaseView;
    private SwipeRefreshLayout refreshLayout;
    private ListView bindingListView;

    private BindingUserListAdapter mAdapter;
    private List<User> mBindingList = new LinkedList<>();
    private UserReceiver mReceiver;
    private Intent mIntent;
    private TextView no_binding_user;
    private ProgressBar loading;

    /**
     * 获取用户列表更新
     */
    private class UserReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(IntentConstant.RESLUT)) {
                String json = intent.getExtras().getString("result");
                mBindingList.clear();
                mBindingList.addAll(JsonUtils.getUser(json));
                if (mBindingList.size() == 0) {
                    refreshDone();
                }
                LogTool.showLog(TAG, "UserReceiver", "User:" + mBindingList.toString());
                mAdapter = new BindingUserListAdapter(mContext, mBindingList);
                bindingListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                refreshDone();
            }
        }
    }

    private void refreshDone() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                loading.setVisibility(View.INVISIBLE);
                if (mBindingList.size() > 0) {
                    no_binding_user.setVisibility(View.INVISIBLE);
                } else {
                    no_binding_user.setVisibility(View.VISIBLE);
                }
            }
        }, 1000 * 2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mBaseView = inflater.inflate(R.layout.fragment_qrcode_binding_list, container, false);
        init();
        return mBaseView;
    }

    private void init() {
        mIntent = new Intent(IntentConstant.QUERY);
        mContext.sendBroadcast(mIntent);

        // 注册UserReceiver广播接收者
        mReceiver = new UserReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IntentConstant.RESLUT);
        intentFilter.addAction(IntentConstant.DRESLUT);
        mContext.registerReceiver(mReceiver, intentFilter);

        no_binding_user = (TextView) mBaseView.findViewById(R.id.no_binding_user);
        no_binding_user.setVisibility(View.INVISIBLE);
        refreshLayout = (SwipeRefreshLayout) mBaseView.findViewById(R.id.view_swipe_refresh);
        mAdapter = new BindingUserListAdapter(mContext, mBindingList);
        bindingListView = (ListView) mBaseView.findViewById(R.id.binding_user);
        loading = (ProgressBar) mBaseView.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        refreshLayout.setRefreshing(true);
        if (!Utils.getInstance().checkNetworkStatus(mContext)) {
            refreshDone();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshDone();
            }
        }, 1000 * 5);

        refreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright));
        bindingListView.setAdapter(mAdapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mContext.sendBroadcast(mIntent);
                if (!Utils.getInstance().checkNetworkStatus(mContext)) {
                    Toast.makeText(mContext, getString(R.string.no_network_connected),
                            Toast.LENGTH_SHORT).show();
                    refreshDone();
                    return;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1000 * 5);
            }
        });

    }

    @Override
    public void onDestroyView() {
        mContext.unregisterReceiver(mReceiver);
        super.onDestroyView();
    }
}
