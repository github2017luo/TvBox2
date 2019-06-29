package com.easy.tvbox.base;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.easy.tvbox.ui.daily.DailyActivity;
import com.easy.tvbox.ui.home.HomeActivity;
import com.easy.tvbox.ui.live.LiveActivity;
import com.easy.tvbox.ui.login.LoginActivity;
import com.easy.tvbox.ui.mine.MineActivity;
import com.easy.tvbox.ui.music.MusicActivity;
import com.easy.tvbox.ui.music.MusicDetailActivity;
import com.easy.tvbox.ui.phone.UpdatePhoneActivity;
import com.easy.tvbox.ui.video.DailyVideoActivity;
import com.easy.tvbox.ui.video.VideoActivity;

public class RouteManager {
    public static final String HOME = "/HomeActivity";
    public static final String LOGIN = "/LoginActivity";
    public static final String VIDEO = "/VideoActivity";
    public static final String UPDATE_PHONE = "/UpdatePhoneActivity";
    public static final String MUSIC = "/MusicActivity";
    public static final String MUSIC_DETAIL = "/MusicDetailActivity";
    public static final String MINE = "/MineActivity";
    public static final String LIVE = "/LiveActivity";
    public static final String DAILY = "/DailyActivity";

    public static void goMusicDetailActivity(Context context, int position) {
        Intent intent = new Intent(context, MusicDetailActivity.class);
        intent.putExtra("position", position);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void goMusicActivity(Context context) {
        Intent intent = new Intent(context, MusicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void goHomeActivity(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void goUpdatePhoneActivity(Context context) {
        Intent intent = new Intent(context, UpdatePhoneActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void goLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 视频播放-- 大课，
     *
     * @param context
     * @param json
     */
    public static void goVideoActivity(Context context, String json) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("data", json);
        context.startActivity(intent);
    }

    /**
     * 视频播放---每日课程
     *
     * @param context
     * @param json
     */
    public static void goDailyVideoActivity(Context context, String json) {
        Intent intent = new Intent(context, DailyVideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("data", json);
        context.startActivity(intent);
    }


    public static void goMineActivity(Context context) {
        Intent intent = new Intent(context, MineActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static void goLiveActivity(Context context) {
        Intent intent = new Intent(context, LiveActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void goDailyActivity(Context context) {
        Intent intent = new Intent(context, DailyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
