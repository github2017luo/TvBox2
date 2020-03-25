package com.easy.tvbox.base;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.easy.tvbox.ui.album.AlbumActivity;
import com.easy.tvbox.ui.album.AlbumListActivity;
import com.easy.tvbox.ui.daily.DailyActivity;
import com.easy.tvbox.ui.home.HomeActivity;
import com.easy.tvbox.ui.live.LiveActivity;
import com.easy.tvbox.ui.login.LoginActivity;
import com.easy.tvbox.ui.mine.MineActivity;
import com.easy.tvbox.ui.music.MusicActivity;
import com.easy.tvbox.ui.music.MusicDetailActivity;
import com.easy.tvbox.ui.phone.UpdatePhoneActivity;
import com.easy.tvbox.ui.test.TestActivity;
import com.easy.tvbox.ui.video.DailyVideoActivity;
import com.easy.tvbox.ui.video.MusicVideoActivity;
import com.easy.tvbox.ui.video.VideoActivity;

public class RouteManager {

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

    public static void goAlbumListActivity(Context context, String uid) {
        Intent intent = new Intent(context, AlbumListActivity.class);
        intent.putExtra("uid", uid);
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

    public static void goTestActivity(Context context) {
        Intent intent = new Intent(context, TestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public static void goDailyActivity(Context context) {
        Intent intent = new Intent(context, DailyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 音乐视频-- mv，
     *
     * @param context
     * @param json
     */
    public static void goMusicVideoActivity(Context context, String json) {
        Intent intent = new Intent(context, MusicVideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("data", json);
        context.startActivity(intent);
    }

    public static void goAlbumActivity(Context context) {
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
