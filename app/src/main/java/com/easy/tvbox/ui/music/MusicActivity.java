package com.easy.tvbox.ui.music;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

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
                switchType(1);
            }
        });
        mViewBinding.tvMv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchType(2);
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
        mViewBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    musicFragment.choose(true);
                } else {
                    musicFragment.choose(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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

    public void switchType(int currentType) {
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
