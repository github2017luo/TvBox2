package com.easy.tvbox.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.Constant;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.DailyData;
import com.easy.tvbox.bean.DailyList;
import com.easy.tvbox.bean.DownFile;
import com.easy.tvbox.bean.LiveData;
import com.easy.tvbox.bean.LiveList;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.databinding.HomeBinding;
import com.easy.tvbox.event.DailyUpdateEvent;
import com.easy.tvbox.event.LiveUpdateEvent;
import com.easy.tvbox.http.DownloadHelper;
import com.easy.tvbox.http.DownloadListener;
import com.easy.tvbox.utils.ToastUtils;
import com.owen.focus.FocusBorder;
import com.zhouwei.mzbanner.holder.MZHolderCreator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;

//@Route(path = RouteManager.HOME, name = "首页")
public class HomeActivity extends BaseActivity<HomeBinding> implements HomeView, DownloadListener {

    @Inject
    HomePresenter presenter;
    List<String> bannerImages = new ArrayList<>();
    Account account;
    public static List<DailyList> dailyDataContent = new ArrayList<>();
    public static List<LiveList> liveDataContent = new ArrayList<>();

    FocusBorder mFocusBorder;
    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码
    DownloadHelper mDownloadHelper = new DownloadHelper("http://www.baseurl.com", this);

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

        mViewBinding.rlLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteManager.goLiveActivity(HomeActivity.this);
                EventBus.getDefault().post(new LiveUpdateEvent(0));
            }
        });
        mViewBinding.rlLive.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onMoveFocusBorder(v, 1.1f);
            }
        });
        mViewBinding.rlDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteManager.goDailyActivity(HomeActivity.this);
            }
        });
        mViewBinding.rlDaily.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onMoveFocusBorder(v, 1.1f);
            }
        });
        mViewBinding.rlMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteManager.goMusicActivity(HomeActivity.this);
            }
        });
        mViewBinding.rlMusic.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onMoveFocusBorder(v, 1.1f);
            }
        });
        mViewBinding.rlMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteManager.goMineActivity(HomeActivity.this);
            }
        });
        mViewBinding.rlMy.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onMoveFocusBorder(v, 1.1f);
            }
        });

        ViewPager viewPager = mViewBinding.banner.getViewPager();
        if (viewPager != null) {
            viewPager.setFocusable(false);
        }
        mFocusBorder.setVisible(true);
        onMoveFocusBorder(mViewBinding.rlLive, 1.1f);
        initData();
    }


    private void initData() {
        presenter.saveEquipment();
        presenter.getCarouselByShopNo(account.getShopNo());
        presenter.timeRequestLiveCourse();
        presenter.timeRequestDailyCourse(account.getShopNo());
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
            ToastUtils.showLong(respond.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        //不能删
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDownloadHelper.dispose();
        if (presenter != null) {
            presenter.dailyRequestCancel();
            presenter.dailyCountDownCancel();
            presenter.liveRequestCancel();
            presenter.downloadCancel();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDailyUpdateEvent(DailyUpdateEvent event) {
        if (event.type == 0) {
            presenter.queryDaily(account.getShopNo());
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
        } else {
//            ToastUtils.showLong(respond.getMessage());
        }
        EventBus.getDefault().post(new LiveUpdateEvent(1));
    }

    @Override
    public void dailyCallback(DailyData dailyData) {
        if (dailyData != null) {
            List<DailyList> temp = dailyData.getContent();
            if (temp != null) {
                dailyDataContent = temp;
                preloadImage();
                presenter.saveDownloadInfo(dailyDataContent);
            }
        }
        EventBus.getDefault().post(new DailyUpdateEvent(1));
    }

    private void preloadImage() {
        if (dailyDataContent != null && dailyDataContent.size() > 0) {
            for (DailyList dailyList : dailyDataContent) {
                if (!TextUtils.isEmpty(dailyList.getPosterUrl())) {
                    Glide.with(this)
                            .load(dailyList.getPosterUrl())
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .preload();
                }
                if (dailyList.getDailyPlay() != null && !TextUtils.isEmpty(dailyList.getDailyPlay().getCover())) {
                    Glide.with(this)
                            .load(dailyList.getDailyPlay().getCover())
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .preload();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            if (grantResults.length >= 1) {
                int writeResult = grantResults[0];
                //读写内存权限
//                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
            }
        }
    }

    /**
     * 获取权限
     */
    private boolean getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GET_PERMISSION_REQUEST);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void countDownDaily(DailyList dailyList) {
        if (dailyList != null) {
            RouteManager.goDailyVideoActivity(HomeActivity.this, JSON.toJSONString(dailyList));
        }
    }

    @Override
    public void countDownLive(LiveList liveList) {
        if (liveList != null) {
            RouteManager.goVideoActivity(HomeActivity.this, JSON.toJSONString(liveList));
        }
    }

    @Override
    public void saveDownloadInfoCallback() {
        boolean isOk = getPermissions();
        if (isOk) {
            startDownLoad();
        }
    }

    public void startDownLoad() {
        DownFile downFile = presenter.getUnDownLoad();
        if (downFile != null) {
            mDownloadHelper.downloadFile(downFile.getDownLoadUrl(), downFile.getFilePath(), downFile.getFileName());
        }
    }

    @Override
    public void onStartDownload() {
        Log.d("Download", "onStartDownload");
    }

    @Override
    public void onProgress(int progress) {
        Log.d("Download", "onProgress:" + progress);
    }

    @Override
    public void onFinishDownload(File file) {
        Log.d("Download", "onFinishDownload_file: " + file.getPath());
        presenter.updateDownInfo(file.getPath());
        presenter.updateDailyListDownLoadNum(dailyDataContent);
        if (!Constant.isTestDownload) {
            startDownLoad();
        }
    }

    @Override
    public void onFail(Throwable ex) {
        Log.d("Download", "onFail:" + ex.getMessage());
    }
}