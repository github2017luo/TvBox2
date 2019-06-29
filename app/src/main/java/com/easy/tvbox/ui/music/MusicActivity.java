package com.easy.tvbox.ui.music;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.databinding.MusicBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.ui.LoadingView;

import java.util.List;

import javax.inject.Inject;

public class MusicActivity extends BaseActivity<MusicBinding> implements MusicView {

    @Inject
    MusicPresenter musicPresenter;
    Account account;
    MusicFragment musicFragment, mvFragment;
    int currentType = 1;

    @Override
    public int getLayoutId() {
        return R.layout.music;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void addPresenters(List<BasePresenter> observerList) {
        observerList.add(musicPresenter);
    }

    @Override
    public void initView() {
        account = DataManager.getInstance().queryAccount();
        if (account == null) {
            finish();
            return;
        }
        mViewBinding.loadingView.setRetryListener(v -> {
            if (NetworkUtils.isNetConnected(MusicActivity.this)) {
                networkChange(true);
            }
        });
        mViewBinding.tvMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentType = 1;
                switchType();
            }
        });
        mViewBinding.tvMv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentType = 2;
                switchType();
            }
        });
        musicFragment = MusicFragment.getInstance(1);
        mvFragment = MusicFragment.getInstance(2);
        mViewBinding.viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return position == 0 ? musicFragment : mvFragment;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        networkChange(NetworkUtils.isNetConnected(MusicActivity.this));
    }

    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect) {
            mViewBinding.viewPager.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
        } else {
            mViewBinding.viewPager.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
        }
    }

    public void switchType() {
        if (currentType == 1) {
            mViewBinding.tvMusic.setTextColor(getResources().getColor(R.color.blue));
            mViewBinding.tvMv.setTextColor(getResources().getColor(R.color.white));
            mViewBinding.viewPager.setCurrentItem(0);
        } else {
            mViewBinding.tvMusic.setTextColor(getResources().getColor(R.color.white));
            mViewBinding.tvMv.setTextColor(getResources().getColor(R.color.blue));
            mViewBinding.viewPager.setCurrentItem(1);
        }
    }
}
