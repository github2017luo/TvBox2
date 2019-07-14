package com.easy.tvbox.tvview.tvRecycleView;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by owen on 2017/7/4.
 */

public class V7StaggeredGridLayoutManager extends StaggeredGridLayoutManager {
    public V7StaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
        if(parent instanceof TvRecyclerView) {
            return parent.requestChildRectangleOnScreen(child, rect, immediate);
        }
        return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
    }
}
