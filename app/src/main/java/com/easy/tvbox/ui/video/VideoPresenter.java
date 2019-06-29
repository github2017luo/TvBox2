package com.easy.tvbox.ui.video;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.bean.Respond;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class VideoPresenter extends BasePresenter<VideoView> {

    @Inject
    public VideoPresenter() {

    }

    @Override
    public void onAttached() {

    }

    /**
     * 获取大课播放地址
     */
    public void getLivePlayUrl(String uid) {
        Disposable disposable = requestStore.getLivePlayUrl(uid)
                .doOnSuccess(respond -> {
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            JSONObject jsonObject = JSON.parseObject(body);
                            String playUrl = jsonObject.getString("playUrl");
                            respond.setObj(playUrl);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respond -> mView.livePlayUrlCallback(respond),
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.livePlayUrlCallback(respond);
                        });
        mCompositeSubscription.add(disposable);
    }
}
