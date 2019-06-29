package com.easy.tvbox.ui.login;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.ImageCode;
import com.easy.tvbox.bean.PhoneCode;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.bean.Shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class LoginPresenter extends BasePresenter<LoginView> {

    @Inject
    public LoginPresenter() {

    }

    @Override
    public void onAttached() {

    }

    /**
     * 注册
     *
     * @param code
     * @param phone
     * @param password
     */
    public void register(String phone, String password, String shopNo, String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("password", password);
        map.put("shopNo", shopNo);
        map.put("code", code);
        Disposable disposable = requestStore.register(map)
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
     * 登陆
     *
     * @param phone
     * @param phoneCode
     */
    public void login(String phone, String phoneCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", phone);
        Disposable disposable = requestStore.login(phoneCode, map)
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

    /**
     * 获取验证码
     *
     * @param phone
     * @param type  注册验证码：type=’register’
     *              登录验证码：type=’login’
     *              修改密码：type=‘editpwd’
     *              修改手机号：type=’editphone’
     */
    public void sendMessage(String phone, String type, String imageCode, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("type", type);
        map.put("imageCode", imageCode);
        map.put("id", id);
        Disposable disposable = requestStore.sendMessage(map)
                .doOnSuccess(respond -> {
                    if (respond.isOk()) {
                        String body = respond.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            try {
                                PhoneCode phoneCode = JSON.parseObject(body, PhoneCode.class);
                                respond.setObj(phoneCode);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respond -> mView.sendMessageCallback(respond),
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.sendMessageCallback(respond);
                        });
        mCompositeSubscription.add(disposable);
    }

    /**
     * 获取图形验证码
     */
    public void generateImageCode() {
        Disposable disposable = requestStore.generateImageCode()
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
}
