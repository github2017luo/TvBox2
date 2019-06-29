package com.easy.tvbox.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class ItemFocusView extends View {

    /**
     * 焦点高亮助手
     */
    private FocusHighlightHandler mFocusHighLight = null;

    /**
     * constructor
     *
     * @param context context
     */
    public ItemFocusView(Context context) {
        this(context, null);
    }

    /**
     * constructor
     *
     * @param context context
     * @param attrs   attrs
     */
    public ItemFocusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * constructor
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public ItemFocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * View焦点发生变化
     *
     * @param gainFocus             true:获得焦点;false:失去焦点
     * @param direction             方向
     * @param previouslyFocusedRect previouslyFocusedRect
     */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        //view焦点发生变化的时候,回调焦点高亮助手
        if (null != mFocusHighLight) {
            mFocusHighLight.onItemFocused(this, gainFocus);
        }
    }

    /**
     * view依附到窗口
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFocusHighLight = new FocusScaleHelper.ItemFocusScale(
                FocusHighlight.ZOOM_FACTOR_SMALL);
        mFocusHighLight.onInitializeView(this);
    }
}
