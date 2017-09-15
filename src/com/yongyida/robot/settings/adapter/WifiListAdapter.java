package com.yongyida.robot.settings.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.utils.Utils;

import java.util.List;

public class WifiListAdapter extends BaseAdapter {
    private List<ScanResult> mListwifi;
    private String mCurWifiSSID;
    Context mContext;
    LayoutInflater mInflater;

    public WifiListAdapter(Context context, List<ScanResult> listwifi, String curWifiSSID) {
        mContext = context;
        mListwifi = listwifi;
        mCurWifiSSID = curWifiSSID;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mListwifi.size();
    }

    @Override
    public Object getItem(int pos) {
        return mListwifi.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        ScanResult scanResult = mListwifi.get(pos);
        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lst_wifi_item, viewGroup, false);
            mViewHolder = new ViewHolder();
            mViewHolder.wifiName = (TextView) convertView.findViewById(R.id.item_wifi_name);
            mViewHolder.wifiIcon = (ImageView) convertView.findViewById(R.id.item_wifi_icon);
            mViewHolder.wifiSelect = (ImageView) convertView.findViewById(R.id.item_wifi_select);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.wifiName.setText(scanResult.SSID);
        if (!"".equals(Utils.getInstance().getEncryptionType(scanResult))) {//判断wifi有没有加密,有加密显示有锁图标
            //判断信号强度，显示对应的指示图标
            if (Math.abs(scanResult.level) > 60) {
                mViewHolder.wifiIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wifi_1));
            } else if (Math.abs(scanResult.level) > 30) {
                mViewHolder.wifiIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wifi_2));
            } else {
                mViewHolder.wifiIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wifi_3));
            }
        } else {//没有加密显示没锁图标
            if (Math.abs(scanResult.level) > 60) {
                mViewHolder.wifiIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wifi_4));
            } else if (Math.abs(scanResult.level) > 30) {
                mViewHolder.wifiIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wifi_5));
            } else {
                mViewHolder.wifiIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wifi_6));
            }
        }
        if (scanResult.SSID.equals(mCurWifiSSID)) {
            mViewHolder.wifiSelect.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.wifiSelect.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView wifiName;
        ImageView wifiIcon;
        ImageView wifiSelect;
    }

    public void updateWifiList(List<ScanResult> listwifi,String curWifiSSID) {
        mCurWifiSSID = curWifiSSID;
        mListwifi.clear();
        mListwifi.addAll(listwifi);
        notifyDataSetChanged();
    }

}
