package com.yongyida.robot.settings.adapter;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.bean.Constants;
import com.yongyida.robot.settings.fragment.FaceInputFragment;
import com.yongyida.robot.settings.utils.Utils;
import com.yongyida.robot.settings.view.CircleImageView;
import com.yongyida.robot.settings.view.HintDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tianchunming on 2017/8/14.
 */

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.MyViewHolder> {
    private List<String> mIds;
    private Context mContext;


    public PersonListAdapter(List<String> ids, Context context) {
        mContext = context;
        this.mIds = ids;
    }

    public void setAdapterData(List<String> ids) {
        this.mIds = ids;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_person_list_adapter, viewGroup, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {
        myViewHolder.tv.setText(mIds.get(i));
        if (mContext.getString(R.string.person_dad).equals(mIds.get(i))) { //爸爸图片 如果本地没有 就取默认的

            if (!getPhotoByName(mIds.get(i))) {
                myViewHolder.civ.setImageResource(R.drawable.bb);
            } else {
                myViewHolder.civ.setImageBitmap(getBitmapFromResolver(mIds.get(i)));
            }
        } else if (mContext.getString(R.string.person_mum).equals(mIds.get(i))) { //妈妈图片 如果本地没有 就取默认的
            if (!getPhotoByName(mIds.get(i))) {
                myViewHolder.civ.setImageResource(R.drawable.mm);
            } else {
                myViewHolder.civ.setImageBitmap(getBitmapFromResolver(mIds.get(i)));
            }
        } else if (mContext.getString(R.string.person_baby).equals(mIds.get(i))) { //宝贝图片 如果本地没有 就取默认的
            if (!getPhotoByName(mIds.get(i))) {
                myViewHolder.civ.setImageResource(R.drawable.baby);
            } else {
                myViewHolder.civ.setImageBitmap(getBitmapFromResolver(mIds.get(i)));
            }
        } else if (mContext.getString(R.string.person_grandpa).equals(mIds.get(i))) { //爷爷图片 如果本地没有 就取默认的
            if (!getPhotoByName(mIds.get(i))) {
                myViewHolder.civ.setImageResource(R.drawable.yy);
            } else {
                myViewHolder.civ.setImageBitmap(getBitmapFromResolver(mIds.get(i)));
            }
        } else if (mContext.getString(R.string.person_grandma).equals(mIds.get(i))) { //奶奶图片 如果本地没有 就取默认的
            if (!getPhotoByName(mIds.get(i))) {
                myViewHolder.civ.setImageResource(R.drawable.nn);
            } else {
                myViewHolder.civ.setImageBitmap(getBitmapFromResolver(mIds.get(i)));
            }
        } else {
            if (getPhotoByName(mIds.get(i))) {
                myViewHolder.civ.setImageBitmap(getBitmapFromResolver(mIds.get(i)));
            } else {
                myViewHolder.civ.setImageResource(R.mipmap.ic_launcher);
            }
        }

        myViewHolder.civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.getInstance().checkNetworkStatus(mContext)) {
                    Toast.makeText(mContext, R.string.please_connect_net, Toast.LENGTH_LONG).show();
                    return;
                }
                List<String> constantList = getLocalList();
                if (constantList.contains(mIds.get(i))) {  //爸爸 妈妈 这些有默认图片的
                    if (!getPhotoByName(mIds.get(i))) {    //本地不存在
                        if (mIds != null && mIds.size() >= 20) {
                            Toast.makeText(mContext, R.string.exceed_limit, Toast.LENGTH_LONG).show();
                            return;
                        }
                        HintDialog dialog = new HintDialog(mContext, mIds.get(i), HintDialog.ADD);
                        dialog.setDialogCallback(new HintDialog.Dialogcallback() {
                            @Override
                            public void onConfirm(String string) {
                                openNewAddFaceActivity(string);
                            }
                        });
                        dialog.show();
                    } else {                           //本地存在
                        HintDialog dialog = new HintDialog(mContext, mIds.get(i), HintDialog.MODIFY);
                        dialog.setDialogCallback(new HintDialog.Dialogcallback() {
                            @Override
                            public void onConfirm(String string) {
                                openNewAddFaceActivity(string);
                            }
                        });
                        dialog.show();
                    }
                } else {
                    HintDialog dialog = new HintDialog(mContext, mIds.get(i), HintDialog.MODIFY);
                    dialog.setDialogCallback(new HintDialog.Dialogcallback() {
                        @Override
                        public void onConfirm(String string) {
                            openNewAddFaceActivity(string);
                        }
                    });
                    dialog.show();
                }
            }
        });
        myViewHolder.civ.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String imageName = mIds.get(i);
                if (!Utils.getInstance().checkNetworkStatus(mContext)) {
                    Toast.makeText(mContext, R.string.please_connect_net, Toast.LENGTH_LONG).show();
                    return false;
                }
                if (i >= 0) {
                    List<String> constantList = getLocalList();
                    if (constantList.contains(imageName)) {  //爸爸 妈妈 这些有默认图片的
                        if (!getPhotoByName(imageName)) {
                            return false;
                        }
                    } else {
                        mIds.remove(i);
                    }
                    HintDialog dialog = new HintDialog(mContext, imageName, HintDialog.DELETE);
                    dialog.setDialogCallback(new HintDialog.Dialogcallback() {
                        @Override
                        public void onConfirm(String string) {
                            deletePersonFromResolver(string);
                            notifyDataSetChanged();
                        }
                    });
                    dialog.show();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mIds.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        CircleImageView civ;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = ((TextView) itemView.findViewById(R.id.tv_name));
            civ = (CircleImageView) itemView.findViewById(R.id.civ_photo);
        }
    }

    /**
     * 获取本地的照片
     *
     * @param name
     * @return
     */
    private boolean getPhotoByName(String name) {
        File file = new File(Constants.PORTRAIT_LOCATION);
        if (file != null) {
            String[] arr = file.list();
            if (arr != null) {
                List<String> list = Arrays.asList(arr);
                if (list.contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取图片bitmap
     *
     * @param imageName
     * @return
     */
    private Bitmap getBitmapFromResolver(String imageName) {
        ContentResolver rs = mContext.getContentResolver();
        Uri uriImage = Uri.parse("content://com.yyd.facedetect/images/" + imageName);
        try {
            InputStream in = rs.openInputStream(uriImage);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除person
     *
     * @param imageName
     */
    private void deletePersonFromResolver(String imageName) {
        try {
            Uri uri = Uri.parse("content://com.yyd.facedetect/images");
            ContentResolver rs = mContext.getContentResolver();
            rs.delete(uri, imageName, null);
        } catch (Exception e) {

        }
    }

    /**
     * 获取本地的图片名称
     *
     * @return
     */
    private List<String> getLocalList() {
        List<String> personList = new ArrayList<String>();
        personList.add(mContext.getString(R.string.person_dad));
        personList.add(mContext.getString(R.string.person_mum));
        personList.add(mContext.getString(R.string.person_baby));
        personList.add(mContext.getString(R.string.person_grandpa));
        personList.add(mContext.getString(R.string.person_grandma));
        return personList;
    }

    /**
     * 跳转到人脸识别界面
     *
     * @param str
     */
    public void openNewAddFaceActivity(String str) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName("com.yyd.facedetect", "com.yyd.facedetect.activity.NewAddFaceActivity");
        try {
            intent.setComponent(cn);
            intent.putExtra(Constants.INTENT_PERSON_ID, str);
            intent.putExtra(Constants.INTENT_MODIFY_NAME, str);
            mContext.startActivity(intent);
        } catch (Exception e) {

        }
    }
}
