package com.easy.tvbox.base;

import java.io.File;

public class Constant {
    public static final String RELEASE_DOMAIN = "https://gl.ajitai.com.cn";
    public static final String DEBUG_DOMAIN = "http://oa.xm-golden.cn";
    public static final String BASE_URL = DEBUG_DOMAIN + "/ajitai/api/box/";


    public static final String CODE = "azitai@2019";
    public static final String PHONE = "13779926288";
    public static final String PHONE_TEST = "13779926287";

    public static final boolean IS_DEBUG = true;//是否是Degbug
    public static final boolean OPEN_PLAYER = true;// 是否开启播放器，播放器在模拟器上是不能用的
    public static final boolean isTestDownload = false;//是否是Degbug

    /**
     * 文件根路径
     */
    public static final String BASE_FILE_PATH = "localFile" + File.separator;

    // 文件类型
    /**
     * 应用类型:app
     */
    public static final int TYPE_APP = 0;
    /**
     * 文件类型:file
     */
    public static final int TYPE_MORE = 1;

    /**
     * 录音
     */
    public static final int TYPE_AUDIO_RECORD = 2;

    /**
     * 图片类型：Photo
     */
    public static final int TYPE_PHOTO = 3;

    /**
     * 应用文件保存路径
     */
    public static final String SAVE_APP_PATH = BASE_FILE_PATH + "app" + File.separator;
    /**
     * 录音位置
     */
    public static final String SAVE_AUDIO_RECORD_PATH = BASE_FILE_PATH + "audio_record" + File.separator;

    /**
     * 图片保存路径
     */
    public static final String SAVE_PHOTO_PATH = BASE_FILE_PATH + "photo" + File.separator;
}
