package com.easy.tvbox.base;

import android.app.Application;

import com.easy.tvbox.dagger.AppComponent;
import com.easy.tvbox.dagger.DaggerAppComponent;
import com.easy.tvbox.utils.ToastUtils;

public class App extends Application {

    private static App app;
    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        DataManager.getInstance().init(this);//数据库统一操作管理类初始化
        initComponent();
        ToastUtils.initToast(this);
    }

    public static App getApp() {
        return app;
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    /**
     * 初始化各模块dagger组件
     */
    private void initComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder().build();
        }
    }
}
