package com.easy.tvbox.ui.video;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.bean.DailyPlay;
import com.easy.tvbox.bean.Respond;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class DailyVideoPresenter extends BasePresenter<DailyVideoView> {
    Disposable mDisposable;

    @Inject
    public DailyVideoPresenter() {

    }

    @Override
    public void onAttached() {

    }

    /**
     * 获取大课播放地址
     */
    public void getTimeAxis(String uid) {
        Disposable disposable = requestStore.getTimeAxis(uid)
                .doOnSuccess(respond -> {
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            Log.d("getTimeAxis", body);
                            DailyPlay dailyPlay = JSON.parseObject(body, DailyPlay.class);
                            respond.setObj(dailyPlay);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respond -> mView.dailyPlayUrlCallback(respond),
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.dailyPlayUrlCallback(respond);
                        });
        mCompositeSubscription.add(disposable);
    }

    public void downCount(long time) {
        //每10分支更新一次数据
        Observable.timer(time, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(Long number) {
                        mView.playFormalVideo();
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
}