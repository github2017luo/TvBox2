package com.easy.tvbox.ui.phone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;

import com.easy.tvbox.BuildConfig;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.Constant;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.ImageCode;
import com.easy.tvbox.bean.PhoneCode;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.databinding.UpdatePhoneBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.ui.LoadingView;
import com.easy.tvbox.utils.SMSCountDownTimer;
import com.easy.tvbox.utils.ToastUtils;

import java.util.List;

import javax.inject.Inject;

public class UpdatePhoneActivity extends BaseActivity<UpdatePhoneBinding> implements UpdatePhoneView {

    @Inject
    UpdatePhonePresenter presenter;
    SMSCountDownTimer smsCountDownTimer;
    boolean checkOldPhone = true;// true: 验证旧手机号
    Account account;

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void addPresenters(List<BasePresenter> observerList) {
        observerList.add(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.update_phone;
    }

    @Override
    public void initView() {
        account = DataManager.getInstance().queryAccount();
        if (account == null) {
            finish();
        }
        mViewBinding.editPhone.setText(account.getPhone());
        mViewBinding.editPhone.setEnabled(false);

        mViewBinding.ivImageCode.setOnClickListener(v -> presenter.generateImageCode());

        mViewBinding.tvPhoneCode.setOnClickListener(v -> {
            String phone = mViewBinding.editPhone.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                ToastUtils.showLong("请输入新手机号");
                return;
            }
            String imageCode = mViewBinding.editImageCode.getText().toString();
            if (TextUtils.isEmpty(imageCode)) {
                ToastUtils.showLong("请填写图形验证码");
                return;
            }
            if (mViewBinding.ivImageCode.getTag() != null) {
                mViewBinding.tvPhoneCode.setEnabled(false);
                mViewBinding.tvPhoneCode.setText("获取中...");
                mViewBinding.tvPhoneCode.setTag(phone);
                presenter.sendMessage(phone, "login", imageCode, mViewBinding.ivImageCode.getTag().toString());
            } else {
                ToastUtils.showLong("请刷新图形验证码重试");
            }
        });

        mViewBinding.tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mViewBinding.editPhone.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showLong("请输入新手机号");
                    return;
                }
                String imageCode = mViewBinding.editImageCode.getText().toString();
                if (TextUtils.isEmpty(imageCode)) {
                    ToastUtils.showLong("请填写图形验证码");
                    return;
                }
                String phoneCode = mViewBinding.editPhoneCode.getText().toString();
                if (TextUtils.isEmpty(phoneCode)) {
                    ToastUtils.showLong("请填写短信验证码");
                    return;
                }
                if (BuildConfig.DEBUG) {
                    phoneCode = Constant.CODE;
                    if (account.getPhone().equals(Constant.PHONE)) {
                        phone = Constant.PHONE_TEST;
                    } else {
                        phone = Constant.PHONE;
                    }
                }
                if (checkOldPhone) {
                    presenter.validOldPhone(phoneCode, account.getPhone());
                } else {
                    presenter.updatePhone(phoneCode, account.getPhone(), phone);
                }
            }
        });

        mViewBinding.loadingView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.is3gConnected(UpdatePhoneActivity.this)) {
                    networkChange(true);
                }
            }
        });

        networkChange(NetworkUtils.is3gConnected(UpdatePhoneActivity.this));
    }

    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect) {
            presenter.generateImageCode();
            mViewBinding.llContain.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
        } else {
            mViewBinding.llContain.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
        }
    }

    /**
     * 切换到新手机号验证环境
     */
    public void switchNewPhoneEnv() {
        mViewBinding.editPhone.setEnabled(true);
        mViewBinding.editPhone.setText("");
        mViewBinding.editPhone.setHint("请输入新手机号");
        mViewBinding.editImageCode.setText("");
        mViewBinding.editPhoneCode.setText("");
        if (smsCountDownTimer != null) {
            smsCountDownTimer.cancel();
            smsCountDownTimer.onFinish();
        }
        checkOldPhone = false;
    }

    @Override
    public void updatePhone(Respond respond) {
        if (respond.isOk()) {
            String phone = mViewBinding.editPhone.getText().toString();
            account.setPhone(phone);
            DataManager.getInstance().updateAccount(account);
            ToastUtils.showLong(respond.getMessage());
            finish();
        } else {
            ToastUtils.showLong(respond.getMessage());
        }
    }

    @Override
    public void validOldPhoneCallback(Respond respond) {
        if (respond.isOk()) {
            switchNewPhoneEnv();
        } else {
            ToastUtils.showLong(respond.getMessage());
        }
    }

    @Override
    public void imageCodeCallback(Respond<ImageCode> respond) {
        if (respond.isOk()) {
            ImageCode imageCode = respond.getObj();
            if (imageCode != null && !TextUtils.isEmpty(imageCode.getImage())) {
                byte[] decodedString = android.util.Base64.decode(imageCode.getImage(), android.util.Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                mViewBinding.ivImageCode.setImageBitmap(decodedByte);
                mViewBinding.ivImageCode.setTag(imageCode.getId());
            }
        } else {
            ToastUtils.showLong(respond.getMessage());
        }
    }

    @Override
    public void sendMessageCallback(Respond<PhoneCode> respond) {
        if (respond.isOk()) {
            ToastUtils.showLong(respond.getMessage());
            smsCountDownTimer = new SMSCountDownTimer(mViewBinding.tvPhoneCode, 60000, 1000);
        } else {
            mViewBinding.tvPhoneCode.setText(getString(R.string.get_phone_code));
            mViewBinding.tvPhoneCode.setEnabled(true);
            ToastUtils.showLong(respond.getMessage());
        }
    }
}
