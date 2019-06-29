package com.easy.tvbox.ui.music;

import com.easy.tvbox.base.BasePresenter;

import javax.inject.Inject;

public class MusicDetailPresenter extends BasePresenter<MusicDetailView> {

    @Inject
    public MusicDetailPresenter() {

    }

    @Override
    public void onAttached() {

    }

//    /**
//     * 获取音乐列表
//     * page（页码），size（每页大小），sort（排序字段，支持多个，写法如：sort=a,desc&sort=b,asc）
//     */
//    public void getMusicDetail(String uid) {
//        Disposable disposable = requestStore.getMusicDetail(uid)
//                .doOnSuccess(respond -> {
//                    if (respond.isOk()) {
//                        String body = respond.getBody();
//                        if (!TextUtils.isEmpty(body)) {
//                            try {
//                                MusicDetail musicData = JSON.parseObject(body, MusicDetail.class);
//                                respond.setObj(musicData);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(respond -> mView.musicDetailCallback(respond),
//                        throwable -> {
//                            Respond respond = getThrowableRespond(throwable);
//                            mView.musicDetailCallback(respond);
//                        });
//        mCompositeSubscription.add(disposable);
//    }
}
