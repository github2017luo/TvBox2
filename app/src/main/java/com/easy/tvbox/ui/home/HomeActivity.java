package com.easy.tvbox.ui.home;

import android.util.Log;
import android.view.View;

import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.DailyData;
import com.easy.tvbox.bean.DailyList;
import com.easy.tvbox.bean.LiveData;
import com.easy.tvbox.bean.LiveList;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.databinding.HomeBinding;
import com.easy.tvbox.event.DailyUpdateEvent;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.ui.LoadingView;
import com.easy.tvbox.utils.ToastUtils;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

//@Route(path = RouteManager.HOME, name = "首页")
public class HomeActivity extends BaseActivity<HomeBinding> implements HomeView {

    @Inject
    HomePresenter presenter;
    List<String> bannerImages = new ArrayList<>();
    Account account;
    public static List<DailyList> dailyDataContent;
    public static List<LiveList> liveDataContent;

    @Override
    public int getLayoutId() {
        return R.layout.home;
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
    public void onPause() {
        super.onPause();
        mViewBinding.banner.pause();//暂停轮播
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewBinding.banner.start();//开始轮播
    }

    @Override
    public void initView() {
        account = DataManager.getInstance().queryAccount();
        if (account == null) {
            finish();
            return;
        }

        mViewBinding.llLive.setOnClickListener(v -> {
            RouteManager.goLiveActivity(HomeActivity.this);
        });
        mViewBinding.llDaily.setOnClickListener(v -> RouteManager.goDailyActivity(HomeActivity.this));
        mViewBinding.llMusic.setOnClickListener(v -> RouteManager.goMusicActivity(HomeActivity.this));
        mViewBinding.llMine.setOnClickListener(v -> RouteManager.goMineActivity(HomeActivity.this));
        mViewBinding.loadingView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.is3gConnected(HomeActivity.this)) {
                    networkChange(true);
                }
            }
        });
        networkChange(NetworkUtils.is3gConnected(this));

        presenter.timeRequestDailyCourse(account.getShopNo());
        presenter.queryForLive();
    }

    private void initData() {
        presenter.saveEquipment();
        presenter.getCarouselByShopNo(account.getShopNo());
    }

    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect) {
            initData();
            mViewBinding.llContain.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
        } else {
            mViewBinding.llContain.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
        }
    }

    @Override
    public void carouselCallback(Respond<List<String>> respond) {
        if (respond.isOk()) {
            List<String> images = respond.getObj();
            if (images != null && images.size() > 0) {
                bannerImages.clear();
                bannerImages.addAll(respond.getObj());
                mViewBinding.banner.setPages(bannerImages, (MZHolderCreator<BannerViewHolder>) BannerViewHolder::new);
                mViewBinding.banner.setDelayedTime(10000);
                mViewBinding.banner.setIndicatorVisible(true);
            }
        } else {
            ToastUtils.showLong(respond.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        //不能删
    }

    @Override
    public void queryForAudioCallback(Respond<DailyData> respond) {
        if (respond.isOk()) {
            DailyData dailyData = respond.getObj();
            if (dailyData != null) {
                dailyDataContent = dailyData.getContent();
                if (dailyDataContent != null) {
                    Log.d("queryForAudioCallback", dailyDataContent.toString());
                }
            }
        } else {
            ToastUtils.showLong(respond.getMessage());
        }
        EventBus.getDefault().post(new DailyUpdateEvent(1));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.cancel();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDailyUpdateEvent(DailyUpdateEvent event) {
        if (event.type == 0) {
            presenter.queryForAudio(account.getShopNo());
        }
    }

    @Override
    public void liveCallback(Respond<LiveData> respond) {
        if (respond.isOk()) {
            LiveData liveData = respond.getObj();
            if (liveData != null) {
                liveDataContent = liveData.getContent();
                Log.d("liveCallback", liveDataContent.toString());
            }
        } else {
            ToastUtils.showLong(respond.getMessage());
        }
    }
}