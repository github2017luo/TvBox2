package com.easy.tvbox.ui.login;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.easy.tvbox.ui.home.HomeActivity;
import com.easy.tvbox.utils.SMSCountDownTimer;
import com.easy.tvbox.utils.SpaceFilter;
import com.easy.tvbox.utils.ToastUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;

import static com.easy.tvbox.base.Constant.IS_DEBUG;

//@Route(path = RouteManager.LOGIN, name = "登录/注册")
public class LoginActivity extends BaseActivity<LoginBinding> implements LoginView {

    @Inject
    LoginPresenter loginPresenter;
    SMSCountDownTimer smsCountDownTimer;
    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码

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
        if (IS_DEBUG) {
            Account account = DataManager.getInstance().queryAccount();
            if (account != null) {
                RouteManager.goHomeActivity(LoginActivity.this);
                finish();
                return;
            }
        }
        getPermissions();
        mViewBinding.editPhone.setFilters(new InputFilter[]{new SpaceFilter()});
        mViewBinding.editImageCode.setFilters(new InputFilter[]{new SpaceFilter()});
        mViewBinding.editPhoneCode.setFilters(new InputFilter[]{new SpaceFilter()});

        mViewBinding.editImageCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (view.getId() == R.id.editPhone) {
                        //KEYCODE_DPAD_DOWN这个按各自需求做拦截处理,一般不需要处理
                        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                            mViewBinding.editPhone.requestFocus();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        mViewBinding.editPhoneCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (view.getId() == R.id.editPhone) {
                        //KEYCODE_DPAD_DOWN这个按各自需求做拦截处理,一般不需要处理
                        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                            mViewBinding.editImageCode.requestFocus();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        mViewBinding.rlImageCode.setOnClickListener(v -> loginPresenter.generateImageCode());

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
            if (IS_DEBUG) {
                phoneCode = Constant.CODE;
            }
            loginPresenter.login(phone, phoneCode);
        });

        mViewBinding.loadingView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetConnected(LoginActivity.this)) {
                    networkChange(true);
                }
            }
        });

        if (IS_DEBUG) {
            mViewBinding.editPhone.setText("13779926287");
            mViewBinding.editImageCode.setText("ssss");
            mViewBinding.editPhoneCode.setText("sss");
        }

        networkChange(NetworkUtils.isNetConnected(LoginActivity.this));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            if (grantResults.length >= 1) {
                int writeResult = grantResults[0];
                //读写内存权限
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
            }
        }
    }

    /**
     * 获取权限
     */
    private boolean getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GET_PERMISSION_REQUEST);
                return false;
            }
        } else {
            return true;
        }
    }
}
