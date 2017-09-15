package com.yongyida.robot.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import com.yongyida.robot.settings.bean.SettingsListItem;
import com.yongyida.robot.settings.adapter.SettingsListAdapter;
import com.yongyida.robot.settings.fragment.AboutRobotFragment;
import com.yongyida.robot.settings.fragment.FaceInputFragment;
import com.yongyida.robot.settings.fragment.ParentsFragment;
import com.yongyida.robot.settings.fragment.AdvancedSettingFragment;
import com.yongyida.robot.settings.fragment.IntroduceFragment;
import com.yongyida.robot.settings.fragment.NetworkFragment;
import com.yongyida.robot.settings.fragment.QrCodeFragment;
import com.yongyida.robot.settings.fragment.VoiceFragment;
import com.yongyida.robot.settings.fragment.ContactUsFragment;
import com.yongyida.robot.settings.utils.Utils;

public class MainActivity extends Activity {

    private Context mContext;
    private ListView settingListView;
    private FragmentManager fragmentManager;
    private SettingsListAdapter mAdapter;
    public static Fragment currentFragment;

    private int mPos;

    public final static int ID_QRCODE = 0;
    public final static int ID_FACING = 1;
    public final static int ID_NETWORK = 2;
    public final static int ID_PARENTS = 3;
    public final static int ID_VOICE = 4;
    public final static int ID_ADVANCEDSETTING = 5;
    public final static int ID_CONTACT = 6;
    public final static int ID_ABOUTROBOT = 7;

    /**
     * Fragment对象，与 < string-array name="setting_list_title" > 顺序对应
     * 用于切换fragment
     */
    public Fragment[] fragments = {
            new QrCodeFragment(),          //查看编码
            new FaceInputFragment(),       //人脸录入
            new NetworkFragment(),         // wifi
            new ParentsFragment(),         // 家长设置
            new VoiceFragment(),           // 语音命令
            new AdvancedSettingFragment(), //高级设置
            new ContactUsFragment(),       //联系我们
            new AboutRobotFragment(),      //关于机器人
    };
    //james
    public int[] imageSrc = {
            R.drawable.settings_qrcode,          //查看编码
            R.drawable.settings_facing,          //人脸录入
            R.drawable.settings_network,         // wifi
            R.drawable.settings_parents,         // 家长设置
            R.drawable.settings_voice,           // 语音命令
            R.drawable.settings_advancedsetting, //高级设置
            R.drawable.settings_contact,         //联系我们
            R.drawable.settings_aboutrobot       //关于机器人
    };

    public int[] itemIDs = {
            ID_QRCODE,
            ID_FACING,
            ID_NETWORK,
            ID_PARENTS,
            ID_VOICE,
            ID_ADVANCEDSETTING,
            ID_CONTACT,
            ID_ABOUTROBOT
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getInstance().hideStatusBar(this);
        setContentView(R.layout.activity_main);
        mPos = getIntent().getIntExtra("selectItem", 0);
        mContext = this;
        initDatas();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private List<SettingsListItem> listItems = new ArrayList<SettingsListItem>();
    String[] titles;

    private void initDatas() {
        upDateFaceLise();
        titles = getResources().getStringArray(R.array.setting_list_title);
        int[] imgSrc = imageSrc;
        int[] ids = itemIDs;
        listItems.clear();
        int length = imgSrc.length < titles.length ? imgSrc.length : titles.length;
        length = length < ids.length ? length : ids.length;
        for (int i = 0; i < length; i++) {
            SettingsListItem item = new SettingsListItem(titles[i], imgSrc[i], ids[i]);
            listItems.add(item);
        }
    }


    void init() {
        settingListView = (ListView) findViewById(R.id.setting_list);

        fragmentManager = getFragmentManager();

        mAdapter = new SettingsListAdapter(mContext, listItems);
        settingListView.setAdapter(mAdapter);

        //mPos为1，是从launcher状态栏上色wifi图标点击进入的
        if (mPos == 1) {
            changeFragment(fragments[mPos + 1]);
            mAdapter.setSelectedItem(mPos + 1);
            currentFragment = fragments[mPos + 1];
        } else {
            changeFragment(fragments[0]);
            currentFragment = fragments[0];
        }
        mAdapter.setSelectedItemText(titles[0]);
        mAdapter.notifyDataSetChanged();


        settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                onSettingsListItemClick(adapterView, pos);
            }
        });
    }

    /**
     * 设置菜单点击事件
     */

    void onSettingsListItemClick(AdapterView<?> adapterView, int pos) {
        if (!fragments[pos].equals(currentFragment)) {
            changeFragment(fragments[pos]);
            currentFragment = fragments[pos];
        } else {
            if (currentFragment instanceof IntroduceFragment ||
                    currentFragment instanceof ContactUsFragment) {
                changeFragment(fragments[pos]);
            }
        }
        mAdapter.setSelectedItem(pos);
    }


    void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onIVBack(View v) {
        finish();
    }

    //同步人脸识别的数据到本地
    private void upDateFaceLise() {
        try {
            ContentResolver rs = this.getContentResolver();
            Uri uri = Uri.parse("content://com.yyd.facedetect/images");
            ContentValues values = new ContentValues();
            rs.update(uri, values, null, null);
        } catch (Exception e) {

        }
    }
}
