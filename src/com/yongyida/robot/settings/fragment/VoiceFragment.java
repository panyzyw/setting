package com.yongyida.robot.settings.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.adapter.SpeakerListAdapter;
import com.yongyida.robot.settings.bean.Speaker;

import java.util.ArrayList;
import java.util.List;


public class VoiceFragment extends Fragment {
    private Context mContext;
    private View mBaseView;
    private ListView mLvSpeaker;

    private List<Speaker> mSpeakerList;
    private SpeakerListAdapter mSpeakerListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mBaseView = inflater.inflate(R.layout.fragment_speaker_setup, container, false);
        initView();
        initData();
        return mBaseView;
    }

    private void initView() {
        mLvSpeaker = (ListView) mBaseView.findViewById(R.id.lv_speaker);
    }

    private void initData() {
        mSpeakerList = filledData(mContext.getResources().getStringArray(R.array.speaker_title),
                mContext.getResources().getStringArray(R.array.speaker_content));
        mSpeakerListAdapter = new SpeakerListAdapter(mContext, mSpeakerList);
        mLvSpeaker.setAdapter(mSpeakerListAdapter);
    }

    /**
     * 获得数据
     *
     * @param titleDate
     * @param contentDate
     * @return
     */
    private List<Speaker> filledData(String[] titleDate, String[] contentDate) {
        List<Speaker> speakerList = new ArrayList<Speaker>();
        for (int i = 0; i < titleDate.length; i++) {
            Speaker speaker = new Speaker();
            speaker.setTitle(titleDate[i]);
            speaker.setContent(contentDate[i]);
            speakerList.add(speaker);
        }
        return speakerList;
    }
}
