package com.easy.tvbox.ui.daily;

import android.content.Context;

import com.easy.tvbox.base.AbstractObjectPresenter;
import com.easy.tvbox.bean.Daily;

public class DailyGridAdapter extends AbstractObjectPresenter<DailyGridView> {

    public DailyGridAdapter(Context context) {
        super(context);
    }

    @Override
    protected DailyGridView onCreateView() {
        DailyGridView dailyGridView = new DailyGridView(getContext());
        return dailyGridView;
    }

    @Override
    public void onBindViewHolder(Object item, DailyGridView dailyGridView) {
        if (item instanceof Daily) {
            Daily daily = (Daily) item;
            dailyGridView.updateUi(daily);
        }
    }
}
