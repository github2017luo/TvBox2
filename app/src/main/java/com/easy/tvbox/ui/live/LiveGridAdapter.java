package com.easy.tvbox.ui.live;

import android.content.Context;

import com.easy.tvbox.base.AbstractObjectPresenter;
import com.easy.tvbox.bean.LiveList;

public class LiveGridAdapter extends AbstractObjectPresenter<LiveGridView> {

    public LiveGridAdapter(Context context) {
        super(context);
    }

    @Override
    protected LiveGridView onCreateView() {
        return new LiveGridView(getContext());
    }

    @Override
    public void onBindViewHolder(Object item, LiveGridView dailyGridView) {
        if (item instanceof LiveList) {
            LiveList liveList = (LiveList) item;
            dailyGridView.updateUi(liveList);
        }
    }
}
