package com.easy.tvbox.ui.music;

import android.util.Log;
import android.view.View;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.MusicData;
import com.easy.tvbox.bean.MusicInfo;
import com.easy.tvbox.bean.MusicList;
import com.easy.tvbox.databinding.MusicBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.ui.LoadingView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MusicActivity extends BaseActivity<MusicBinding> implements MusicView {

    @Inject
    MusicPresenter musicPresenter;

    MusicAdapter adapter;
    public static List<MusicList> musicLists = new ArrayList<>();
    int page = 0;
    Account account;
    int videoId = 1;//1 音乐，2 MV
    AliVcMediaPlayer mPlayer;
    int currentPosition = 0;//播放的位置
    MusicList currentPlayingMusic;

    @Override
    public int getLayoutId() {
        return R.layout.music;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void addPresenters(List<BasePresenter> observerList) {
        observerList.add(musicPresenter);
    }

    @Override
    public void initView() {
        account = DataManager.getInstance().queryAccount();
        if (account == null) {
            finish();
            return;
        }
        initPayer();
        adapter = new MusicAdapter(this, musicLists);
        mViewBinding.listView.setAdapter(adapter);
        mViewBinding.listView.setOnItemClickListener((parent, view, position, id) -> {
            if (musicLists != null && musicLists.size() > 0 && position < musicLists.size()) {
                if (mPlayer != null) {
                    if (mPlayer.isPlaying()) {
                        mPlayer.stop();
                        currentPlayingMusic = null;
                        currentPosition = 0;
                        refreshView(true);
                    }
                }
                RouteManager.goMusicDetailActivity(MusicActivity.this, position);
            }
        });

        mViewBinding.loadingView.setRetryListener(v -> {
            if (NetworkUtils.is3gConnected(MusicActivity.this)) {
                networkChange(true);
            }
        });

        mViewBinding.ivPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer != null) {
                    if (mPlayer.isPlaying()) {
                        mPlayer.pause();
                        refreshView(true);
                    } else {
                        if (currentPlayingMusic != null) {
                            mPlayer.play();
                            refreshView(false);
                        } else {
                            startPayer();
                        }
                    }
                }
            }
        });

        networkChange(NetworkUtils.is3gConnected(MusicActivity.this));
    }

    private void startPayer() {
        if (currentPosition < 0 || currentPosition >= musicLists.size()) {
            return;
        }
        currentPlayingMusic = musicLists.get(currentPosition);
        if (currentPlayingMusic != null) {
            MusicInfo musicInfo = currentPlayingMusic.getMusicInfo();
            if (musicInfo != null) {
                if (mPlayer != null) {
                    mPlayer.prepareAndPlay(musicInfo.getPlayUrl());
                    refreshView(false);
                }
            }
        }
    }

    public void refreshView(boolean stop) {
        if (musicLists != null && musicLists.size() > 0) {
            for (int i = 0; i < musicLists.size(); i++) {
                MusicList musicList = musicLists.get(i);
                if (i == currentPosition && !stop) {
                    musicList.setPlayerState(1);
                } else {
                    musicList.setPlayerState(0);
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            if (stop) {
                mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
            } else {
                mViewBinding.ivPlayer.setImageResource(R.drawable.button_player_pause);
            }
        }
    }

    private void initPayer() {
        mPlayer = new AliVcMediaPlayer(this, mViewBinding.surfaceView);

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
//                Log.d("VideoActivity", "音频数据回调接口，在需要处理音频时使用，如拿到视频音频，然后绘制音柱");
            }
        });
        mPlayer.setFrameInfoListener(new MediaPlayer.MediaPlayerFrameInfoListener() {
            @Override
            public void onFrameInfoListener() {
                //首帧显示时触发
                Log.d("VideoActivity", "首帧显示时触发");
                mViewBinding.ivPlayer.setImageResource(R.drawable.button_player_pause);
            }
        });
        mPlayer.setErrorListener(new MediaPlayer.MediaPlayerErrorListener() {
            @Override
            public void onError(int i, String msg) {
                //错误发生时触发，错误码见接口文档
                Log.d("VideoActivity", "错误发生时触发，错误码见接口文档:" + i + "\n" + msg);
                mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
            }
        });
        mPlayer.setCompletedListener(new MediaPlayer.MediaPlayerCompletedListener() {
            @Override
            public void onCompleted() {
                //视频正常播放完成时触发
                Log.d("VideoActivity", "视频正常播放完成时触发");
                currentPosition++;
                if (currentPosition >= musicLists.size()) {
                    mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
                } else {
                    startPayer();
                }
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
                mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
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
    public void networkChange(boolean isConnect) {
        if (isConnect) {
            musicPresenter.queryMusic(page, account.getShopNo(), videoId);
            mViewBinding.llContain.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
        } else {
            mViewBinding.llContain.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
        }
    }

    @Override
    public void queryMusicCallback(MusicData musicData) {
        if (musicData != null) {
            List<MusicList> musicDataContent = musicData.getContent();
            if (musicDataContent != null && musicDataContent.size() > 0) {
                musicLists.addAll(musicDataContent);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.destroy();
        }
        musicLists.clear();
    }
}
