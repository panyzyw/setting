package com.yongyida.robot.settings.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yongyida.robot.settings.R;

/**
 * @author Bright 2016/11/11
 */
public class SlideToggleButton extends RelativeLayout {
    static final String TAG = SlideToggleButton.class.getSimpleName();

    Context mContext;
    static final int POSITION_LEFT = 0;
    static final int POSITION_CENTER = 1;
    static final int POSITION_RIGHT = 2;
    static final float currentAlpha = 1.0f;
    static final float otherAlpha = 1f;

    Drawable mBtnBackGround, mSliderBackGround;
    String leftText, centerText, rightText;
    int leftTextColor, centerTextColor, rightTextColor;
    float leftTextSize, centerTextSize, rightTextSize;
    OnTextClickListener mListener;
    int defaultPosition, currentPosition;
    boolean isThreeTextView;

    TextView leftTextView, centerTextView, rightTextView;
    ImageView slider;
    LayoutParams leftParams, centerParams, rightParams, sliderParams;

    public SlideToggleButton(Context context) {
        super(context);
    }

    public SlideToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlideToggleButton);
        mBtnBackGround = ta.getDrawable(R.styleable.SlideToggleButton_btnBackground);
        mSliderBackGround = ta.getDrawable(R.styleable.SlideToggleButton_sliderBackground);
        leftText = ta.getString(R.styleable.SlideToggleButton_leftText);
        leftTextColor = ta.getColor(R.styleable.SlideToggleButton_leftTextColor, 0xffffffff);
        leftTextSize = ta.getDimension(R.styleable.SlideToggleButton_leftTextSize, 7);
        centerText = ta.getString(R.styleable.SlideToggleButton_centerText);
        centerTextColor = ta.getColor(R.styleable.SlideToggleButton_centerTextColor, 0xffffffff);
        centerTextSize = ta.getDimension(R.styleable.SlideToggleButton_centerTextSize, 3);
        rightText = ta.getString(R.styleable.SlideToggleButton_rightText);
        rightTextColor = ta.getColor(R.styleable.SlideToggleButton_rightTextColor, 0xffffffff);
        rightTextSize = ta.getDimension(R.styleable.SlideToggleButton_rightTextSize, 3);
        defaultPosition = ta.getInteger(R.styleable.SlideToggleButton_defaultPosition, POSITION_LEFT);
        isThreeTextView = ta.getBoolean(R.styleable.SlideToggleButton_isThreeTextView, false);
        ta.recycle();

        if (defaultPosition == POSITION_LEFT ||
                defaultPosition == POSITION_CENTER ||
                defaultPosition == POSITION_RIGHT) {
            currentPosition = defaultPosition;
        } else {
            currentPosition = POSITION_LEFT;
        }
        setBackground(mBtnBackGround);
        initUI();
    }

    /**
     * 初始化所有子view
     */
    void initUI() {
        setSliderView();
        setLeftTextView();
        setRightTextView();
        if (isThreeTextView) {
            setCenterTextView();
        }
    }

    void setLeftTextView() {
        leftTextView = new TextView(mContext);
        leftParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
        );
        leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
        leftTextView.setGravity(Gravity.CENTER);
        leftTextView.setText(leftText);
        leftTextView.setTextSize(leftTextSize);
        leftTextView.setTextColor(leftTextColor);
        if (currentPosition == POSITION_LEFT) {
            leftTextView.setAlpha(currentAlpha);
            leftTextView.setTextColor(getResources().getColor(R.color.about_gender_current_color));
        } else {
            leftTextView.setAlpha(otherAlpha);
            leftTextView.setTextColor(getResources().getColor(R.color.about_gender_other_color));
        }
        leftTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null && currentPosition != POSITION_LEFT) {
                    mListener.onLeftTextClick();
                    doSliderAnimation(POSITION_LEFT);
                    leftTextView.setAlpha(currentAlpha);
                    if (rightTextView != null) {
                        rightTextView.setAlpha(otherAlpha);
                    }
                    if (centerTextView != null) {
                        centerTextView.setAlpha(otherAlpha);
                    }
                }
            }
        });
        addView(leftTextView, leftParams);
    }

    void setRightTextView() {
        rightTextView = new TextView(mContext);
        rightParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
        );
        rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
        rightTextView.setGravity(Gravity.CENTER);
        rightTextView.setText(rightText);
        rightTextView.setTextSize(rightTextSize);
        rightTextView.setTextColor(rightTextColor);
        if (currentPosition == POSITION_RIGHT) {
            rightTextView.setAlpha(currentAlpha);
            rightTextView.setTextColor(getResources().getColor(R.color.about_gender_current_color));
        } else {
            rightTextView.setAlpha(otherAlpha);
            rightTextView.setTextColor(getResources().getColor(R.color.about_gender_other_color));
        }
        rightTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null && currentPosition != POSITION_RIGHT) {
                    mListener.onRightTextClick();
                    if (isThreeTextView) {
                        doSliderAnimation(POSITION_RIGHT);
                    } else {
                        doSliderAnimation(POSITION_RIGHT - 1);
                    }
                    rightTextView.setAlpha(currentAlpha);
                    if (leftTextView != null) {
                        leftTextView.setAlpha(otherAlpha);
                    }
                    if (centerTextView != null) {
                        centerTextView.setAlpha(otherAlpha);
                    }
                }
            }
        });
        addView(rightTextView, rightParams);
    }

    void setCenterTextView() {
        centerTextView = new TextView(mContext);
        centerParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
        );
        centerParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
        centerTextView.setGravity(Gravity.CENTER);
        centerTextView.setText(centerText);
        centerTextView.setTextSize(centerTextSize);
        centerTextView.setTextColor(centerTextColor);
        if (currentPosition == POSITION_CENTER) {
            centerTextView.setAlpha(currentAlpha);
            centerTextView.setTextColor(getResources().getColor(R.color.about_gender_current_color));
        } else {
            centerTextView.setAlpha(otherAlpha);
            centerTextView.setTextColor(getResources().getColor(R.color.about_gender_other_color));
        }
        centerTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null && currentPosition != POSITION_CENTER) {
                    mListener.onCenterTextClick();
                    doSliderAnimation(POSITION_CENTER);
                    centerTextView.setAlpha(currentAlpha);
                    if (leftTextView != null) {
                        leftTextView.setAlpha(otherAlpha);
                    }
                    if (rightTextView != null) {
                        rightTextView.setAlpha(otherAlpha);
                    }
                }
            }
        });
        addView(centerTextView, centerParams);
    }

    void setSliderView() {
        slider = new ImageView(mContext);
        slider.setBackground(mSliderBackGround);
        sliderParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
        );
        setSliderPosition(currentPosition);
        addView(slider, sliderParams);
    }

    public void setSliderPosition(int position) {
        doSliderAnimation(position);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        onTextViewItemLayout();
    }

    /**
     * 布局每个子view
     */
    void onTextViewItemLayout() {
        int width = getMeasuredWidth();
        if (isThreeTextView) {
            leftTextView.setWidth(width / 3);
            rightTextView.setWidth(width / 3);
            centerTextView.setWidth(width / 3);
            slider.setMaxWidth(width / 3);
            slider.setMinimumWidth(width / 3);
        } else {
            leftTextView.setWidth(width / 2);
            rightTextView.setWidth(width / 2);
            slider.setMaxWidth(width / 2);
            slider.setMinimumWidth(width / 2);
        }
    }

    /**
     * 根据目标位置，实现slider的平移的动画
     *
     * @param descPos slider目标位置
     */
    void doSliderAnimation(int descPos) {
        if (currentPosition == descPos) {
            return;
        }
        AnimationSet set = new AnimationSet(true);
        set.setFillAfter(true);
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, (float) currentPosition,
                Animation.RELATIVE_TO_SELF, (float) descPos,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        animation.setDuration(200);
        set.addAnimation(animation);
        slider.startAnimation(set);
        currentPosition = descPos;
        if (isThreeTextView) {
            switch (descPos) {
                case POSITION_LEFT:
                    leftTextView.setAlpha(currentAlpha);
                    centerTextView.setAlpha(otherAlpha);
                    rightTextView.setAlpha(otherAlpha);
                    break;
                case POSITION_CENTER:
                    leftTextView.setAlpha(otherAlpha);
                    rightTextView.setAlpha(otherAlpha);
                    centerTextView.setAlpha(currentAlpha);
                    break;
                case POSITION_RIGHT:
                    leftTextView.setAlpha(otherAlpha);
                    centerTextView.setAlpha(otherAlpha);
                    rightTextView.setAlpha(currentAlpha);
                    break;
            }
        } else {
            switch (descPos) {
                case POSITION_LEFT:
                    leftTextView.setAlpha(currentAlpha);
                    rightTextView.setAlpha(otherAlpha);
                    break;
                case POSITION_RIGHT - 1:
                    leftTextView.setAlpha(otherAlpha);
                    rightTextView.setAlpha(currentAlpha);
                    break;
            }
        }

        if (leftTextView != null && centerTextView != null && rightTextView != null) {
            switch (descPos) {
                case 0:
                    leftTextView.setTextColor(getResources().getColor(R.color.about_gender_current_color));
                    centerTextView.setTextColor(getResources().getColor(R.color.about_gender_other_color));
                    rightTextView.setTextColor(getResources().getColor(R.color.about_gender_other_color));
                    break;

                case 1:
                    leftTextView.setTextColor(getResources().getColor(R.color.about_gender_other_color));
                    centerTextView.setTextColor(getResources().getColor(R.color.about_gender_current_color));
                    rightTextView.setTextColor(getResources().getColor(R.color.about_gender_other_color));
                    break;

                case 2:
                    leftTextView.setTextColor(getResources().getColor(R.color.about_gender_other_color));
                    centerTextView.setTextColor(getResources().getColor(R.color.about_gender_other_color));
                    rightTextView.setTextColor(getResources().getColor(R.color.about_gender_current_color));
                    break;
            }
        }
    }

    public interface OnTextClickListener {
        void onLeftTextClick();

        void onRightTextClick();

        void onCenterTextClick();
    }

    public void setOnTextClickListener(OnTextClickListener listener) {
        mListener = listener;
    }

    /**
     * 获取当前slider位置
     *
     * @return
     */
    public int getCurrentPosition() {
        return currentPosition;
    }

    public void doTextClick() {
        if (mListener != null) {
            if (isThreeTextView) {
                if (currentPosition == POSITION_RIGHT) {
                    leftTextView.performClick();
                } else if (currentPosition == POSITION_LEFT) {
                    centerTextView.performClick();
                } else if (currentPosition == POSITION_CENTER) {
                    rightTextView.performClick();
                }
            } else {
                if (currentPosition == POSITION_RIGHT - 1) {
                    leftTextView.performClick();
                } else if (currentPosition == POSITION_LEFT) {
                    rightTextView.performClick();
                }
            }
        }
    }

    /**
     * 设置左边字体颜色
     */
    public void setLeftTextColor(int color) {
        leftTextView.setTextColor(color);
    }

    /**
     * 设置右边字体颜色
     */
    public void setRightTextColor(int color) {
        rightTextView.setTextColor(color);
    }

    /**
     * 设置中间字体颜色
     */
    public void setCenterTextColor(int color) {
        centerTextView.setTextColor(color);
    }
}
