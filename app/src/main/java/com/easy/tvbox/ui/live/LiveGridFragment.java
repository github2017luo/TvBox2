package com.easy.tvbox.ui.live;

import android.app.Activity;
import android.os.Bundle;

import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.VerticalGridPresenter;

import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.LiveList;
import com.easy.tvbox.event.LiveUpdateEvent;
import com.easy.tvbox.ui.daily.DailyActivity;
import com.easy.tvbox.ui.daily.DailyGridAdapter;
import com.easy.tvbox.ui.home.HomeActivity;
import com.easy.tvbox.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class LiveGridFragment extends VerticalGridSupportFragment {

    private ArrayObjectAdapter mAdapter;
    LivePresenter presenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setupRowAdapter();
    }

    public void setupRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_XSMALL, true);
        gridPresenter.setNumberOfColumns(2);
        gridPresenter.setShadowEnabled(true);
        setGridPresenter(gridPresenter);

        mAdapter = new ArrayObjectAdapter(new LiveGridAdapter(getActivity()));
        setAdapter(mAdapter);

        setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder, row) -> {
            if (item instanceof LiveList) {
                LiveList liveList = (LiveList) item;
                if (liveList.getState() == 0) {
                    ToastUtils.showLong("直播已结束");
                } else if (liveList.getState() == 1) {
                    ToastUtils.showLong("直播未开始");
                } else {
                    RouteManager.goVideoActivity(getContext(), liveList.getUid());
                }
            }
        });
        refreshView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveUpdateEvent(LiveUpdateEvent event) {
        if (event.type == 1) {
            refreshView();
        }
    }

    public void refreshView() {
        List<LiveList> dailyDataContent = HomeActivity.liveDataContent;
        if (dailyDataContent != null && dailyDataContent.size() > 0) {
            if (mAdapter != null) {
                prepareEntranceTransition();
                mAdapter.clear();
                mAdapter.addAll(0, dailyDataContent);
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

    public void setPresenter(LivePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
