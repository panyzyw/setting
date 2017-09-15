package com.yongyida.robot.settings.fragment;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yongyida.robot.settings.R;

/**
 * 公司简介
 *
 * @author Bright. Create on 2016/11/7 0007.
 */
public class IntroduceFragment extends Fragment {
    Context mContext;
    View mBaseView;
    TextView title;
    static final int CLICK_LIMIT = 10;
    static final long TIME_LIMIT = 1000 * 2;
    static long lastClickTime = 0;
    static int currClick = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mBaseView = inflater.inflate(R.layout.fragment_introduce, container, false);
        initUI();
        return mBaseView;
    }

    void initUI() {
        title = (TextView) mBaseView.findViewById(R.id.title);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currTime = System.currentTimeMillis();
                if (currTime - lastClickTime <= TIME_LIMIT) {
                    currClick++;
                    if (currClick >= CLICK_LIMIT) {
                        Intent intent = new Intent();
                        ComponentName comp = new ComponentName("com.android.settings",
                                "com.android.settings.Settings");
                        intent.setComponent(comp);
                        intent.setAction("android.intent.action.VIEW");
                        startActivity(intent);
                        currClick = 0;
                        lastClickTime = 0;
                    }
                } else {
                    lastClickTime = currTime;
                    currClick = 0;
                }
            }
        });
    }
}
