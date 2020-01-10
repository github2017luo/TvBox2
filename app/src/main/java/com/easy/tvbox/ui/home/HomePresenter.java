package com.easy.tvbox.ui.home;

import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.AppVersion;
import com.easy.tvbox.bean.LiveData;
import com.easy.tvbox.bean.LiveList;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.utils.CommonUtils;
import com.easy.tvbox.utils.DimensUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomePresenter extends BasePresenter<HomeView> {

    Disposable liveTimeRequestDisposable;
    Disposable liveCountDownDisposable;
    Disposable timeCheckVersionDisposable;

    @Inject
    public HomePresenter() {

    }

    @Override
    public void onAttached() {

    }

    /**
     * 保存设备信息
     */
    public void saveEquipment() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", Build.SERIAL);
        Disposable disposable = requestStore.saveEquipment(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        mCompositeSubscription.add(disposable);
    }

    /**
     * 首页轮播图
     *
     * @param shopNo
     */
    public void getCarouselByShopNo(String shopNo) {
        Disposable disposable = requestStore.getCarouselByShopNo(shopNo)
                .doOnSuccess(respond -> {
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            try {
                                List<String> account = JSON.parseArray(body, String.class);
                                respond.setObj(account);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respond -> mView.carouselCallback(respond),
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.carouselCallback(respond);
                        });
        mCompositeSubscription.add(disposable);
    }

    /**
     * 定时请求直播
     */
    public void timeRequestLiveCourse() {
        liveRequestCancel();
        //每10分支更新一次数据
        Observable.interval(0, 3, TimeUnit.HOURS)
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        liveTimeRequestDisposable = disposable;
                    }

                    @Override
                    public void onNext(Long number) {
                        queryLive();
                    }

                    @Override
                    public void onError(Throwable e) {
                        //取消订阅
                        liveRequestCancel();
                    }

                    @Override
                    public void onComplete() {
                        //取消订阅
                        liveRequestCancel();
                    }
                });
    }

    /**
     * 取消订阅
     */
    public void liveRequestCancel() {
        if (liveTimeRequestDisposable != null && !liveTimeRequestDisposable.isDisposed()) {
            liveTimeRequestDisposable.dispose();
        }
    }

    /**
     * 直播列表
     */
    public void queryLive() {
        Map<String, Object> map = new HashMap<>();
        Account account = DataManager.getInstance().queryAccount();
        if (account != null) {
            map.put("shopNo", account.getShopNo());
        }
        Disposable disposable = requestStore.queryForLive(0, 20, map)
                .doOnSuccess(respond -> {
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            LiveData liveData = JSON.parseObject(body, LiveData.class);
                            if (liveData != null) {
                                List<LiveList> contents = liveData.getContent();
                                if (contents != null && contents.size() > 0) {
                                    long mix = Long.MAX_VALUE;
                                    LiveList mixLiveList = null;
                                    long currentTime = System.currentTimeMillis();
                                    int[] screens = DimensUtils.getWidthHeight(context);
                                    int width = (screens[0] - DimensUtils.dp2px(context, 90)) / 2;
                                    int height = width / 2 + DimensUtils.dp2px(context, 60);
                                    for (LiveList content : contents) {
                                        long startTime = CommonUtils.date2TimeStamp(content.getBeginDate());
                                        long endTime = CommonUtils.date2TimeStamp(content.getEndDate());
                                        content.setStartTime(startTime);
                                        content.setEndTime(endTime);
                                        if (currentTime < startTime && startTime < mix) {
                                            mix = startTime;
                                            mixLiveList = content;
                                        }
                                        if (startTime != 0 && endTime != 0) {
                                            if (endTime < currentTime) {
                                                content.setState(0);
                                            } else if (startTime > currentTime) {
                                                content.setState(1);
                                            } else {
                                                content.setState(2);
                                            }
                                            String startDate = CommonUtils.timeStamp2Date(startTime, 0);
                                            String endDate = CommonUtils.timeStamp2Date(endTime, 1);
                                            if (startDate != null && endDate != null) {
                                                content.setShowTime(startDate + "-" + endDate);
                                            }
                                        }
                                        content.setWidth(width);
                                        content.setHeight(height);
                                    }
                                    countDownLive(mixLiveList);
                                }
                            }
                            respond.setObj(liveData);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respond -> mView.liveCallback(respond),
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.liveCallback(respond);
                        });
        mCompositeSubscription.add(disposable);
    }

    /**
     * 直播开始倒计时
     *
     * @param liveList
     */
    public void countDownLive(LiveList liveList) {
        long time = -1;
        if (liveList != null) {
            time = liveList.getStartTime() - System.currentTimeMillis();
        }
        if (time < 0) {
            return;
        }
        liveCountDownCancel();
        Observable.timer(time, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        liveCountDownDisposable = disposable;
                    }

                    @Override
                    public void onNext(Long number) {
                        mView.countDownLive(liveList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //取消订阅
                        liveCountDownCancel();
                    }

                    @Override
                    public void onComplete() {
                        //取消订阅
                        liveCountDownCancel();
                    }
                });
    }

    /**
     * 取消订阅
     */
    public void liveCountDownCancel() {
        if (liveCountDownDisposable != null && !liveCountDownDisposable.isDisposed()) {
            liveCountDownDisposable.dispose();
        }
    }

    public void timeCheckVersion() {
        //每10分支更新一次数据
        Observable.interval(30, 30, TimeUnit.MINUTES)
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        timeCheckVersionDisposable = disposable;
                    }

                    @Override
                    public void onNext(Long number) {
                        requestVersion();
                    }

                    @Override
                    public void onError(Throwable e) {
                        //取消订阅
                        timeCheckVersionCancel();
                    }

                    @Override
                    public void onComplete() {
                        //取消订阅
                        timeCheckVersionCancel();
                    }
                });
    }


    /**
     * 取消订阅
     */
    public void timeCheckVersionCancel() {
        if (timeCheckVersionDisposable != null && !timeCheckVersionDisposable.isDisposed()) {
            timeCheckVersionDisposable.dispose();
        }
    }

    public void requestVersion() {
        Disposable disposable = requestStore.requestVersion()
                .doOnSuccess(respond -> {
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            try {
                                AppVersion shopList = JSON.parseObject(body, AppVersion.class);
                                respond.setObj(shopList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respond -> {
                            mView.checkUpdateCallback(respond);
                        },
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.checkUpdateCallback(respond);
                        });
        mCompositeSubscription.add(disposable);
    }
}
