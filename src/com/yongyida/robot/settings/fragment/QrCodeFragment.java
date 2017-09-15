package com.yongyida.robot.settings.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yongyida.robot.settings.R;

/**
 * 查看机器人二维码，内含二维码图片和已绑定用户列表
 *
 * @author Bright. Create on 2016/11/7 0007.
 */

public class QrCodeFragment extends Fragment {
    private Context mContext;
    private View mBaseView;
    private FragmentManager fragmentManager;
    private Button btn_display_qrcode;
    private Fragment currentFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mBaseView = inflater.inflate(R.layout.fragment_qrcode_setup, container, false);
        init();
        return mBaseView;
    }

    void init() {
        fragmentManager = getChildFragmentManager();
        currentFragment = new QrCodeImageFragment();
        changeFragment(currentFragment);
        btn_display_qrcode = (Button) mBaseView.findViewById(R.id.btn_display_qrcode);

        btn_display_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFragment instanceof QrCodeImageFragment) {
                    currentFragment = new QrCodeBindingListFragment();
                    changeFragment(currentFragment);
                    btn_display_qrcode.setText(R.string.display_qrcode);
                } else if (currentFragment instanceof QrCodeBindingListFragment) {
                    currentFragment = new QrCodeImageFragment();
                    changeFragment(currentFragment);
                    btn_display_qrcode.setText(R.string.display_bind_list);
                }
            }
        });

    }

    void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.qrcode_content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
