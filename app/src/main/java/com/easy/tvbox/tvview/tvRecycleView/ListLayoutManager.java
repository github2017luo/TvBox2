
package com.easy.tvbox.tvview.tvRecycleView;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;


public class ListLayoutManager extends BaseLayoutManager {
    private static final String LOGTAG = "ListLayoutManager";

    public ListLayoutManager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListLayoutManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ListLayoutManager(Context context, Orientation orientation) {
        super(orientation);
    }

    @Override
    public int getLaneCount() {
        return 1;
    }

    @Override
    public void getLaneForPosition(Lanes.LaneInfo outInfo, int position, Direction direction) {
        outInfo.set(0, 0);
    }

    @Override
    protected void moveLayoutToPosition(int position, int offset, RecyclerView.Recycler recycler, RecyclerView.State state) {
        getLanes().reset(offset);
    }

}
