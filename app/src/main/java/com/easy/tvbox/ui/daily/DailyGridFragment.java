package com.easy.tvbox.ui.daily;

import android.app.Activity;
import android.os.Bundle;

import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.VerticalGridPresenter;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Daily;

import java.util.List;


public class DailyGridFragment extends VerticalGridSupportFragment {

    private ArrayObjectAdapter mAdapter;
    List<Daily> dailyList;
    DailyPresenter dailyPresenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupRowAdapter();
    }

    public void setupRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_SMALL, true);
        gridPresenter.setNumberOfColumns(2);
        gridPresenter.setShadowEnabled(true);
        setGridPresenter(gridPresenter);

        mAdapter = new ArrayObjectAdapter(new DailyGridAdapter(getActivity()));
        setAdapter(mAdapter);

        setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder, row) -> {
            if (item instanceof Daily) {
                Daily daily = (Daily) item;
                RouteManager.goDailyVideoActivity(getContext(), JSON.toJSONString(daily));
            }
        });
    }

    private void refreshView() {
        if (dailyList != null && dailyList.size() > 0) {
            if (mAdapter != null && dailyPresenter != null) {
                prepareEntranceTransition();
                mAdapter.clear();
                mAdapter.addAll(0, dailyList);
                startEntranceTransition();
            }
        } else {
            Activity activity = getActivity();
            if (activity instanceof DailyActivity) {
                DailyActivity dailyActivity = (DailyActivity) activity;
                dailyActivity.setNoData();
            }
        }
    }

    void updateData(DailyPresenter dailyPresenter, List<Daily> dailyList) {
        this.dailyList = dailyList;
        this.dailyPresenter = dailyPresenter;
        refreshView();
    }
}
