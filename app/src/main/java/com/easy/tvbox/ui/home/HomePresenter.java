package com.easy.tvbox.ui.home;

import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.bean.DailyData;
import com.easy.tvbox.bean.DailyList;
import com.easy.tvbox.bean.LiveData;
import com.easy.tvbox.bean.LiveList;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.utils.CommonUtils;

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

    Disposable mDisposable;

    @Inject
    public HomePresenter() {

    }

    @Override
    public void onAttached() {

    }

    /**
     * 定时请求每日课程
     */
    public void timeRequestDailyCourse(String shopNo) {
        //每10分支更新一次数据
        Observable.interval(0, 10, TimeUnit.MINUTES)
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(Long number) {
                        queryForAudio(shopNo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //取消订阅
                        cancel();
                    }

                    @Override
                    public void onComplete() {
                        //取消订阅
                        cancel();
                    }
                });
    }

    /**
     * 取消订阅
     */
    public void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    /**
     * 获取每日音频列表---不需要翻页
     */
    public void queryForAudio(String shopNo) {
        Map<String, Object> map = new HashMap<>();
        map.put("shopNo", shopNo);
        Disposable disposable = requestStore.queryForAudio(0, 10, map)
                .doOnSuccess(respond -> {
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            DailyData dailyData = JSON.parseObject(body, DailyData.class);
                            if (dailyData != null) {
                                List<DailyList> contents = dailyData.getContent();
                                if (contents != null && contents.size() > 0) {
                                    for (DailyList content : contents) {
                                        long startTime = CommonUtils.date2TimeStamp(content.getBeginDate());
                                        long endTime = CommonUtils.date2TimeStamp(content.getEndDate());
                                        content.setStartTime(startTime);
                                        content.setEndTime(endTime);
                                        if (startTime != 0 && endTime != 0) {
                                            String startDate = CommonUtils.timeStamp2Date(startTime, 1);
                                            String endDate = CommonUtils.timeStamp2Date(endTime, 1);
                                            if (startDate != null && endDate != null) {
                                                content.setShowTime(startDate + "-" + endDate);
                                            }
                                        }
                                    }
                                }
                            }
                            respond.setObj(dailyData);
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(respond -> mView.queryForAudioCallback(respond),
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.queryForAudioCallback(respond);
                        });
        mCompositeSubscription.add(disposable);
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
     * 直播列表
     */
    public void queryForLive() {
        Disposable disposable = requestStore.queryForLive(0, 20)
                .doOnSuccess(respond -> {
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            LiveData liveData = JSON.parseObject(body, LiveData.class);
                            if (liveData != null) {
                                List<LiveList> contents = liveData.getContent();
                                if (contents != null && contents.size() > 0) {
                                    long currentTime = System.currentTimeMillis();
                                    for (LiveList content : contents) {
                                        long startTime = CommonUtils.date2TimeStamp(content.getBeginDate());
                                        long endTime = CommonUtils.date2TimeStamp(content.getEndDate());
                                        if (startTime != 0 && endTime != 0) {
                                            if (endTime < currentTime) {
                                                content.setState(0);
                                            } else if (startTime > currentTime) {
                                                content.setState(2);
                                            } else {
                                                content.setState(1);
                                            }
                                            String startDate = CommonUtils.timeStamp2Date(startTime, 0);
                                            String endDate = CommonUtils.timeStamp2Date(startTime, 1);
                                            if (startDate != null && endDate != null) {
                                                content.setShowTime(startDate + "-" + endDate);
                                            }
                                        }
                                    }
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
}
