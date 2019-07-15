package com.easy.tvbox.ui.album;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.bean.Album;
import com.easy.tvbox.bean.AlbumList;
import com.easy.tvbox.bean.MusicData;
import com.easy.tvbox.bean.MusicInfo;
import com.easy.tvbox.bean.MusicList;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.utils.DimensUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class AlbumListPresenter extends BasePresenter<AlbumListView> {

    @Inject
    public AlbumListPresenter() {

    }

    @Override
    public void onAttached() {

    }
    /**
     * 获取歌单里音乐列表
     */
    public void querySongSheetMusic(String uid) {
        Map<String, Object> map = new HashMap<>();
        map.put("songSheetUid", uid);
        Disposable disposable = requestStore.querySongSheetMusic(map)
                .map(respond -> {
                    MusicData musicData = new MusicData();
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            musicData = JSON.parseObject(body, MusicData.class);
                            List<MusicList> musicLists = musicData.getContent();
                            if (musicLists != null && musicLists.size() > 0) {
                                for (MusicList musicList : musicLists) {
                                    long time = musicList.getDuration();
                                    if (time != 0) {
                                        StringBuilder builder = new StringBuilder();
                                        long hours = time / 3600;
                                        if (hours > 0) {
                                            builder.append(hours).append(":");
                                        }
                                        long minutes = time / 60;
                                        if (minutes > 0) {
                                            builder.append(minutes).append(":");
                                        }
                                        long seconds = time % 60;
                                        if (seconds > 0) {
                                            builder.append(seconds);
                                        }
                                        musicList.setDurationStr(builder.toString());
                                    }
                                }
                            }
                        }
                    }
                    return musicData;
                }).flatMap((Function<MusicData, ObservableSource<MusicData>>) musicData -> {
                    List<MusicList> musicLists = musicData.getContent();
                    List<Observable<MusicInfo>> observableList = new ArrayList<>();
                    if (musicLists != null && musicLists.size() > 0) {
                        for (MusicList musicList : musicLists) {
                            String musicId = musicList.getMusicId();
                            if (!TextUtils.isEmpty(musicId)) {
                                Observable<MusicInfo> observable = getPlayUrl(musicList.getUid(), musicList.getMusicId());
                                observableList.add(observable);
                            } else if (!TextUtils.isEmpty(musicList.getVideoId())) {
                                Observable<MusicInfo> observable = getPlayUrl(musicList.getUid(), musicList.getVideoId());
                                observableList.add(observable);
                            }
                        }
                    }
                    return Observable.zip(observableList, objects -> {
                        List<MusicInfo> musicInfos = new ArrayList<>();
                        if (objects != null) {
                            for (Object object : objects) {
                                if (object instanceof MusicInfo) {
                                    musicInfos.add((MusicInfo) object);
                                }
                            }
                            List<MusicList> musicLists1 = musicData.getContent();
                            if (!musicInfos.isEmpty() && musicLists1 != null && musicLists1.size() > 0) {
                                for (MusicList musicList : musicLists1) {
                                    for (MusicInfo musicInfo : musicInfos) {
                                        if (musicInfo.getUid().equals(musicList.getUid())) {
                                            musicList.setMusicInfo(musicInfo);
                                        }
                                    }
                                }
                            }
                        }
                        return musicData;
                    });
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        musicData -> mView.queryMusicCallback(musicData),
                        throwable -> mView.queryMusicCallback(null));
        mCompositeSubscription.add(disposable);
    }

    public Observable<MusicInfo> getPlayUrl(String uid, String id) {
        return requestStore.getPlayUrl(id)
                .map(respond -> {
                    MusicInfo musicInfo = new MusicInfo();
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            musicInfo = JSON.parseObject(body, MusicInfo.class);
                            if (musicInfo != null) {
                                musicInfo.setUid(uid);
                            }
                            respond.setObj(musicInfo);
                        }
                    }
                    return musicInfo;
                }).subscribeOn(Schedulers.io())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.d("getPlayUrl", throwable.getMessage());
                    }
                })
                .toObservable();
    }
}
