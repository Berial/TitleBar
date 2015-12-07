package berial.ml.titlebar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * 标题栏
 * Created by Berial on 15/10/26.
 */
public class TitleBar extends View {

    private static final String SUPER_STATE = "superState";
    private static final String LEFT_TEXT = "leftText";
    private static final String RIGHT_TEXT = "rightText";
    private static final String TITLE = "title";
    private static final String TEXT_COLOR = "textColor";
    private static final String SUB_TEXT_SIZE = "subTextSize";
    private static final String TITLE_SIZE = "titleSize";
    private static final String LEFT_ICON = "leftIcon";
    private static final String RIGHT_ICON = "rightIcon";
    private static final String HIDE_LEFT_ICON = "hideLeftIcon";
    private static final String HIDE_RIGHT_ICON = "hideRightIcon";
    private static final String HIDE_LEFT_TEXT = "hideLeftText";
    private static final String HIDE_RIGHT_TEXT = "hideRightText";

    private Resources mResources;

    private String mLeftText, mRightText;

    private Paint mPaint;

    private String mTitleText;

    private Drawable mLeftIcon, mRightIcon;

    private int mLeftIconRes, mRightIconRes;

    private int mIconPadding;

    private float mSubTextSize, mTitleSize;

    private int mTextColor;

    private OnClickListener mLeftListener, mRightListener;

    private float mLeftClickableArea, mRightClickableArea;

