package com.easy.tvbox.ui.album;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.bean.Album;
import com.easy.tvbox.bean.Respond;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class AlbumPresenter extends BasePresenter<AlbumView> {

    @Inject
    public AlbumPresenter() {

    }

    @Override
    public void onAttached() {

    }

    public void querySongSheet(String shopNo) {
        Map<String, Object> map = new HashMap<>();
        map.put("shopNo", shopNo);
        map.put("status", "1");
        Disposable disposable = requestStore.querySongSheet(map)
                .doOnSuccess(respond -> {
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            try {
                                Album album = JSON.parseObject(body, Album.class);
                                respond.setObj(album);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respond -> mView.querySongSheetCallback(respond),
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.querySongSheetCallback(respond);
                        });
        mCompositeSubscription.add(disposable);
    }
}
