package com.easy.tvbox.ui.video;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.LiveList;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.databinding.VideoBinding;
import com.easy.tvbox.utils.ToastUtils;

import java.util.List;

import javax.inject.Inject;

public class VideoActivity extends BaseActivity<VideoBinding> implements VideoView {

    @Inject
    VideoPresenter presenter;
    AliVcMediaPlayer mPlayer;
    LiveList liveData;

    @Override
    public int getLayoutId() {
        return R.layout.video;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void networkChange(boolean isConnect) {

    }

    @Override
    public void addPresenters(List<BasePresenter> observerList) {
        observerList.add(presenter);
    }

    @Override
    public void initView() {
        Account account = DataManager.getInstance().queryAccount();
        if (account == null) {
            finish();
            return;
        }
        Intent intent = getIntent();
        String dataJson = intent.getStringExtra("data");
        try {
            liveData = JSON.parseObject(dataJson, LiveList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (liveData == null) {
            finish();
            return;
        }
        initPayer();
        presenter.getLivePlayUrl(liveData.getUid());
    }

    private void startPayer(String uri) {
        if (mPlayer != null) {
            mPlayer.prepareAndPlay(uri);
        }
    }

    private void initPayer() {
        mPlayer = new AliVcMediaPlayer(this, mViewBinding.surfaceView);
        mPlayer.setMaxBufferDuration(30 * 1000);
        mPlayer.setPreparedListener(new MediaPlayer.MediaPlayerPreparedListener() {
            @Override
            public void onPrepared() {
                //准备完成时触发
                Log.d("VideoActivity", "准备完成时触发");
            }
        });
        mPlayer.setPcmDataListener(new MediaPlayer.MediaPlayerPcmDataListener() {
            @Override
            public void onPcmData(byte[] bytes, int i) {
                //音频数据回调接口，在需要处理音频时使用，如拿到视频音频，然后绘制音柱。
                Log.d("VideoActivity", "音频数据回调接口，在需要处理音频时使用，如拿到视频音频，然后绘制音柱");
            }
        });
        mPlayer.setFrameInfoListener(new MediaPlayer.MediaPlayerFrameInfoListener() {
            @Override
            public void onFrameInfoListener() {
                //首帧显示时触发
                Log.d("VideoActivity", "首帧显示时触发");
            }
        });
        mPlayer.setErrorListener(new MediaPlayer.MediaPlayerErrorListener() {
            @Override
            public void onError(int i, String msg) {
                //错误发生时触发，错误码见接口文档
                Log.d("VideoActivity", "错误发生时触发，错误码见接口文档:" + i + "\n" + msg);
            }
        });
        mPlayer.setCompletedListener(new MediaPlayer.MediaPlayerCompletedListener() {
            @Override
            public void onCompleted() {
                //视频正常播放完成时触发
                Log.d("VideoActivity", "视频正常播放完成时触发");
                finish();
            }
        });
        mPlayer.setSeekCompleteListener(new MediaPlayer.MediaPlayerSeekCompleteListener() {
            @Override
            public void onSeekCompleted() {
                //视频seek完成时触发
                Log.d("VideoActivity", "视频seek完成时触发");
            }
        });
        mPlayer.setStoppedListener(new MediaPlayer.MediaPlayerStoppedListener() {
            @Override
            public void onStopped() {
                //使用stop接口时触发
                Log.d("VideoActivity", "使用stop接口时触发");
            }
        });
        mPlayer.setCircleStartListener(new MediaPlayer.MediaPlayerCircleStartListener() {
            @Override
            public void onCircleStart() {
                //循环播放开始
                Log.d("VideoActivity", "循环播放开始");
            }
        });
        //SEI数据回调
        mPlayer.setSEIDataListener(new MediaPlayer.MediaPlayerSEIDataListener() {
            @Override
            public void onSeiUserUnregisteredData(String data) {
                //解析SEI数据，在这里可以展示题目信息或答案信息
                Log.d("VideoActivity", "解析SEI数据，在这里可以展示题目信息或答案信息");
            }
        });
    }

    @Override
    public void livePlayUrlCallback(Respond<String> respond) {
        if (respond.isOk()) {
            String uri = respond.getObj();
            if (!TextUtils.isEmpty(uri)) {
                startPayer(uri);
            }
        } else {
            ToastUtils.showLong(respond.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.destroy();
        }
    }
}
