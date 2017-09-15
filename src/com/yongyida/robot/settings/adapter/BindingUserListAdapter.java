package com.yongyida.robot.settings.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.utils.log.LogTool;
import com.yongyida.robot.settings.bean.IntentConstant;
import com.yongyida.robot.settings.bean.User;
import com.yongyida.robot.settings.view.ConfirmDialog;

import java.util.LinkedList;
import java.util.List;


public class BindingUserListAdapter extends BaseAdapter {
    private static final String TAG = BindingUserListAdapter.class.getSimpleName();
    private Context mContext;
    private List<User> mDatas = new LinkedList<>();
    private ViewHolder mViewHolder;
    private ConfirmDialog dialog;

    public BindingUserListAdapter(Context context, List<User> bindingList) {
        this.mContext = context;
        mDatas.clear();
        mDatas.addAll(bindingList);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.qrcode_binding_list_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.tv_name = (TextView) convertView.findViewById(R.id.user_name);
            mViewHolder.tv_phone = (TextView) convertView.findViewById(R.id.phone_number);
            mViewHolder.tv_online = (TextView) convertView.findViewById(R.id.user_online);
            mViewHolder.btn_delete = (Button) convertView.findViewById(R.id.btn_delete);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        User user = mDatas.get(position);
        String name = user.getName();
        if (user.getName() == null || user.getName().isEmpty()) {
            name = mContext.getString(R.string.user_name) + (position + 1);
            LogTool.showLog(TAG, "getView", "name=" + name);
        }
        mViewHolder.tv_name.setText(name);
        if (user.getNickName() != null && !user.getNickName().isEmpty()) {
            mViewHolder.tv_phone.setText(user.getNickName());
            LogTool.showLog(TAG, "getView", "getNickName=" + user.getNickName());
        }
        mViewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 弹出对话框询问是否恢复出厂设置
                ConfirmDialog.Builder builder = new ConfirmDialog.Builder(mContext);
                builder.setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 解除绑定
                        User user = mDatas.get(position);
                        Intent intent = new Intent(IntentConstant.DELETE);
                        intent.putExtra("id", user.getId().toString());
                        mContext.sendBroadcast(intent);

                        // 刷新
                        mDatas.remove(position);
                        notifyDataSetChanged();

                        // 重新获取
                        Intent mIntent = new Intent(IntentConstant.QUERY);
                        mContext.sendBroadcast(mIntent);
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.cancel();
                    }
                });
                builder.setMessage(R.string.qr_code_dialog_content);
                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_phone;
        TextView tv_online;
        Button btn_delete;
    }

}
