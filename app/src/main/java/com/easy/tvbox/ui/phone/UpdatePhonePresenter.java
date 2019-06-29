package com.easy.tvbox.ui.phone;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.ImageCode;
import com.easy.tvbox.bean.PhoneCode;
import com.easy.tvbox.bean.Respond;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class UpdatePhonePresenter extends BasePresenter<UpdatePhoneView> {

    @Inject
    public UpdatePhonePresenter() {

    }

    @Override
    public void onAttached() {

    }

    /**
     * 更新手机号
     *
     * @param code
     * @param oldPhone
     * @param newPhone
     */
    public void updatePhone(String code, String oldPhone, String newPhone) {
        Map<String, Object> map = new HashMap<>();
        map.put("oldPhone", oldPhone);
        map.put("newPhone", newPhone);
        Disposable disposable = requestStore.updatePhone(code, map)
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
                .subscribe(respond -> mView.updatePhone(respond),
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.updatePhone(respond);
                        });
        mCompositeSubscription.add(disposable);
    }

    /**
     * 验证旧手机号
     *
     * @param code
     * @param oldPhone
     */
    public void validOldPhone(String code, String oldPhone) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", oldPhone);
        map.put("code", code);
        Disposable disposable = requestStore.validOldPhone(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respond -> mView.validOldPhoneCallback(respond),
                        throwable -> {
                            Respond respond = getThrowableRespond(throwable);
                            mView.validOldPhoneCallback(respond);
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
}
