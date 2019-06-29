package com.easy.tvbox.ui.daily;

import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.bean.DailyList;
import com.easy.tvbox.ui.home.HomeActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DailyPresenter extends BasePresenter<DailyView> {

    Disposable mDisposable;

    @Inject
    public DailyPresenter() {

    }

    @Override
    public void onAttached() {

    }

    public int playState(DailyList dailyList) {
        if (dailyList != null) {
            long nowTime = System.currentTimeMillis();
            if (nowTime < dailyList.getStartTime()) {
                return -1;
            } else if (nowTime < dailyList.getEndTime()) {
                return 0;
            } else {
                return 1;
            }
        }
        return -1;
    }

    /**
     * 倒计时
     */
    public void downCount() {
        //每10分支更新一次数据
        Observable.interval( 0,1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(Long number) {
                        List<DailyList> dailyDataContent = HomeActivity.dailyDataContent;
                        if (dailyDataContent != null && dailyDataContent.size() > 0) {
                            long currrentTime = getCurrentTime() / 1000;
                            for (DailyList dailyList : dailyDataContent) {
//                                Log.d("downCount", CommonUtils.timeStamp2Date(dailyList.getStartTime(), 0));
                                long second = dailyList.getStartTime()/1000 - currrentTime;
                                long hour = 0, minute = 0;
                                if (second > 0) {
                                    if (second >= 60) {
                                        minute = second / 60;
                                        second = second % 60;
                                        if (minute >= 60) {
                                            hour = minute / 60;
                                            minute = minute % 60;
                                        }
                                    }
                                    dailyList.setDownCount(buildString(hour, "时", minute, "分", second, "秒"));
                                }
                            }
                        }
                        mView.downCountCallback();
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

    public long getCurrentTime() {
//        return CommonUtils.date2TimeStamp("2019-06-23 09:00:00");
        return System.currentTimeMillis();
    }

    public String buildString(Object... str) {
        if (str != null && str.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (Object s : str) {
                builder.append(s);
            }
            return builder.toString();
        }
        return null;
    }

    /**
     * 取消订阅
     */
    public void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
