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
import com.yongyida.robot.settings.bean.Speaker;

import java.util.List;

public class SpeakerListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Speaker> mListItems;
    private LayoutInflater mInflater;

    public SpeakerListAdapter(Context context, List<Speaker> listItems) {
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
            convertView = mInflater.inflate(R.layout.lst_speaker_item, viewGroup, false);
            mViewHolder = new ViewHolder();
            mViewHolder.title = (TextView) convertView.findViewById(R.id.item_speaker_title);
            mViewHolder.content = (TextView) convertView.findViewById(R.id.item_speaker_content);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.title.setText(mListItems.get(pos).getTitle());
        mViewHolder.content.setText(mListItems.get(pos).getContent());
        return convertView;
    }

    private class ViewHolder {
        TextView title;
        TextView content;
    }
}
