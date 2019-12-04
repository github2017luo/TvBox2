package com.easy.tvbox.ui.home;

import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.BuildConfig;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.Constant;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.AppVersion;
import com.easy.tvbox.bean.LiveData;
import com.easy.tvbox.bean.LiveList;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.databinding.HomeBinding;
import com.easy.tvbox.event.LiveUpdateEvent;
import com.easy.tvbox.http.ProgressInfo;
import com.easy.tvbox.http.ProgressListener;
import com.easy.tvbox.http.ProgressManager;
import com.easy.tvbox.mqtt.MqttSimple;
import com.easy.tvbox.ui.test.Utils;
import com.easy.tvbox.utils.ToastUtils;
import com.easy.tvbox.view.AppUpdateDialog;
import com.owen.focus.FocusBorder;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class HomeActivity extends BaseActivity<HomeBinding> implements HomeView {

    @Inject
    HomePresenter presenter;
    List<String> bannerImages = new ArrayList<>();
    Account account;
    public static List<LiveList> liveDataContent = new ArrayList<>();
    String downloadPath;
    FocusBorder mFocusBorder;
    MqttSimple mqttSimple;
    AppUpdateDialog dialog;
    public static boolean canInHome = false;

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

    protected void onMoveFocusBorder(View focusedView, float scale) {
        if (null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale));
        }
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
        mFocusBorder = new FocusBorder.Builder()
                .asColor()
                .borderColorRes(R.color.actionbar_color)
                .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 3f)
                .shadowColorRes(R.color.green_bright)
                .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 5f)
                .build(this);

        mViewBinding.rlLive.setOnClickListener(v -> {
            RouteManager.goLiveActivity(HomeActivity.this);
//            RouteManager.goVideoActivity(HomeActivity.this,"http://mobile.hxsoft.net/live/show.m3u8");
            EventBus.getDefault().post(new LiveUpdateEvent(0));
        });

        mViewBinding.rlLive.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));

        mViewBinding.rlDaily.setOnClickListener(v -> RouteManager.goDailyActivity(HomeActivity.this));

        mViewBinding.rlDaily.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));

        mViewBinding.rlMusic.setOnClickListener(v -> RouteManager.goMusicActivity(HomeActivity.this));

        mViewBinding.rlMusic.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));

        mViewBinding.rlMy.setOnClickListener(v -> RouteManager.goMineActivity(HomeActivity.this));

        mViewBinding.rlMy.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));

        ViewPager viewPager = mViewBinding.banner.getViewPager();
        if (viewPager != null) {
            viewPager.setFocusable(false);
        }
        mFocusBorder.setVisible(true);
        onMoveFocusBorder(mViewBinding.rlLive, 1.1f);
        initData();

        String fileName = "tvBox.apk";
        downloadPath = Utils.getSaveFilePath(Constant.TYPE_APP, this) + fileName;
        presenter.timeCheckVersion();
    }


    private void initData() {
//        presenter.saveEquipment();
        presenter.getCarouselByShopNo(account.getShopNo());
        presenter.timeRequestLiveCourse();

        mqttSimple = new MqttSimple(getApplicationContext());
        mqttSimple.connect(account.getShopNo()/*"/BOX/S0001"*/);
    }

    @Override
    public void networkChange(boolean isConnect) {
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
            ToastUtils.showLong("轮播图失败：" + respond.toString());
        }
    }

    @Override
    public void onBackPressed() {
        //不能删
    }

    private ProgressListener getDownloadListener() {
        return new ProgressListener() {
            @Override
            public void onProgress(ProgressInfo progressInfo) {
                int progress = progressInfo.getPercent();
//                Log.d("DownLoad", "--Download-- " + progress + " %  " + progressInfo.getSpeed() + " byte/s  " + progressInfo.toString());
                if (dialog != null) {
                    dialog.setProgress(progress);
                }
                if (progressInfo.isFinish()) {
                    if (dialog != null) {
                        dialog.setProgress(100);
                        dialog.dismiss();
                    }
                    if (downloadPath != null) {
                        Utils.install(mContext, BuildConfig.APPLICATION_ID, downloadPath);
                    }
                }
            }

            @Override
            public void onError(long id, Exception e) {
                Log.d("DownLoad", e.getMessage());
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.liveRequestCancel();
        }
        if (mqttSimple != null) {
            mqttSimple.onDestroy();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveUpdateEvent(LiveUpdateEvent event) {
        if (event.type == 0) {
            presenter.queryLive();
        }
    }

    @Override
    public void liveCallback(Respond<LiveData> respond) {
        if (respond.isOk()) {
            LiveData liveData = respond.getObj();
            if (liveData != null) {
                List<LiveList> temp = liveData.getContent();
                if (temp != null) {
                    liveDataContent = temp;
                    Log.d("liveCallback", liveDataContent.toString());
                }
            }
        }
        EventBus.getDefault().post(new LiveUpdateEvent(1));
    }

    @Override
    public void countDownLive(LiveList liveList) {
        if (liveList != null) {
            RouteManager.goVideoActivity(HomeActivity.this, JSON.toJSONString(liveList));
        }
    }

    @Override
    public void checkUpdateCallback(Respond<AppVersion> respond) {
        if (respond.isOk()) {
            AppVersion appVersion = respond.getObj();
            if (appVersion != null) {
                String currentVersion = BuildConfig.VERSION_NAME.replace(".", "");
                String lastVersion = currentVersion;
                if (!TextUtils.isEmpty(appVersion.getVersion())) {
                    lastVersion = appVersion.getVersion().replace(".", "");
                }
                int cVersion = Utils.formatInt(currentVersion);
                int cVersionName = Utils.formatInt(lastVersion);
                if (Constant.TEST_UPDATE || cVersionName > cVersion) {
                    showAppVersionDialog(appVersion);
                }
            }
        }
    }

    private void showAppVersionDialog(AppVersion appVersion) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new AppUpdateDialog(this);
        dialog.show();
        ProgressManager.getInstance().addResponseListener(appVersion.getDownloadUrl(), getDownloadListener());
        ProgressManager.getInstance().startDownload(downloadPath, appVersion.getDownloadUrl());
    }
}