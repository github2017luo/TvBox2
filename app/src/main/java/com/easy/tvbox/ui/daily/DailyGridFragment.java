package com.easy.tvbox.ui.daily;

import android.os.Bundle;

import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.VerticalGridPresenter;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.R;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Daily;
import com.easy.tvbox.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


public class DailyGridFragment extends VerticalGridSupportFragment {

    private ArrayObjectAdapter mAdapter;
    List<Daily> dailyList = new ArrayList<>();

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
                if(daily.getDailyItems()==null){
                    ToastUtils.showShort("暂无数据");
                    return;
                }
                RouteManager.goDailyVideoActivity(getContext(), JSON.toJSONString(daily));
            }
        });
        refreshView();
    }

    private void refreshView() {
        Daily han = new Daily();
        han.setImageResource(R.drawable.han);
        dailyList.add(han);

        Daily meng = new Daily();
        meng.setImageResource(R.drawable.meng);
        dailyList.add(meng);

        if (mAdapter != null) {
            prepareEntranceTransition();
            mAdapter.clear();
            mAdapter.addAll(0, dailyList);
            startEntranceTransition();
        }
    }

    void updateData(List<Daily> newData) {
        if (newData != null && newData.size() > 0) {
            for (Daily temp : newData) {
                for (Daily daily : dailyList) {
                    if (daily.getImageResource() == temp.getImageResource()) {
                        daily.setDailyItems(temp.getDailyItems());
                    }
                }
            }
            mAdapter.notifyAll();
        }
    }
}
