package com.easy.tvbox.ui.video;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.DailyPlay;
import com.easy.tvbox.bean.Respond;

import java.io.File;
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

    public void downCount(long time) {
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

    public File getDownload(String url) {
        String filePath = DataManager.getInstance().getDownloadPath(getDownloadPath(url));
        if (filePath != null) {
            return new File(filePath);
        }
        return null;
    }
    /**
     * 获取下载文件路径--只有文件地址，不包括token(即问号后都不要)
     *
     * @param url
     * @return
     */
    private String getDownloadPath(String url) {
        if (url != null) {
            int indexUrl = url.indexOf("?");
            if (indexUrl != -1) {
                return url.substring(0, indexUrl);
            }
        }
        return null;
    }
}
