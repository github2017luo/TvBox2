package com.easy.tvbox.ui.daily;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.bean.Daily;
import com.easy.tvbox.bean.DailyItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class DailyPresenter extends BasePresenter<DailyView> {

    @Inject
    public DailyPresenter() {

    }

    @Override
    public void onAttached() {

    }

    public void queryDaily() {
        Disposable disposable = Observable.zip(queryDaily(1), queryDaily(2), (dailyList1, dailyList2) -> {
            List<Daily> dailyList = new ArrayList<>();
            if (dailyList1 != null && dailyList1.size() > 0) {
                Daily daily = new Daily();
                DailyItem dailyItem = dailyList1.get(0);
                daily.setImageUrl(dailyItem.getFaceurl());
                daily.setDailyItems(dailyList1);
                dailyList.add(daily);
            }
            if (dailyList2 != null && dailyList2.size() > 0) {
                Daily daily = new Daily();
                DailyItem dailyItem = dailyList2.get(0);
                daily.setImageUrl(dailyItem.getFaceurl());
                daily.setDailyItems(dailyList2);
                dailyList.add(daily);
            }
            return dailyList;
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(daily -> mView.dailyCallback(daily), throwable ->
                        mView.dailyCallback(null));
        mCompositeSubscription.add(disposable);
    }

    /**
     * 请求每日课程
     * // @param type  1：每日课程 2:直播 3：特殊 4：回放
     *
     * @param period 1：中文 2：蒙语
     */
    private Observable<List<DailyItem>> queryDaily(int period) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("period", period);
        return requestStore.queryForAudio(map)
                .map(respond -> {
                    List<DailyItem> dailyList = new ArrayList<>();
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            JSONObject jsonObject = JSON.parseObject(body);
                            String content = jsonObject.getString("content");
                            return JSON.parseArray(content, DailyItem.class);
                        }
                    }
                    return dailyList;
                }).onErrorReturn(throwable -> new ArrayList<>()).toObservable();
    }
}
