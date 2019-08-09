package com.easy.tvbox.ui.home;

import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.Constant;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.DailyData;
import com.easy.tvbox.bean.DailyList;
import com.easy.tvbox.bean.DailyPlay;
import com.easy.tvbox.bean.DailyRoll;
import com.easy.tvbox.bean.DownFile;
import com.easy.tvbox.bean.LiveData;
import com.easy.tvbox.bean.LiveList;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.utils.CommonUtils;
import com.easy.tvbox.utils.DimensUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.Url;

public class HomePresenter extends BasePresenter<HomeView> {

    Disposable dailyTimeRequestDisposable;
    Disposable dailyCountDownDisposable;
    Disposable liveTimeRequestDisposable;
    Disposable liveCountDownDisposable;
    Disposable downloadDisposable;
    String downloadPath;

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
                .map(respond -> {
                    DailyData dailyData = new DailyData();
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            dailyData = JSON.parseObject(body, DailyData.class);
                            if (dailyData != null) {
                                List<DailyList> contents = dailyData.getContent();
                                if (contents != null && contents.size() > 0) {
                                    long mix = Long.MAX_VALUE;
                                    DailyList mixDailyList = null;
                                    long currentTime = System.currentTimeMillis();
                                    int[] screens = DimensUtils.getWidthHeight(context);
                                    int width = (screens[0] - DimensUtils.dp2px(context, 90)) / 2;
                                    int height = width / 2 + DimensUtils.dp2px(context, 60);
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
                                        content.setWidth(width);
                                        content.setHeight(height);
                                    }
                                    countDownDaily(mixDailyList);
                                }
                            }
                        }
                    }
                    return dailyData;
                })
                .flatMap((Function<DailyData, ObservableSource<DailyData>>) dailyData -> {
                    List<DailyList> dailyLists = dailyData.getContent();
                    List<Observable<DailyPlay>> observableList = new ArrayList<>();
                    if (dailyLists != null && dailyLists.size() > 0) {
                        for (DailyList dailyList : dailyLists) {
                            String uid = dailyList.getUid();
                            if (!TextUtils.isEmpty(uid)) {
                                Observable<DailyPlay> observable = getTimeAxis(uid);
                                observableList.add(observable);
                            }
                        }
                    }
                    return Observable.zip(observableList, objects -> {
                        List<DailyPlay> dailyPlays = new ArrayList<>();
                        if (objects != null) {
                            for (Object object : objects) {
                                if (object instanceof DailyPlay) {
                                    dailyPlays.add((DailyPlay) object);
                                }
                            }
                            List<DailyList> musicLists = dailyData.getContent();
                            if (!dailyPlays.isEmpty() && musicLists != null && musicLists.size() > 0) {
                                for (DailyList dailyList : musicLists) {
                                    for (DailyPlay dailyPlay : dailyPlays) {
                                        if (dailyPlay.getUid().equals(dailyList.getUid())) {
                                            dailyList.setDailyPlay(dailyPlay);
                                        }
                                    }
                                }
                            }
                        }
                        return dailyData;
                    });
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dailyData -> mView.dailyCallback(dailyData),
                        throwable -> mView.dailyCallback(null));
        mCompositeSubscription.add(disposable);
    }

    public Observable<DailyPlay> getTimeAxis(String uid) {
        return requestStore.getTimeAxis(uid)
                .map(respond -> {
                    DailyPlay dailyPlay = new DailyPlay();
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            dailyPlay = JSON.parseObject(body, DailyPlay.class);
                            if (dailyPlay != null) {
                                dailyPlay.setUid(uid);
                            }
                        }
                    }
                    return dailyPlay;
                })
                .subscribeOn(Schedulers.io())
                .doOnError(throwable -> Log.d("getTimeAxis", throwable.getMessage()))
                .toObservable();
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

    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String dateString = formatter.format(new Date());
        return dateString;
    }

    public String getFilePath() {
        String data = getDate();
        return context.getCacheDir() + File.separator + data;
    }

    public void saveDownloadInfo(List<DailyList> dailyDataContent) {
        if (dailyDataContent == null || dailyDataContent.isEmpty()) {
            return;
        }
        downloadCancel();
        String path = getFilePath();
        downloadPath = DataManager.getInstance().getDownloadFilePath();
        if (downloadPath != null && !downloadPath.equals(path)) {
            File cacheFile = context.getCacheDir();
            if (cacheFile != null && cacheFile.exists()) {
                cacheFile.delete();
            }
            File file = new File(downloadPath);
            if (file.exists()) {
                boolean isOk = file.delete();
                if (isOk) {
                    DataManager.getInstance().removeAllDownLoad();
                }
            }
        }
        downloadPath = path;
        DailyList[] dailyLists = dailyDataContent.toArray(new DailyList[dailyDataContent.size()]);
        //todo
        if (Constant.isTestDownload) {
            List<DailyList> temp = new ArrayList<>();
            temp.add(dailyDataContent.get(0));
            dailyLists = temp.toArray(new DailyList[temp.size()]);
        }
        //todo
        Observable.fromArray(dailyLists)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DailyList>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        downloadDisposable = d;
                    }

                    @Override
                    public void onNext(DailyList dailyList) {
                        Log.d("Download", "保存下载信息：" + dailyList.toString());
                        if (dailyList.getDailyPlay() != null) {
                            saveToSQL(downloadPath, dailyList.getDailyPlay());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        downloadCancel();
                    }

                    @Override
                    public void onComplete() {
                        downloadCancel();
                        mView.saveDownloadInfoCallback();
                    }
                });
    }

    public void saveToSQL(String downloadPath, DailyPlay dailyPlay) {
        if (dailyPlay != null) {
            List<DailyRoll> rolls = dailyPlay.getRoll();
            if (Constant.isTestDownload) {
                addDownloadToSql(downloadPath, rolls.get(0).getSource());
                return;
            }
            if (rolls != null && !rolls.isEmpty()) {
                for (DailyRoll dailyRoll : rolls) {
                    addDownloadToSql(downloadPath, dailyRoll.getSource());
                }
            }
            List<DailyRoll> formals = dailyPlay.getFormal();
            if (formals != null && !formals.isEmpty()) {
                for (DailyRoll dailyRoll : formals) {
                    addDownloadToSql(downloadPath, dailyRoll.getSource());
                }
            }
        }
    }

    public void addDownloadToSql(String filePath, String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }
        String downloadPath = getDownloadPath(downloadUrl);
        DownFile isDownloaded = DataManager.getInstance().isDownloaded(downloadPath);
        if (isDownloaded == null && !TextUtils.isEmpty(downloadUrl)) {
            DownFile downFile = new DownFile();
            downFile.setDownLoadUrl(downloadUrl);
            downFile.setDownLoadPath(downloadPath);
            String fileName = System.currentTimeMillis() + "_" + downloadPath.substring(downloadPath.lastIndexOf("/") + 1);
            downFile.setFilePath(filePath);
            downFile.setFileName(fileName);
            downFile.setPath(filePath + File.separator + fileName);
            DataManager.getInstance().saveDownloadInfo(downFile);
            Log.d("Download", "保存到数据库：" + downFile.toString());
        } else {
            if (!downloadUrl.equals(isDownloaded.getDownLoadUrl())) {
                isDownloaded.setDownLoadUrl(downloadUrl);
                DataManager.getInstance().saveDownloadInfo(isDownloaded);//更新
            }
            Log.d("Download", "已保存到数据库：" + isDownloaded.toString());
        }
    }

    public DownFile getUnDownLoad() {
        return DataManager.getInstance().getUnDownLoad();
    }

    /**
     * 取消订阅
     */
    public void downloadCancel() {
        if (downloadDisposable != null && !downloadDisposable.isDisposed()) {
            downloadDisposable.dispose();
        }
    }

    public void updateDownInfo(String path) {
        if (!TextUtils.isEmpty(path)) {
            DataManager.getInstance().updateDownInfo(path);
        }
    }

    public void updateDailyListDownLoadNum(List<DailyList> dailyDataContent) {
        if (dailyDataContent != null && dailyDataContent.size() > 0) {
            for (DailyList dailyList : dailyDataContent) {
                DailyPlay dailyPlay = dailyList.getDailyPlay();
                if (dailyPlay != null && !dailyPlay.isDownloadFinish()) {
                    List<DailyRoll> all = new ArrayList<>();
                    List<DailyRoll> dailyRolls = dailyPlay.getFormal();
                    if (dailyRolls != null && dailyRolls.size() > 0) {
                        all.addAll(dailyRolls);
                    }
                    List<DailyRoll> rolls = dailyPlay.getRoll();
                    if (rolls != null && rolls.size() > 0) {
                        all.addAll(rolls);
                    }
                    if (all.size() > 0) {
                        List<DownFile> downloaded = DataManager.getInstance().getDownloaded();
                        if (downloaded != null && downloaded.size() > 0) {
                            int num = 0;
                            for (DailyRoll dailyRoll : all) {
                                for (DownFile downFile : downloaded) {
                                    boolean sama = false;
                                    String downloadPath = getDownloadPath(dailyRoll.getSource());
                                    if (downloadPath != null && downloadPath.equals(downFile.getDownLoadPath())) {
                                        num++;
                                        sama = true;
                                    }
                                    Log.d("DownloadUpdate", "===>匹配:" + sama);
                                }
                            }
                            if (num == all.size()) {
                                dailyPlay.setDownloadFinish(true);
                            }
                            dailyPlay.setDownloadPro(num + "/" + all.size());
                        }
                    }
                }
            }
        }
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

    private boolean isTheSame(String url, String downUrl) {
        if (url != null && downUrl != null) {
            url = getDownloadPath(url);
            downUrl = getDownloadPath(downUrl);
            Log.d("DownloadUpdate", "下载路径:" + url);
            Log.d("DownloadUpdate", "sql路径:" + downUrl);
            return url.equals(downUrl);
        }
        return false;
    }
}
