package com.easy.tvbox.ui.live;

import android.view.View;

import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.databinding.LiveBinding;
import com.easy.tvbox.event.LiveUpdateEvent;
import com.easy.tvbox.ui.home.HomeActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

public class LiveActivity extends BaseActivity<LiveBinding> implements LiveView {

    @Inject
    LivePresenter presenter;
    LiveAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.live;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void networkChange(boolean isConnect) {

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
        adapter = new LiveAdapter(this, HomeActivity.liveDataContent);
        mViewBinding.gridView.setAdapter(adapter);
        showNoData();
    }

    private void showNoData() {
        if (HomeActivity.liveDataContent == null || HomeActivity.liveDataContent.isEmpty()) {
            mViewBinding.ivNoData.setVisibility(View.VISIBLE);
        } else {
            mViewBinding.ivNoData.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveUpdateEvent(LiveUpdateEvent event) {
        if (event.type == 1) {
            if (adapter != null) {
                adapter.setDatas(HomeActivity.liveDataContent);
            }
        }
    }
}