    private boolean mHideLeftIcon, mHideRightIcon, mHideLeftText, mHideRightText;

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);

        mLeftText = t.getString(R.styleable.TitleBar_tbLeftText);
        mRightText = t.getString(R.styleable.TitleBar_tbRightText);

        mTitleText = t.getString(R.styleable.TitleBar_tbTitle);

        mResources = getResources();

        mLeftIconRes = t.getResourceId(R.styleable.TitleBar_tbLeftIcon, 0);
        mRightIconRes = t.getResourceId(R.styleable.TitleBar_tbRightIcon, 0);

        DisplayMetrics dm = mResources.getDisplayMetrics();

        float d16 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, dm);
        float d18 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, dm);

        mSubTextSize = t.getDimension(R.styleable.TitleBar_tbSubTextSize, d16);
        mTitleSize = t.getDimension(R.styleable.TitleBar_tbTitleSize, d18);

        mIconPadding = t.getDimensionPixelSize(R.styleable.TitleBar_tbIconPadding, 0);

        mTextColor = t.getColor(R.styleable.TitleBar_tbTextColor, Color.WHITE);

        t.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        setClickable(true);
    }

    public TitleBar(Context context) {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int leftPadding = getPaddingLeft();
        int rightPadding = getPaddingRight();

        int leftIconWidth = 0;
        int rightIconWidth = 0;

        if (mLeftIconRes != 0) {
            mLeftIcon = mResources.getDrawable(mLeftIconRes);
        }

        if (mLeftIcon != null && !mHideLeftIcon) {
            leftIconWidth = mLeftIcon.getIntrinsicWidth();
            int h = mLeftIcon.getIntrinsicHeight();

            mLeftIcon.setBounds(leftPadding, getHeight() / 2 - h / 2, leftIconWidth + leftPadding,
                    getHeight() / 2 + h / 2);
            mLeftIcon.draw(canvas);

            mLeftClickableArea = leftPadding + mIconPadding + leftIconWidth;
        }

        if (mRightIconRes != 0) {
            mRightIcon = mResources.getDrawable(mRightIconRes);
        }

        if (mRightIcon != null && !mHideRightIcon) {
            rightIconWidth = mRightIcon.getIntrinsicWidth();
            int h = mRightIcon.getIntrinsicHeight();

            mRightIcon.setBounds(getWidth() - rightIconWidth - rightPadding, getHeight() / 2 - h / 2,
                    getWidth() - rightPadding, getHeight() / 2 + h / 2);
            mRightIcon.draw(canvas);

            mRightClickableArea = rightPadding + mIconPadding + rightIconWidth;
        }

        if (!TextUtils.isEmpty(mLeftText) && !mHideLeftText) {
            int left = leftPadding + mIconPadding + leftIconWidth;

            mPaint.setColor(mTextColor);
            mPaint.setTextSize(mSubTextSize);
            mPaint.setTextAlign(Paint.Align.LEFT);
            Paint.FontMetrics fm = mPaint.getFontMetrics();

            mLeftClickableArea = left + mPaint.measureText(mLeftText);

            float baseline = (getHeight() - fm.ascent - fm.descent) / 2;

            canvas.drawText(mLeftText, left, baseline, mPaint);
        } else {
            mLeftClickableArea += 30; // 增加30px的可触发面积
        }

        if (!TextUtils.isEmpty(mRightText) && !mHideRightText) {
            int right = rightPadding + mIconPadding + rightIconWidth;

            mPaint.setColor(mTextColor);
            mPaint.setTextSize(mSubTextSize);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            Paint.FontMetrics fm = mPaint.getFontMetrics();

            mRightClickableArea = right + mPaint.measureText(mRightText);

            float baseline = (getHeight() - fm.ascent - fm.descent) / 2;

            canvas.drawText(mRightText, getWidth() - right, baseline, mPaint);
        } else {
            mRightClickableArea += 30; // 增加30px的可触发面积
        }

        if (!TextUtils.isEmpty(mTitleText)) {
            mPaint.setColor(mTextColor);
            mPaint.setTextSize(mTitleSize);
            mPaint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fm = mPaint.getFontMetrics();

            float baseline = (getHeight() - fm.ascent - fm.descent) / 2;

            canvas.drawText(mTitleText, getWidth() / 2, baseline, mPaint);
        }
    }

    /**
     * 设置点击事件
     *
     * @param left  左侧文字的点击事件
     * @param right 右侧文字的点击事件
     */
    public void setOnClickListener(OnClickListener left, OnClickListener right) {
        mLeftListener = left;
        mRightListener = right;
    }

    /**
     * 设置返回键监听(即左侧按钮)
     */
    public void setBackClickListener(OnClickListener onClickListener) {
        mLeftListener = onClickListener;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        mTitleText = title;
        invalidate();
    }

    /**
     * 设置标题
     *
     * @param titleRes 标题id
     */
    public void setTitle(@StringRes int titleRes) {
        setTitle(mResources.getString(titleRes));
    }

    public void setLeftText(String leftText) {
        mLeftText = leftText;
        invalidate();
    }

    public void setLeftText(@StringRes int leftTextRes) {
        setLeftText(mResources.getString(leftTextRes));
    }

    public void setRightText(String rightText) {
        mRightText = rightText;
        invalidate();
    }

    public void setRightText(@StringRes int rightTextRes) {
        setRightText(mResources.getString(rightTextRes));
    }

    /**
     * 设置左侧图标资源
     *
     * @param leftIconRes 图标资源id
     */
    public void setLeftIcon(@DrawableRes int leftIconRes) {
        mLeftIconRes = leftIconRes;
        invalidate();
    }

    public void setRightIcon(@DrawableRes int rightIconRes) {
        mRightIconRes = rightIconRes;
        invalidate();
    }

    /**
     * 设置两侧的文字大小
     *
     * @param size 文字大小
     */
    public void setSubTextSize(float size) {
        mSubTextSize = size;
        invalidate();
    }

    public void setTitleTextSize(float size) {
        mTitleSize = size;
        invalidate();
    }

    public void setTextColor(@ColorRes int color) {
        mTextColor = mResources.getColor(color);
        invalidate();
    }

    public void hideLeftText(boolean hidden) {
        mHideLeftText = hidden;
        invalidate();
    }

    public void hideRightText(boolean hidden) {
        mHideLeftIcon = hidden;
        invalidate();
    }

    public void hideLeft(boolean hidden) {
        mHideLeftText = mHideLeftIcon = hidden;
        invalidate();
    }

    public void hideLeftIcon(boolean hidden) {
        mHideLeftText = hidden;
        invalidate();
    }

    public void hideRightIcon(boolean hidden) {
        mHideRightIcon = hidden;
        invalidate();
    }

    public void hideRight(boolean hidden) {
        mHideRightText = mHideRightIcon = hidden;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                if (x < mLeftClickableArea && mLeftListener != null) {
                    //触发左侧点击的区域
                    mLeftListener.onClick(this);
                } else if (x > getWidth() - mRightClickableArea && mRightListener != null) {
                    //触发右侧点击的区域
                    mRightListener.onClick(this);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_STATE, superState);

        bundle.putString(LEFT_TEXT, mLeftText);
        bundle.putString(RIGHT_TEXT, mRightText);
        bundle.putString(TITLE, mTitleText);
        bundle.putFloat(SUB_TEXT_SIZE, mSubTextSize);
        bundle.putFloat(TITLE_SIZE, mTitleSize);
        bundle.putInt(TEXT_COLOR, mTextColor);
        bundle.putInt(LEFT_ICON, mLeftIconRes);
        bundle.putInt(RIGHT_ICON, mRightIconRes);
        bundle.putBoolean(HIDE_LEFT_ICON, mHideLeftIcon);
        bundle.putBoolean(HIDE_RIGHT_ICON, mHideRightIcon);
        bundle.putBoolean(HIDE_LEFT_TEXT, mHideLeftText);
        bundle.putBoolean(HIDE_RIGHT_TEXT, mHideRightText);

        return bundle;
    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            mLeftText = bundle.getString(LEFT_TEXT);
            mRightText = bundle.getString(RIGHT_TEXT);
            mTitleText = bundle.getString(TITLE);
            mSubTextSize = bundle.getFloat(SUB_TEXT_SIZE);
            mTitleSize = bundle.getFloat(TITLE_SIZE);
            mTextColor = bundle.getInt(TEXT_COLOR);
            mLeftIconRes = bundle.getInt(LEFT_ICON);
            mRightIconRes = bundle.getInt(RIGHT_ICON);
            mHideLeftIcon = bundle.getBoolean(HIDE_LEFT_ICON);
            mHideRightIcon = bundle.getBoolean(HIDE_RIGHT_ICON);
            mHideLeftText = bundle.getBoolean(HIDE_LEFT_TEXT);
            mHideRightText = bundle.getBoolean(HIDE_RIGHT_TEXT);

            super.onRestoreInstanceState(bundle.getParcelable(SUPER_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

}
