package com.easy.tvbox.base;

import com.easy.tvbox.BuildConfig;

public class Constant {
    public static final String RELEASE_DOMAIN = "https://gl.ajitai.com.cn";
    public static final String DEBUG_DOMAIN = "http://oa.xm-golden.cn";
    public static final String BASE_URL = (BuildConfig.DEBUG ? DEBUG_DOMAIN : RELEASE_DOMAIN) + "/ajitai/api/box/";


    public static final String CODE = "azitai@2019";
    public static final String PHONE = "13779926288";
    public static final String PHONE_TEST = "13779926287";
}
