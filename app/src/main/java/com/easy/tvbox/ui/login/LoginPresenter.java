package com.easy.tvbox.ui.login;

import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.CheckLogin;
import com.easy.tvbox.bean.ImageCode;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.bean.Shop;

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

public class LoginPresenter extends BasePresenter<LoginView> {

    Disposable requestDisposable;

    @Inject
    public LoginPresenter() {

    }

    @Override
    public void onAttached() {

    }

    /**
     * 请求二维码
     */
    public void requestQrCode() {
        Disposable disposable = requestStore.requestQrCode()
                .doOnSuccess(respond -> {
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            try {
                                ImageCode imageCode = JSON.parseObject(body, ImageCode.class);
                                respond.setObj(imageCode);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respond -> mView.imageCodeCallback(respond),
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.imageCodeCallback(respond);
                        });
        mCompositeSubscription.add(disposable);
    }

    /**
     * 定时检测是否登录
     */
    public void timeCheckLogin(String key) {
        requestCancel();
        //每10分支更新一次数据
        Observable.interval(0, 5, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        requestDisposable = disposable;
                    }

                    @Override
                    public void onNext(Long number) {
                        requestCheckLogin(key);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //取消订阅
                        requestCancel();
                    }

                    @Override
                    public void onComplete() {
                        //取消订阅
                        requestCancel();
                    }
                });
    }

    /**
     * 取消订阅
     */
    public void requestCancel() {
        if (requestDisposable != null && !requestDisposable.isDisposed()) {
            requestDisposable.dispose();
        }
    }

    /**
     * 检测登录
     */
    public void requestCheckLogin(String key) {
        Disposable disposable = requestStore.requestCheckLogin(key)
                .doOnSuccess(respond -> {
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            try {
                                CheckLogin checkLogin = JSON.parseObject(body, CheckLogin.class);
                                respond.setObj(checkLogin);
                                requestCancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respond -> mView.checkLoginCallback(respond),
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.checkLoginCallback(respond);
                        });
        mCompositeSubscription.add(disposable);
    }

    /**
     * 登陆
     *
     * @param userId
     */
    public void login(String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", userId);
        Disposable disposable = requestStore.login(Build.SERIAL, map)
                .doOnSuccess(respond -> {
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            try {
                                Account account = JSON.parseObject(body, Account.class);
                                respond.setObj(account);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respond -> mView.loginCallback(respond),
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.loginCallback(respond);
                        });
        mCompositeSubscription.add(disposable);
    }

    /**
     * 获取门店信息
     */
    public void getAllShop() {
        Disposable disposable = requestStore.getAllShop()
                .doOnSuccess(respond -> {
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            try {
                                List<Shop> shopList = JSON.parseArray(body, Shop.class);
                                respond.setObj(shopList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respond -> {
                            //mView.getAllShopCallback(respond),
                        },
                        throwable -> {
//                            Respond respond = getThrowableRespond(throwable);
//                            mView.getAllShopCallback(respond);
                        });
        mCompositeSubscription.add(disposable);
    }
}
