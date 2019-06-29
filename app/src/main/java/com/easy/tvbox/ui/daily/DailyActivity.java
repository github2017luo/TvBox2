package com.easy.tvbox.ui.daily;

import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.DailyList;
import com.easy.tvbox.databinding.DailyBinding;
import com.easy.tvbox.event.DailyUpdateEvent;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.ui.LoadingView;
import com.easy.tvbox.ui.home.HomeActivity;
import com.easy.tvbox.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DailyActivity extends BaseActivity<DailyBinding> implements DailyView {

    @Inject
    DailyPresenter presenter;
    DailyAdapter adapter;
    List<DailyList> dailyLists = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.daily;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void addPresenters(List<BasePresenter> observerList) {
        observerList.add(presenter);
    }

    @Override
    public void initView() {
        Account account = DataManager.getInstance().queryAccount();
        if (account == null) {
            finish();
            return;
        }
        adapter = new DailyAdapter(this, dailyLists);
        mViewBinding.gridView.setAdapter(adapter);
        mViewBinding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < dailyLists.size()) {
                    DailyList dailyList = dailyLists.get(position);
                    int playState = presenter.playState(dailyList);
                    if (playState == -1) {
                        ToastUtils.showLong("时间未到");
                    } else if (playState == 0) {
                        RouteManager.goDailyVideoActivity(DailyActivity.this, JSON.toJSONString(dailyList));
                    } else if (playState == 1) {
                        ToastUtils.showLong("时间已过");
                    }
                }
            }
        });

        mViewBinding.loadingView.setRetryListener(v -> {
            if (NetworkUtils.isNetConnected(DailyActivity.this)) {
                networkChange(true);
            }
        });

        refreshView();
        networkChange(NetworkUtils.isNetConnected(DailyActivity.this));
        presenter.downCount();
    }

    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect) {
            mViewBinding.gridView.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
        } else {
            mViewBinding.gridView.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDailyUpdateEvent(DailyUpdateEvent event) {
        if (event.type == 1) {
            refreshView();
        }
    }

    private void refreshView() {
        List<DailyList> dailyDataContent = HomeActivity.dailyDataContent;
        if (dailyDataContent != null && dailyDataContent.size() > 0) {
            dailyLists.clear();
            dailyLists.addAll(dailyDataContent);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        } else {
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NODATA);
            mViewBinding.gridView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.cancel();
        }
    }

    @Override
    public void downCountCallback() {
        refreshView();
    }
}
