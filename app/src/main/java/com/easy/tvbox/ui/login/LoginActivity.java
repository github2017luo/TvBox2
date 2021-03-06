package com.easy.tvbox.ui.login;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.easy.tvbox.BuildConfig;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.Constant;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.AppVersion;
import com.easy.tvbox.bean.CheckLogin;
import com.easy.tvbox.bean.ImageCode;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.databinding.LoginBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.http.ProgressInfo;
import com.easy.tvbox.http.ProgressListener;
import com.easy.tvbox.http.ProgressManager;
import com.easy.tvbox.ui.LoadingView;
import com.easy.tvbox.ui.home.HomeActivity;
import com.easy.tvbox.ui.test.Utils;
import com.easy.tvbox.utils.SystemUtils;
import com.easy.tvbox.utils.ToastUtils;
import com.easy.tvbox.view.AppUpdateDialog;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;

//@Route(path = RouteManager.LOGIN, name = "登录/注册")
public class LoginActivity extends BaseActivity<LoginBinding> implements LoginView {

    @Inject
    LoginPresenter loginPresenter;
    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码
    AppUpdateDialog dialog;
    String downloadPath;

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
        String fileName = "tvBox.apk";
        downloadPath = Utils.getSaveFilePath(Constant.TYPE_APP, this) + fileName;
        getPermissions();
        isLoginActivity = true;
        Account account = DataManager.getInstance().queryAccount();

        if (BuildConfig.DEBUG) {
            if (account == null) {
                account = new Account();
                account.setId("15184784396");
                account.setName("ajt12345678");
                account.setPhone("13959932888");
                account.setShopName("内蒙古国际蒙医医院");
                account.setShopNo("S0016");
                DataManager.getInstance().login(account);
            } else {
                HomeActivity.canInHome = true;
            }
        }

        if (HomeActivity.canInHome && account != null) {
            RouteManager.goHomeActivity(LoginActivity.this);
            finish();
            return;
        }
        mViewBinding.tvRefresh.setOnClickListener(v -> {
            loginPresenter.requestQrCode();
        });
        mViewBinding.loadingView.setRetryListener(v -> {
            if (NetworkUtils.isNetConnected(LoginActivity.this)) {
                networkChange(true);
            }
        });
        networkChange(NetworkUtils.isNetConnected(LoginActivity.this));
        loginPresenter.timeCheckVersion();
    }


    @Override
    public void networkChange(boolean isConnect) {
        if (isConnect) {
            loginPresenter.requestQrCode();
            mViewBinding.llContain.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
        } else {
            mViewBinding.llContain.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
        }
    }

    @Override
    public void loginCallback(Respond<Account> respond) {
        if (respond.isOk()) {
            Account account = respond.getObj();
            ToastUtils.showLong(respond.getMessage());
            if (account != null) {
                DataManager.getInstance().login(account);
                HomeActivity.canInHome = true;
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
                mViewBinding.ivQrCode.setImageBitmap(decodedByte);
                loginPresenter.timeCheckLogin(imageCode.getKey());
            }
        } else {
            ToastUtils.showLong(respond.getMessage());
        }
    }

    @Override
    public void checkLoginCallback(Respond<CheckLogin> respond) {
        if (respond.isOk()) {
            CheckLogin checkLogin = respond.getObj();
            loginPresenter.login(checkLogin.getAid());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            if (grantResults.length >= 1) {
//                int writeResult = grantResults[0];
                //读写内存权限
//                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
            }
        }
    }

    private ProgressListener getDownloadListener() {
        return new ProgressListener() {
            @Override
            public void onProgress(ProgressInfo progressInfo) {
                int progress = progressInfo.getPercent();
//                Log.d("DownLoad", "--Download-- " + progress + " %  " + progressInfo.getSpeed() + " byte/s  " + progressInfo.toString());
                if (dialog != null) {
                    dialog.setProgress(progress);
                }
                if (progressInfo.isFinish()) {
                    if (dialog != null) {
                        dialog.setProgress(100);
                        dialog.dismiss();
                    }
                    if (downloadPath != null) {
                        Utils.install(mContext, BuildConfig.APPLICATION_ID, downloadPath);
                    }
                }
            }

            @Override
            public void onError(long id, Exception e) {
                Log.d("DownLoad", e.getMessage());
            }
        };
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

    @Override
    protected void onDestroy() {
        loginPresenter.timeCheckLoginCancel();
        loginPresenter.timeCheckVersionCancel();
        super.onDestroy();
    }

    @Override
    public void checkUpdateCallback(Respond<AppVersion> respond) {
        if (respond.isOk()) {
            AppVersion appVersion = respond.getObj();
            if (appVersion != null) {
                int currentVersion = SystemUtils.getVersion(BuildConfig.VERSION_NAME);
                int versionName = currentVersion;
                if (!TextUtils.isEmpty(appVersion.getVersion())) {
                    versionName = SystemUtils.getVersion(appVersion.getVersion());
                }
                if (versionName > currentVersion) {
                    showAppVersionDialog(appVersion);
                }
            }
        }
    }

    private void showAppVersionDialog(AppVersion appVersion) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new AppUpdateDialog(this);
        dialog.show();
        ProgressManager.getInstance().addResponseListener(appVersion.getDownloadUrl(), getDownloadListener());
        ProgressManager.getInstance().startDownload(downloadPath, appVersion.getDownloadUrl());
    }
}
