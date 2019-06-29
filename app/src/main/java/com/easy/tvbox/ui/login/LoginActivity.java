package com.easy.tvbox.ui.login;

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
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.ImageCode;
import com.easy.tvbox.bean.PhoneCode;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.databinding.LoginBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.ui.LoadingView;
import com.easy.tvbox.utils.SMSCountDownTimer;
import com.easy.tvbox.utils.ToastUtils;

import java.util.List;

import javax.inject.Inject;

//@Route(path = RouteManager.LOGIN, name = "登录/注册")
public class LoginActivity extends BaseActivity<LoginBinding> implements LoginView {

    @Inject
    LoginPresenter loginPresenter;
    SMSCountDownTimer smsCountDownTimer;

    @Override
    public void addPresenters(List<BasePresenter> observerList) {
        observerList.add(loginPresenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.login;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void initView() {
        isLoginActivity = true;
        if (BuildConfig.DEBUG) {
            Account account = DataManager.getInstance().queryAccount();
            if (account != null) {
                RouteManager.goHomeActivity(LoginActivity.this);
                finish();
                return;
            }
        }
        mViewBinding.ivImageCode.setOnClickListener(v -> loginPresenter.generateImageCode());

        mViewBinding.tvPhoneCode.setOnClickListener(v -> {
            String phone = mViewBinding.editPhone.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                ToastUtils.showLong("请输入手机号");
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
                loginPresenter.sendMessage(phone, "login", imageCode, mViewBinding.ivImageCode.getTag().toString());
            } else {
                ToastUtils.showLong("请刷新图形验证码重试");
            }
        });

        mViewBinding.login.setOnClickListener(v -> {
            String phone = mViewBinding.editPhone.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                ToastUtils.showLong("请输入手机号");
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
            }
            loginPresenter.login(phone, phoneCode);
        });
        mViewBinding.loadingView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isWifiConnected(LoginActivity.this)) {
                    networkChange(true);
                }
            }
        });
        networkChange(NetworkUtils.isWifiConnected(LoginActivity.this));
    }

    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect) {
            loginPresenter.generateImageCode();
            mViewBinding.llContain.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
        } else {
            mViewBinding.llContain.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
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

    @Override
    public void loginCallback(Respond<Account> respond) {
        if (respond.isOk()) {
            Account account = respond.getObj();
            ToastUtils.showLong(respond.getMessage());
            if (account != null) {
                DataManager.getInstance().login(account);
                RouteManager.goHomeActivity(this);
                finish();
            }
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
    public void onBackPressed() {

    }
}
