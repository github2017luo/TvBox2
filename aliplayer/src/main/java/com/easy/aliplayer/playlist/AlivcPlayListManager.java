package com.easy.aliplayer.playlist;


import com.alibaba.fastjson.JSON;
import com.easy.aliplayer.playlist.vod.core.AliyunVodHttpCommon;
import com.easy.aliplayer.playlist.vod.core.AliyunVodKey;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @author Mulberry
 * create on 2018/5/17.
 */

public class AlivcPlayListManager {
    public AlivcPlayListManager() {
    }

    private static class AlivcPlayListManagerHolder {
        private static AlivcPlayListManager instance = new AlivcPlayListManager();
    }

    public static AlivcPlayListManager getInstance() {
        return AlivcPlayListManagerHolder.instance;
    }

    public interface PlayListListener {
        void onPlayList(int code, ArrayList<AlivcVideoInfo.DataBean.VideoListBean> videos);
    }

    public void fetchPlayList(String accessKeyId, String accessKeySecret, String securityToken, PlayListListener playListListener) {
        fetchVideoList(accessKeyId, accessKeySecret, securityToken, playListListener);
    }

    private void fetchVideoList(String accessKeyId, String accessKeySecret, String securityToken, final PlayListListener playListListener) {

        OkHttpClient client = new OkHttpClient.Builder()
        .build();
        String url = AliyunVodHttpCommon.getInstance().getVodDomain() + "?" + AliyunVodKey.KEY_NEW_VOD_CATEID + "=" + AliyunVodHttpCommon.Action.GET_VIDEO_CATE_ID;
        final Request request = new Request.Builder()
        .url(url)
        .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                String body = response.body().string();

                AlivcVideoInfo alivcVideoInfo = JSON.parseObject(body, AlivcVideoInfo.class);
                AlivcVideoInfo.DataBean dataBean = alivcVideoInfo.getData();
                if (dataBean != null){
                    ArrayList<AlivcVideoInfo.DataBean.VideoListBean> videoList = (ArrayList<AlivcVideoInfo.DataBean.VideoListBean>) dataBean.getVideoList();
                    if (videoList != null) {
                        playListListener.onPlayList(response.code(), videoList);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {

            }
        });
    }

    /**
     * 模拟点播流演示视频
     */
    private ArrayList<AlivcVideoInfo.DataBean.VideoListBean> mockVodData() {
        ArrayList<AlivcVideoInfo.DataBean.VideoListBean> vodList = new ArrayList<>();
        return vodList;
    }

    public static Object fromJson(String jsonString, Type type) {
        return JSON.parseObject(jsonString, type);
    }

    public class AlivcVideoList {
        private String requestId;
        private AlivcVideoInfo[] videoList;
        private int totall;

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public AlivcVideoInfo[] getVideoList() {
            return videoList;
        }

        public void setVideoList(AlivcVideoInfo[] videoList) {
            this.videoList = videoList;
        }

        public int getTotall() {
            return totall;
        }

        public void setTotall(int totall) {
            this.totall = totall;
        }
    }
}
