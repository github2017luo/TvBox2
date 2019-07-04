package com.easy.tvbox.ui.home;

import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
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

    Disposable dailyTimeRequestDisposable;
    Disposable dailyCountDownDisposable;
    Disposable liveTimeRequestDisposable;
    Disposable liveCountDownDisposable;

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
        dailyRequestCancel();
        //每10分支更新一次数据
        Observable.interval(0, 10, TimeUnit.MINUTES)
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        dailyTimeRequestDisposable = disposable;
                    }

                    @Override
                    public void onNext(Long number) {
                        queryDaily(shopNo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //取消订阅
                        dailyRequestCancel();
                    }

                    @Override
                    public void onComplete() {
                        //取消订阅
                        dailyRequestCancel();
                    }
                });
    }

    /**
     * 取消订阅
     */
    public void dailyRequestCancel() {
        if (dailyTimeRequestDisposable != null && !dailyTimeRequestDisposable.isDisposed()) {
            dailyTimeRequestDisposable.dispose();
        }
    }

    /**
     * 获取每日音频列表---不需要翻页
     */
    public void queryDaily(String shopNo) {
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
                                    long mix = Long.MAX_VALUE;
                                    DailyList mixDailyList = null;
                                    long currentTime = System.currentTimeMillis();
                                    for (DailyList content : contents) {
                                        long startTime = CommonUtils.date2TimeStamp(content.getBeginDate());
                                        long endTime = CommonUtils.date2TimeStamp(content.getEndDate());
                                        content.setStartTime(startTime);
                                        content.setEndTime(endTime);
                                        if (currentTime < startTime && startTime < mix) {
                                            mix = startTime;
                                            mixDailyList = content;
                                        }
                                        if (startTime != 0 && endTime != 0) {
                                            String startDate = CommonUtils.timeStamp2Date(startTime, 1);
                                            String endDate = CommonUtils.timeStamp2Date(endTime, 1);
                                            if (startDate != null && endDate != null) {
                                                content.setShowTime(startDate + "-" + endDate);
                                            }
                                        }
                                    }
                                    countDownDaily(mixDailyList);
                                }
                            }
                            respond.setObj(dailyData);
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(respond -> mView.dailyCallback(respond),
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.dailyCallback(respond);
                        });
        mCompositeSubscription.add(disposable);
    }

    /**
     * 每日课程开始倒计时
     *
     * @param dailyList
     */
    public void countDownDaily(DailyList dailyList) {
        long time = -1;
        if (dailyList != null) {
            time = dailyList.getStartTime() - System.currentTimeMillis();
        }
        if (time < 0) {
            return;
        }
        dailyCountDownCancel();
        Observable.timer(time, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        dailyCountDownDisposable = disposable;
                    }

                    @Override
                    public void onNext(Long number) {
                        mView.countDownDaily(dailyList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //取消订阅
                        dailyCountDownCancel();
                    }

                    @Override
                    public void onComplete() {
                        //取消订阅
                        dailyCountDownCancel();
                    }
                });
    }

    /**
     * 取消订阅
     */
    public void dailyCountDownCancel() {
        if (dailyCountDownDisposable != null && !dailyCountDownDisposable.isDisposed()) {
            dailyCountDownDisposable.dispose();
        }
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
     * 每日课程开始倒计时
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
                .subscribeOn(Schedulers.newThread())
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
}
