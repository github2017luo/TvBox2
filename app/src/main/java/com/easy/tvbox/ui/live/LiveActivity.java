package com.easy.tvbox.ui.live;

import android.view.View;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.LiveList;
import com.easy.tvbox.databinding.LiveBinding;
import com.easy.tvbox.event.LiveUpdateEvent;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.ui.LoadingView;
import com.easy.tvbox.ui.home.HomeActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

public class LiveActivity extends BaseActivity<LiveBinding> implements LiveView {

    @Inject
    LivePresenter presenter;

    @Override
    public int getLayoutId() {
        return R.layout.live;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public synchronized void networkChange(boolean isConnect) {
        if (isConnect) {
            EventBus.getDefault().post(new LiveUpdateEvent(0));
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_LOADING);
            mViewBinding.ivNoData.setVisibility(View.GONE);
        } else {
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
            mViewBinding.ivNoData.setVisibility(View.GONE);
        }
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

        mViewBinding.loadingView.setRetryListener(v -> {
            if (NetworkUtils.isNetConnected(LiveActivity.this)) {
                networkChange(true);
            }
        });

        networkChange(NetworkUtils.isNetConnected(LiveActivity.this));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveUpdateEvent(LiveUpdateEvent event) {
        if (event.type == 1) {
            if (HomeActivity.liveDataContent != null && !HomeActivity.liveDataContent.isEmpty()) {
                LiveList startLive = null;
                for (LiveList liveList : HomeActivity.liveDataContent) {
                    if (liveList.getState() != 0 && liveList.getState() != 1) {
                        startLive = liveList;
                        break;
                    }
                }
                if (startLive != null) {
                    RouteManager.goVideoActivity(LiveActivity.this, JSON.toJSONString(startLive));
                    finish();
                    return;
                }
            }
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
            mViewBinding.ivNoData.setVisibility(View.VISIBLE);
        }
    }
}
