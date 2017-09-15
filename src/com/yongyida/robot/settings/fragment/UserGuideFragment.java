package com.yongyida.robot.settings.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.yongyida.robot.settings.R;
import com.yongyida.robot.settings.activity.VideoFullScreenActivity;

import java.util.Formatter;
import java.util.Locale;

import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;
import static android.widget.RelativeLayout.LayoutParams;

/**
 * 操作指引界面
 * <p>
 * 全屏和非全屏都是用这个Fragment，所以一些播放的进度等标识，需要静态变量保存，
 * 并在onResume时根据此时是全屏还是非全屏，进行状态恢复。
 * <br>
 * 全屏界面 {@link VideoFullScreenActivity}
 * <p>
 * 视频界面，手势滑动，1 左边上下滑动：调节音量，2 中间左右滑动：调节进度，3 右边上下滑动：调节亮度
 *
 * @author Bright. Create on 2016/11/7 0007.
 */
public class UserGuideFragment extends Fragment implements View.OnTouchListener {
    Context mContext;
    static final int WHAT_INVISIBLE = 0;
    static final int WHAT_VISIBLE = 1;
    static final int WHAT_REFRESH_TIME = 2;
    static final int DISPLAY_TIME = 2000;
    static int currentPos = 0;
    static boolean isPlaying = false;
    static boolean isFullScreen = false;

    boolean isControllerDisplay = true;
    boolean isVideoPrepared = false;

    boolean isDestroyed = false;

    private View mBaseView;
    private VideoView videoView;
    private ImageButton btn_play_pause, btn_full_screen;
    private RelativeLayout layout_controller, layout_video;
    private TextView tv_progress;
    private SeekBar progress;
    private RelativeLayout adjust_volume_layout, adjust_bright_layout;// 音量控制布局,亮度控制布局
    private TextView adjust_tv_volume_percentage, adjust_tv_bright_percentage;// 音量百分比,亮度百分比
    private ImageView adjust_iv_player_volume;// 音量图标
    private RelativeLayout adjust_progress_layout;// 进度图标
    private TextView adjust_tv_progress_time;// 播放时间进度
    private ImageView adjust_iv_progress;// 快进或快退标志
    private AudioManager audioManager;
    private int currentVolume, maxVolume;
    private float mBrightness = -1f; // 亮度

    Uri uri;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    String allTime;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_INVISIBLE:
                    if (layout_controller != null && btn_play_pause != null) {
                        if (videoView.isPlaying()) {
                            layout_controller.setVisibility(View.INVISIBLE);
                            btn_play_pause.setVisibility(View.INVISIBLE);
                        }
                        isControllerDisplay = false;
                    }
                    break;

                case WHAT_VISIBLE:
                    if (layout_controller != null && btn_play_pause != null) {
                        layout_controller.setVisibility(View.VISIBLE);
                        btn_play_pause.setVisibility(View.VISIBLE);
                        isControllerDisplay = true;
                    }
                    break;

