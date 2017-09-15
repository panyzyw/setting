package com.yongyida.robot.settings.fragment;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.adapter.PersonListAdapter;
import com.yongyida.robot.settings.bean.Constants;
import com.yongyida.robot.settings.utils.ProviderUtils;
import com.yongyida.robot.settings.utils.Utils;
import com.yongyida.robot.settings.view.CircleImageView;
import com.yongyida.robot.settings.view.KCornerDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 人脸录入
 *
 * @author Bright. Create on 2016/11/7 0007.
 */
public class FaceInputFragment extends Fragment {
    Context mContext;
    View mBaseView;
    private KCornerDialog mEditNameialog;//昵称对话框
    private CircleImageView mAddPeople;
    private RecyclerView mPeoPleRv;
    private PersonListAdapter mAdapter;

    private List<String> mPersonList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mBaseView = inflater.inflate(R.layout.fragment_face_input_setup, container, false);
        initDate();
        initView();
        return mBaseView;
    }

    private void initView() {
        mAddPeople = (CircleImageView) mBaseView.findViewById(R.id.bt_new_people);
        mAddPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditNameDialog();
            }
        });

        mPeoPleRv = (RecyclerView) mBaseView.findViewById(R.id.recycler_view);
        mPeoPleRv.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        mAdapter = new PersonListAdapter(mPersonList, getActivity());
        mPeoPleRv.setAdapter(mAdapter);

    }

    private void initDate() {
        if (mPersonList != null)
            mPersonList.clear();
        //获取到全部的person
        mPersonList = getAllList(getImageNameList());
    }

    @Override
    public void onResume() {
        super.onResume();
        initDate();//重新获取数据
        mAdapter.setAdapterData(mPersonList);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 打开人脸识别的界面
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
            startActivity(intent);
        } catch (Exception e) {

        }
    }

    /**
     * 获取默认的图片
     *
     * @return
     */
    private List<String> getLocalList() {
        List<String> personList = new ArrayList<String>();
        personList.add(getString(R.string.person_dad));
        personList.add(getString(R.string.person_mum));
        personList.add(getString(R.string.person_baby));
        personList.add(getString(R.string.person_grandpa));
        personList.add(getString(R.string.person_grandma));
        return personList;
    }

    /**
     * 获取存储在人脸识别app中的人脸图片名
     *
     * @return
     */
    private List<String> getImageNameList() {
        List<String> imageNameList = new ArrayList<String>();
        try {
            Uri uri = Uri.parse("content://com.yyd.facedetect/images");
            ContentResolver rs = getActivity().getContentResolver();
            Cursor cs = rs.query(uri, null, null, null, null);
            while (cs.moveToNext()) {
                imageNameList.add(cs.getString(cs.getColumnIndex("display_name")));
            }
            cs.close();
        } catch (Exception e) {

        }
        return imageNameList;
    }

    /**
     * @param groupList
     * @return
     */
    private List<String> getAllList(List<String> groupList) {
        List<String> list = new ArrayList<String>();
        list.addAll(groupList);
        List<String> tmp = getLocalList();
        for (String name : list) {
            if (tmp.contains(name)) {
                tmp.remove(name);
            }
        }
        list.addAll(tmp);
        return list;
    }

    /**
     * 显示输入昵称对话框
     */
    public void showEditNameDialog() {
        if (!Utils.getInstance().checkNetworkStatus(mContext)) {
            Toast.makeText(mContext, R.string.please_connect_net, Toast.LENGTH_LONG).show();
            return;
        }
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog_edit, (ViewGroup) getActivity().findViewById(R.id.alert_root_RLayout));
        ImageView ivClose = (ImageView) dialogLayout.findViewById(R.id.iv_dialog_close);
        final TextView tvTitle = (TextView) dialogLayout.findViewById(R.id.tv_title);
        final EditText etContent = (EditText) dialogLayout.findViewById(R.id.et_content);
        etContent.setInputType(InputType.TYPE_CLASS_TEXT);
        Button btnConfirm = (Button) dialogLayout.findViewById(R.id.btn_confirm);

        tvTitle.setText(getText(R.string.dialog_input_name));

        mEditNameialog = new KCornerDialog(getActivity(), 0, 0, dialogLayout, R.style.KCornerDialog);
        mEditNameialog.show();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditNameialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etContent.getText().toString().trim();
                if (name.length() == 0) {
                    Toast.makeText(mContext, R.string.dialog_toast_input_nickname, Toast.LENGTH_SHORT).show();
                    return;
                } else if (mPersonList.contains(name)) {
                    Toast.makeText(mContext, R.string.name_exist, Toast.LENGTH_LONG).show();
                    return;
                } else if (mPersonList != null && mPersonList.size() >= 20) {
                    Toast.makeText(mContext, R.string.exceed_limit, Toast.LENGTH_LONG).show();
                    return;
                }
                openNewAddFaceActivity(name);
                mEditNameialog.dismiss();
            }
        });
    }

}
