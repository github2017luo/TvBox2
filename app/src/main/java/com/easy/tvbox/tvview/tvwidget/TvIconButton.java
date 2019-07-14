package com.easy.tvbox.tvview.tvwidget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easy.tvbox.R;

/**
 *
 * @author owen
 * @date 16/9/27
 */
public class TvIconButton extends FrameLayout {
    private TextView mTextView;
    private ImageView mImageView;

    private ValueAnimator mAnimator1;
    private ValueAnimator mAnimator2;
    private int mTextWidth = 0;
    private int mWidth;
    private int mMoveOffset;
    
    public TvIconButton(Context context) {
        this(context, null);
    }

    public TvIconButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvIconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTextView = new TextView(context);
        mTextView.setSingleLine(true);
        mImageView = new ImageView(context);
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LayoutParams params1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LayoutParams params2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params1.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        params2.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TvIconButton, defStyleAttr, 0);
        if(a != null) {
            mTextView.setText(a.getText(R.styleable.TvIconButton_tv_text));
            ColorStateList colorStateList = a.getColorStateList(R.styleable.TvIconButton_tv_textColor);
            if(null != colorStateList) {
                mTextView.setTextColor(colorStateList);
            }
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimensionPixelOffset(R.styleable.TvIconButton_tv_textSize, 20));
            final int padding = a.getDimensionPixelOffset(R.styleable.TvIconButton_tv_textPadding, 0);
            mTextView.setPadding(padding, padding, padding, padding);
            int textBg = a.getResourceId(R.styleable.TvIconButton_tv_textBg, 0);
            if(textBg != 0) {
                mTextView.setBackgroundResource(textBg);
            }
            mMoveOffset = a.getDimensionPixelOffset(R.styleable.TvIconButton_tv_iconMoveOffset, 0);
            mImageView.setImageResource(a.getResourceId(R.styleable.TvIconButton_tv_icon, 0));
            params2.width = a.getDimensionPixelOffset(R.styleable.TvIconButton_tv_iconWidth, LayoutParams.WRAP_CONTENT);
            params2.height = a.getDimensionPixelOffset(R.styleable.TvIconButton_tv_iconHeight, LayoutParams.WRAP_CONTENT);
            a.recycle();
        }

        addView(mTextView, params1);
        addView(mImageView, params2);

    }

    private void initAnima() {
        mWidth = getWidth();
        mTextWidth = mTextView.getWidth();
        mMoveOffset = Math.max(mMoveOffset, mTextWidth);
        mAnimator1 = ObjectAnimator.ofInt(this, "width", mWidth, mWidth + mMoveOffset).setDuration(300);
        mAnimator2 = ObjectAnimator.ofInt(mWidth + mMoveOffset, mWidth).setDuration(300);
        mAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final int width = (int) valueAnimator.getAnimatedValue();
                if(width < getWidth()) {
                    setWidth(width);
                }
            }
        });
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if(null == mAnimator2 || null == mAnimator1) {
            initAnima();
        }
        if(gainFocus) {
            if(mAnimator2.isRunning()) {
                mAnimator2.cancel();
            }
            mAnimator1.start();
        } else {
            if(mAnimator1.isRunning()) {
                mAnimator1.cancel();
            }
            mAnimator2.start();
        }
    }

    public void setWidth(int width) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        setLayoutParams(params);
    }

}