                case WHAT_REFRESH_TIME:
                    if (tv_progress != null && progress != null) {
                        currentPos = videoView.getCurrentPosition();
                        if (videoView.getCurrentPosition() != 0) {
                            tv_progress.setText(stringForTime(videoView.getCurrentPosition()) + "/" + allTime);
                            progress.setProgress(videoView.getCurrentPosition());
                        }
                        if (!isDestroyed) {
                            handler.sendEmptyMessageDelayed(WHAT_REFRESH_TIME, 1000);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mContext = getActivity();
        mBaseView = inflater.inflate(R.layout.fragment_guide_setup, container, false);
        findUI();
        init();
        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.seekTo(currentPos);
        if (isPlaying) {
            videoView.start();
            btn_play_pause.setImageResource(R.drawable.btn_pause);
            handler.sendEmptyMessageDelayed(WHAT_INVISIBLE, DISPLAY_TIME);
        } else {
            videoView.pause();
            btn_play_pause.setImageResource(R.drawable.btn_play);
            handler.sendEmptyMessage(WHAT_VISIBLE);
        }

        if (isFullScreen) {
            fullScreenLayout();
            btn_full_screen.setBackgroundResource(R.drawable.btn_normal_screen_selector);
        } else {
            normalScreenLayout();
            btn_full_screen.setBackgroundResource(R.drawable.btn_full_screen_selector);
        }
    }

    void findUI() {
        btn_play_pause = (ImageButton) mBaseView.findViewById(R.id.btn_play_pause);
        btn_full_screen = (ImageButton) mBaseView.findViewById(R.id.btn_full_screen);
        videoView = (VideoView) mBaseView.findViewById(R.id.videoView);
        layout_controller = (RelativeLayout) mBaseView.findViewById(R.id.layout_controller);
        layout_video = (RelativeLayout) mBaseView.findViewById(R.id.layout_video);
        tv_progress = (TextView) mBaseView.findViewById(R.id.tv_progress);
        progress = (SeekBar) mBaseView.findViewById(R.id.seekBar);
        tv_progress.setText(mContext.getString(R.string.video_default_time));
        adjust_volume_layout = (RelativeLayout) mBaseView.findViewById(R.id.gesture_volume_layout);
        adjust_bright_layout = (RelativeLayout) mBaseView.findViewById(R.id.gesture_bright_layout);
        adjust_progress_layout = (RelativeLayout) mBaseView.findViewById(R.id.gesture_progress_layout);
        adjust_tv_progress_time = (TextView) mBaseView.findViewById(R.id.gesture_tv_progress_time);
        adjust_tv_volume_percentage = (TextView) mBaseView.findViewById(R.id.gesture_tv_volume_percentage);
        adjust_tv_bright_percentage = (TextView) mBaseView.findViewById(R.id.gesture_tv_bright_percentage);
        adjust_iv_progress = (ImageView) mBaseView.findViewById(R.id.gesture_iv_progress);
        adjust_iv_player_volume = (ImageView) mBaseView.findViewById(R.id.gesture_iv_player_volume);
        initVideoView();
    }

    void initVideoView() {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        isDestroyed = false;

        uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.smart);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btn_play_pause.setImageResource(R.drawable.btn_play);
                videoView.seekTo(0);
                progress.setProgress(0);
                tv_progress.setText(stringForTime(0) + "/" + allTime);
                handler.removeMessages(WHAT_VISIBLE);
                handler.removeMessages(WHAT_INVISIBLE);
                handler.sendEmptyMessage(WHAT_VISIBLE);
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                isVideoPrepared = true;
                progress.setMax(videoView.getDuration());
                progress.setProgress(videoView.getCurrentPosition());
                allTime = stringForTime(videoView.getDuration());
                tv_progress.setText(stringForTime(videoView.getCurrentPosition()) + "/" + allTime);
                handler.sendEmptyMessage(WHAT_REFRESH_TIME);
            }
        });
        videoView.setOnTouchListener(this);
    }

    void init() {
        btn_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    btn_play_pause.setImageResource(R.drawable.btn_play);
                    if (videoView.canPause()) {
                        videoView.pause();
                        isPlaying = false;
                        currentPos = videoView.getCurrentPosition();
                    }
                } else {
                    if (isVideoPrepared) {
                        videoView.start();
                        isPlaying = true;
                        currentPos = videoView.getCurrentPosition();
                        btn_play_pause.setImageResource(R.drawable.btn_pause);
                    }
                }
                handler.removeMessages(WHAT_VISIBLE);
                handler.removeMessages(WHAT_INVISIBLE);
                handler.sendEmptyMessageDelayed(WHAT_INVISIBLE, DISPLAY_TIME);
            }
        });
        btn_full_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFullScreen) {
                    isFullScreen = false;
                    isPlaying = videoView.isPlaying();
                    currentPos = videoView.getCurrentPosition();
                    getActivity().finish();
                } else {
                    isFullScreen = true;
                    currentPos = videoView.getCurrentPosition();
                    isPlaying = videoView.isPlaying();
                    Intent intent = new Intent(mContext, VideoFullScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int pos, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(pos);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 作为设置中一个界面时的布局
     */
    void normalScreenLayout() {
        LayoutParams videoParams = (LayoutParams) videoView.getLayoutParams();
        videoParams.width = videoParams.height = LayoutParams.WRAP_CONTENT;
        videoParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        videoParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoParams.addRule(RelativeLayout.CENTER_VERTICAL);
        videoView.setLayoutParams(videoParams);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout_video.getLayoutParams();
        layoutParams.setMargins(0, 0, (int) mContext.getResources().getDimension(R.dimen.act_content_padding_left), 0);
        layoutParams.width = layoutParams.height = LayoutParams.MATCH_PARENT;
        layout_video.setLayoutParams(layoutParams);
    }

    /**
     * 作为全屏时的布局
     */
    void fullScreenLayout() {
        LayoutParams videoParams = (LayoutParams) videoView.getLayoutParams();
        videoParams.setMargins(0, 0, 0, 0);
        videoParams.width = videoParams.height = LayoutParams.MATCH_PARENT;
        videoParams.addRule(ALIGN_PARENT_BOTTOM);
        videoView.setLayoutParams(videoParams);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout_video.getLayoutParams();
        layoutParams.width = layoutParams.height = LayoutParams.MATCH_PARENT;
        layoutParams.setMargins(0, 0, 0, 0);
        layoutParams.setMarginEnd(0);
        layout_video.setLayoutParams(layoutParams);
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 是否设置为全屏
     */
    public void setFullScreen(boolean bool) {
        isFullScreen = bool;
        if (videoView != null) {
            isPlaying = videoView.isPlaying();
            currentPos = videoView.getCurrentPosition();
        }
    }

    @Override
    public void onDestroyView() {
        isDestroyed = true;
        currentPos = 0;
        isPlaying = false;
        isFullScreen = false;
        super.onDestroyView();
    }

    float downX, downY;
    boolean isMoving = false;
    // 判断是否正在调节进度，如果是，滑到调节音量、亮度区域，也是调节进度
    boolean isAdjustingVideoPos = false;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (isControllerDisplay) {
            handler.removeMessages(WHAT_VISIBLE);
            handler.removeMessages(WHAT_INVISIBLE);
            handler.sendEmptyMessage(WHAT_INVISIBLE);
        } else {
            float currX, currY;
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMoving = false;
                    downX = motionEvent.getX();
                    downY = motionEvent.getY();
                    currentPos = videoView.getCurrentPosition();
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    mBrightness = getActivity().getWindow().getAttributes().screenBrightness;
                    if (mBrightness <= 0.00f) {
                        mBrightness = 0.50f;
                    }
                    if (mBrightness < 0.01f) {
                        mBrightness = 0.01f;
                    }
                    if (downX >= videoView.getWidth() * 0.3f &&
                            downX <= videoView.getWidth() * 0.7f) {
                        isAdjustingVideoPos = true;
                    } else {
                        isAdjustingVideoPos = false;
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:
                    isMoving = true;
                    currX = motionEvent.getX();
                    currY = motionEvent.getY();
                    if (Math.abs(currX - downX) <= 10 && Math.abs(currY - downY) <= 10) {
                        isMoving = false;
                        return true;
                    }
                    if (currX <= videoView.getWidth() * 0.2f && !isAdjustingVideoPos &&
                            Math.abs(currY - downY) > Math.abs(currX - downX)) {
                        // TODO 调节声音
                        adjust_volume_layout.setVisibility(View.VISIBLE);
                        adjust_bright_layout.setVisibility(View.INVISIBLE);
                        adjust_progress_layout.setVisibility(View.INVISIBLE);
                        adjustVolume(currX - downX, currY - downY);

                    } else if (currX >= videoView.getWidth() * 0.8f && !isAdjustingVideoPos &&
                            Math.abs(currY - downY) > Math.abs(currX - downX)) {
                        // TODO 调节亮度
                        adjust_volume_layout.setVisibility(View.INVISIBLE);
                        adjust_bright_layout.setVisibility(View.VISIBLE);
                        adjust_progress_layout.setVisibility(View.INVISIBLE);
                        adjustBrightness(currX - downX, currY - downY);

                    } else if (isAdjustingVideoPos) {
                        // TODO 调节进度
                        if (Math.abs(currX - downX) >= Math.abs(currY - downY)) {
                            adjust_volume_layout.setVisibility(View.INVISIBLE);
                            adjust_bright_layout.setVisibility(View.INVISIBLE);
                            adjust_progress_layout.setVisibility(View.VISIBLE);
                            adjustVideoPosition(currX - downX, currY - downY);
                        }
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    adjust_volume_layout.setVisibility(View.INVISIBLE);
                    adjust_bright_layout.setVisibility(View.INVISIBLE);
                    adjust_progress_layout.setVisibility(View.INVISIBLE);
                    if (!isMoving) {
                        currentPos = videoView.getCurrentPosition();
                        handler.removeMessages(WHAT_VISIBLE);
                        handler.removeMessages(WHAT_INVISIBLE);
                        handler.sendEmptyMessage(WHAT_VISIBLE);
                        if (videoView.isPlaying()) {
                            handler.sendEmptyMessageDelayed(WHAT_INVISIBLE, DISPLAY_TIME);
                        }
                    }

                    break;
            }

        }
        return false;
    }

    /**
     * 调节音量
     *
     * @param distanceX 滑动的横向距离
     * @param distanceY 滑动的纵向距离
     */
    void adjustVolume(float distanceX, float distanceY) {
        if (Math.abs(distanceX) < Math.abs(distanceY)) {
            float percentage = Math.abs(distanceY / videoView.getHeight());
            if (distanceY > 0) {
                percentage = -percentage;
            }
            int index = (int) (percentage * maxVolume + currentVolume);
            if (index >= maxVolume) {
                index = maxVolume;
                adjust_iv_player_volume.setImageResource(R.drawable.ic_volume);
            } else if (index <= 0) {
                index = 0;
                adjust_iv_player_volume.setImageResource(R.drawable.ic_slient);
            } else if (index > 0 && index <= 100) {
                adjust_iv_player_volume.setImageResource(R.drawable.ic_volume);
            }
            String percent = Math.abs((int) ((float) index / maxVolume * 100)) + "%";
            adjust_tv_volume_percentage.setText(percent);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        }
    }

    /**
     * 调节亮度
     *
     * @param distanceX 滑动的横向距离
     * @param distanceY 滑动的纵向距离
     */
    void adjustBrightness(float distanceX, float distanceY) {
        if (Math.abs(distanceX) < Math.abs(distanceY)) {
            float percentage = Math.abs(distanceY / videoView.getHeight());
            if (distanceY > 0) {
                percentage = -percentage;
            }
            WindowManager.LayoutParams lpa = getActivity().getWindow().getAttributes();
            lpa.screenBrightness = percentage * 1.0f + mBrightness;
            if (lpa.screenBrightness > 1.0f) {
                lpa.screenBrightness = 1.0f;
            } else if (lpa.screenBrightness < 0.01f) {
                lpa.screenBrightness = 0.01f;
            }
            getActivity().getWindow().setAttributes(lpa);
            String percent = (int) (lpa.screenBrightness * 100) + "%";
            adjust_tv_bright_percentage.setText(percent);
        }
    }

    /**
     * 调节进度
     *
     * @param distanceX 滑动的横向距离
     * @param distanceY 滑动的纵向距离
     */
    void adjustVideoPosition(float distanceX, float distanceY) {
        if (Math.abs(distanceX) > Math.abs(distanceY)) {
            handler.removeMessages(WHAT_REFRESH_TIME);
            float percentage = distanceX / (videoView.getWidth() * 0.6f);
            int newPos = (int) (percentage * videoView.getDuration() + currentPos);
            if (distanceX > 0) {
                adjust_iv_progress.setImageResource(R.drawable.ic_forward);
            } else {
                adjust_iv_progress.setImageResource(R.drawable.ic_backward);
            }
            if (newPos > videoView.getDuration()) {
                newPos = videoView.getDuration();
            } else if (newPos < 0) {
                newPos = 0;
            }
            videoView.seekTo(newPos);
            String text = stringForTime(newPos) + "/" + allTime;
            adjust_tv_progress_time.setText(text);
            if (tv_progress != null && progress != null) {
                if (videoView.getCurrentPosition() != 0) {
                    tv_progress.setText(text);
                    progress.setProgress(videoView.getCurrentPosition());
                }
            }
        }
    }
}
