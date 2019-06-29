package com.easy.tvbox.ui.music;

import android.content.Intent;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
import com.bumptech.glide.Glide;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.MusicInfo;
import com.easy.tvbox.bean.MusicList;
import com.easy.tvbox.databinding.MusicDetailBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.ui.LoadingView;
import com.easy.tvbox.utils.MImageGetter;
import com.easy.tvbox.utils.ToastUtils;

import java.util.List;

import javax.inject.Inject;

public class MusicDetailActivity extends BaseActivity<MusicDetailBinding> implements MusicDetailView {

    @Inject
    MusicDetailPresenter presenter;
    int position;
    MusicList musicList;
    AliVcMediaPlayer mPlayer;
    MusicInfo musicInfo;

    @Override
    public int getLayoutId() {
        return R.layout.music_detail;
    }

    @Override
    public void initDagger() {
        App.getAppComponent().inject(this);
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
        position = intent.getIntExtra("position", 0);
        if (MusicFragment.musicLists.size() > position && position >= 0) {
            musicList = MusicFragment.musicLists.get(position);
        }
        if (musicList == null) {
            finish();
            return;
        }

        initViewClick();

        initPayer();

        networkChange(NetworkUtils.isWifiConnected(MusicDetailActivity.this));

        startPayer();
    }

    private void initViewClick() {
        mViewBinding.tvLyric.setMovementMethod(ScrollingMovementMethod.getInstance());
        mViewBinding.loadingView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isWifiConnected(MusicDetailActivity.this)) {
                    networkChange(true);
                }
            }
        });

        mViewBinding.ivPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                if (position < 0) {
                    ToastUtils.showLong("已经是第一首歌曲");
                    position = 0;
                    return;
                }
                startPayer();
            }
        });

        mViewBinding.ivPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer != null) {
                    if (mPlayer.isPlaying()) {
                        mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
                        mPlayer.pause();
                    } else {
                        if (musicInfo != null) {
                            mPlayer.play();
                            mViewBinding.ivPlayer.setImageResource(R.drawable.button_player_pause);
                        }
                    }
                }
            }
        });
        mViewBinding.ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if (position >= MusicFragment.musicLists.size()) {
                    ToastUtils.showLong("已经是最后一首歌曲");
                    position = MusicFragment.musicLists.size() - 1;
                    return;
                }
                startPayer();
            }
        });
    }

    private void startPayer() {
        if (position < 0 || position >= MusicFragment.musicLists.size()) {
            return;
        }
        musicList = MusicFragment.musicLists.get(position);
        if (musicList != null) {
            refreshView();
            if (mPlayer != null) {
                musicInfo = musicList.getMusicInfo();
                if (musicInfo != null) {
                    mPlayer.prepareAndPlay(musicInfo.getPlayUrl());
                }
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
                mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
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
            mViewBinding.llContain.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
        } else {
            mViewBinding.llContain.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
        }
    }

    private void refreshView() {
        String pic = "";
        List<String> pics = musicList.getPictures();
        if (pics != null && pics.size() > 0) {
            pic = pics.get(0);
        }
        Glide.with(MusicDetailActivity.this)
                .load(pic).error(R.drawable.error_icon)
                .placeholder(R.drawable.error_icon)
                .centerCrop()
                .into(mViewBinding.ivIcon);
        mViewBinding.tvTitle.setText(musicList.getTitle());
        mViewBinding.tvGeshou.setText("演唱者：" + musicList.getGeshou());

        MImageGetter urlImageGetter = new MImageGetter(mViewBinding.tvLyric, MusicDetailActivity.this);//实例化URLImageGetter类
        mViewBinding.tvLyric.setText(Html.fromHtml(musicList.getLyric(), urlImageGetter, null));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayer.pause();
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
