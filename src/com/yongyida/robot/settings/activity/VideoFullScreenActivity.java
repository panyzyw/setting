package com.yongyida.robot.settings.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.fragment.UserGuideFragment;
import com.yongyida.robot.settings.utils.Utils;

/**
 * 操作指导视频全屏界面
 *
 * @author Bright. Create on 2016/11/19 0019.
 */
public class VideoFullScreenActivity extends Activity {
    Context mContext;
    private FragmentManager fragmentManager;
    UserGuideFragment fragment = new UserGuideFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getInstance().hideStatusBar(this);
        setContentView(R.layout.activity_video_full_screen);
        mContext = this;
        init();
    }

    void init() {
        fragmentManager = getFragmentManager();
        fragment.setFullScreen(true);
        changeFragment(fragment);
    }

    void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.video_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            fragment.setFullScreen(false);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
