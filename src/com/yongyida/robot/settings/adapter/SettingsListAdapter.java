package com.yongyida.robot.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.bean.SettingsListItem;

import java.util.List;

/**
 * @author Bright. Create on 2016/11/7 0007.
 */
public class SettingsListAdapter extends BaseAdapter {
    String TAG = SettingsListAdapter.class.getSimpleName();
    private List<SettingsListItem> mListItems;
    String[] settingTitles;
    Context mContext;
    LayoutInflater mInflater;
    int selectedItem = 0;
    String selectedItemText = "";

    public SettingsListAdapter(Context context, List<SettingsListItem> listItems) {
        mContext = context;
        mListItems = listItems;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public Object getItem(int pos) {
        return mListItems.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lst_setting_item, viewGroup, false);
            mViewHolder = new ViewHolder();
            mViewHolder.title = (TextView) convertView.findViewById(R.id.item_title);
            mViewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_ic);
            mViewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.root);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.title.setText(mListItems.get(pos).getmTitle());
        mViewHolder.imageView.setImageResource(mListItems.get(pos).getmImageID());
        if (selectedItem == pos) {
            mViewHolder.relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.bg_item_white));
        } else {
            mViewHolder.relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.bg_item_gray));
        }
        return convertView;
    }

    private class ViewHolder {
        TextView title;
        //james
        private ImageView imageView;
        RelativeLayout relativeLayout;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
        notifyDataSetChanged();
    }

    public void setSelectedItemText(String selectedItemText) {
        this.selectedItemText = selectedItemText;
    }
}
