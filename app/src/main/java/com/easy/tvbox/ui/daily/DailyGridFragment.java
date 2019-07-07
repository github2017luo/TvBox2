package com.easy.tvbox.ui.daily;

import android.app.Activity;
import android.os.Bundle;

import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.VerticalGridPresenter;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.DailyList;
import com.easy.tvbox.event.DailyUpdateEvent;
import com.easy.tvbox.ui.home.HomeActivity;
import com.easy.tvbox.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DailyGridFragment extends VerticalGridSupportFragment {

    private ArrayObjectAdapter mAdapter;
    DailyPresenter presenter;
    List<DailyList> dailyLists = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setupRowAdapter();
    }

    public void setupRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_LARGE, true);
        gridPresenter.setNumberOfColumns(2);
        gridPresenter.setShadowEnabled(true);
        setGridPresenter(gridPresenter);

        mAdapter = new ArrayObjectAdapter(new DailyGridAdapter(getActivity()));
        setAdapter(mAdapter);

        setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder, row) -> {
            if (item instanceof DailyList) {
                DailyList dailyList = (DailyList) item;
                if (presenter != null) {
                    int playState = presenter.playState(dailyList);
                    if (playState == -1) {
                        ToastUtils.showLong("时间未到");
                    } else if (playState == 0) {
                        RouteManager.goDailyVideoActivity(getContext(), JSON.toJSONString(dailyList));
                    } else if (playState == 1) {
                        ToastUtils.showLong("时间已过");
                    }
                }

            }
        });
        refreshView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDailyUpdateEvent(DailyUpdateEvent event) {
        if (event.type == 1) {
            refreshView();
        }
    }

    public void refreshView() {
        List<DailyList> dailyDataContent = HomeActivity.dailyDataContent;
        if (dailyDataContent != null && dailyDataContent.size() > 0) {
            dailyLists.clear();
            dailyLists.addAll(dailyDataContent);
            if (mAdapter != null) {
                prepareEntranceTransition();
                mAdapter.clear();
                mAdapter.addAll(0, dailyLists);
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

    public void setPresenter(DailyPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
