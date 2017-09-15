package com.yongyida.robot.settings.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yongyida.robot.settings.R;

/**
 * G-sensor设置
 *
 * @author Bright. Create on 2016/11/7 0007.
 */

public class GSensorFragment extends Fragment {
    Context mContext;
    View mBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mBaseView = inflater.inflate(R.layout.fragment_guide_setup, container, false);
        return mBaseView;
    }
}
