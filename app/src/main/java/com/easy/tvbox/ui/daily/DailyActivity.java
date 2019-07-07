package com.easy.tvbox.ui.daily;

import android.view.View;

import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.databinding.DailyBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.ui.LoadingView;

import java.util.List;

import javax.inject.Inject;

public class DailyActivity extends BaseActivity<DailyBinding> implements DailyView {

    @Inject
    DailyPresenter presenter;
    DailyGridFragment dailyGridFragment;

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
        dailyGridFragment = new DailyGridFragment();
        dailyGridFragment.setPresenter(presenter);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cardsFragment, dailyGridFragment)
                .commit();

        mViewBinding.loadingView.setRetryListener(v -> {
            if (NetworkUtils.isNetConnected(DailyActivity.this)) {
                networkChange(true);
            }
        });
        networkChange(NetworkUtils.isNetConnected(DailyActivity.this));
        presenter.downCount();
    }

    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect) {
            mViewBinding.cardsFragment.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
        } else {
            mViewBinding.cardsFragment.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
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
        if (dailyGridFragment != null) {
            dailyGridFragment.refreshCountdown();
        }
    }

    public void setNoData() {
        mViewBinding.loadingView.setStatus(LoadingView.STATUS_NODATA);
        mViewBinding.cardsFragment.setVisibility(View.GONE);
    }
}
