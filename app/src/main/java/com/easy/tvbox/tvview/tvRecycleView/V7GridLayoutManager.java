package com.easy.tvbox.tvview.tvRecycleView;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class V7GridLayoutManager extends GridLayoutManager {
    public V7GridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr) {
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
