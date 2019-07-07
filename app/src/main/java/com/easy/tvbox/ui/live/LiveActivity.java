package com.easy.tvbox.ui.live;

import android.view.View;

import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.databinding.LiveBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.ui.LoadingView;
import com.easy.tvbox.ui.home.HomeActivity;

import java.util.List;

import javax.inject.Inject;

public class LiveActivity extends BaseActivity<LiveBinding> implements LiveView {

    @Inject
    LivePresenter presenter;
    LiveGridFragment liveGridFragment;

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
        if (isConnect) {
            if (HomeActivity.liveDataContent == null || HomeActivity.liveDataContent.isEmpty()) {
                mViewBinding.ivNoData.setVisibility(View.VISIBLE);
                mViewBinding.cardsFragment.setVisibility(View.GONE);
            } else {
                mViewBinding.ivNoData.setVisibility(View.GONE);
                mViewBinding.cardsFragment.setVisibility(View.VISIBLE);
            }
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
        } else {
            mViewBinding.cardsFragment.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
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
        liveGridFragment = new LiveGridFragment();
        liveGridFragment.setPresenter(presenter);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cardsFragment, liveGridFragment)
                .commit();

        mViewBinding.loadingView.setRetryListener(v -> {
            if (NetworkUtils.isNetConnected(LiveActivity.this)) {
                networkChange(true);
            }
        });

        networkChange(NetworkUtils.isNetConnected(LiveActivity.this));
    }
}
