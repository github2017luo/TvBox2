package com.easy.tvbox.http;

import android.util.Log;

import com.easy.tvbox.base.App;
import com.easy.tvbox.base.Constant;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpManager {

    public Retrofit retrofit;

    private static class Holder {
        private static final HttpManager instance = new HttpManager();
    }

    public static HttpManager getInstance() {
        return Holder.instance;
    }

    private HttpManager() {
        init();
    }

    public void init() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(message -> Log.d("logInterceptor", message));
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        File cacheFile = new File(App.getApp().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 10); //100Mb

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logInterceptor)
                .cache(cache);
//                .hostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                }).sslSocketFactory(SocketFactory.createSSLSocketFactory());

        retrofit = new Retrofit.Builder()
                .client(okHttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constant.BASE_URL)
                .build();
    }
}
