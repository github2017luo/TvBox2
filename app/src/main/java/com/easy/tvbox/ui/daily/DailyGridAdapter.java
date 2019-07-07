package com.easy.tvbox.ui.daily;

import android.content.Context;

import com.easy.tvbox.base.AbstractObjectPresenter;
import com.easy.tvbox.bean.DailyList;

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
        if (item instanceof DailyList) {
            DailyList dailyList = (DailyList) item;
            dailyGridView.updateUi(dailyList);
        }
    }

    public void refreshCountDown(int position){

    }
}
