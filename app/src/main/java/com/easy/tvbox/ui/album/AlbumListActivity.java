package com.easy.tvbox.ui.album;

import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
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
import com.easy.tvbox.databinding.AlbumListBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.tvview.tvRecycleView.SimpleOnItemListener;
import com.easy.tvbox.tvview.tvRecycleView.TvRecyclerView;
import com.easy.tvbox.ui.LoadingView;
import com.easy.tvbox.ui.music.MusicFragment;
import com.easy.tvbox.utils.DimensUtils;
import com.owen.focus.FocusBorder;

import java.util.List;

import javax.inject.Inject;

import static com.easy.tvbox.base.Constant.OPEN_PLAYER;

public class AlbumListActivity extends BaseActivity<AlbumListBinding> implements AlbumListView {

    @Inject
    AlbumListPresenter presenter;
    FocusBorder mFocusBorder;
    AlbumListAdapter adapter;
    Account account;
    String uid;
    AliVcMediaPlayer mPlayer;
    List<MusicList> musicLists;
    int currentPosition = 0;//播放的位置
    boolean isPlaying;
    MusicList currentPlayingMusic;
    float scale = 1.03f;

    @Override
    public int getLayoutId() {
        return R.layout.album_list;
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
        account = DataManager.getInstance().queryAccount();
        if (account == null) {
            finish();
            return;
        }

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        int[] size = DimensUtils.getWidthHeight(this);
        int bigWidth = size[0] - DimensUtils.dp2px(this, 290 + 10);
        int smallWidth = size[0] - DimensUtils.dp2px(this, 290 + 10 + 40);
        scale = bigWidth * 1.0f / smallWidth;

        if (OPEN_PLAYER) {
            initPayer();
        }

        mFocusBorder = new FocusBorder.Builder()
                .asColor()
                .borderColorRes(R.color.actionbar_color)
                .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 3f)
                .shadowColorRes(R.color.green_bright)
                .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 5f)
                .build(this);

        mViewBinding.loadingView.setRetryListener(v -> {
            if (NetworkUtils.isNetConnected(AlbumListActivity.this)) {
                networkChange(true);
            }
        });

        adapter = new AlbumListAdapter(this);
        mViewBinding.recyclerView.setSpacingWithMargins(10, 3);
        mViewBinding.recyclerView.setAdapter(adapter);
        mViewBinding.recyclerView.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                onMoveFocusBorder(itemView, scale);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                if (musicLists != null && musicLists.size() > 0 && position < musicLists.size()) {
                    if (mPlayer != null) {
                        if (mPlayer.isPlaying()) {
                            mPlayer.stop();
                            currentPlayingMusic = null;
                            currentPosition = 0;
                            refreshView(true);
                        }
                    }
                    RouteManager.goMusicDetailActivity(AlbumListActivity.this, position);
                }
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

        mViewBinding.ivPlayer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onMoveFocusBorder(v, 1.1f);
            }
        });
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
                isPlaying = false;
                mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
            } else {
                isPlaying = true;
                mViewBinding.ivPlayer.setImageResource(R.drawable.button_player_pause);
            }
        }
    }

    protected void onMoveFocusBorder(View focusedView, float scale) {
        if (null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale));
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
                isPlaying = true;
                mViewBinding.ivPlayer.setImageResource(R.drawable.button_player_pause);
            }
        });
        mPlayer.setErrorListener(new MediaPlayer.MediaPlayerErrorListener() {
            @Override
            public void onError(int i, String msg) {
                //错误发生时触发，错误码见接口文档
                Log.d("VideoActivity", "错误发生时触发，错误码见接口文档:" + i + "\n" + msg);
                isPlaying = false;
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
                    isPlaying = false;
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
                isPlaying = false;
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
            mViewBinding.recyclerView.setVisibility(View.VISIBLE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_HIDDEN);
            presenter.querySongSheetMusic(uid);
        } else {
            mViewBinding.recyclerView.setVisibility(View.GONE);
            mViewBinding.loadingView.setStatus(LoadingView.STATUS_NONETWORK);
        }
    }

    @Override
    public void queryMusicCallback(MusicData data) {
        if (data != null) {
            musicLists = data.getContent();
            if (musicLists != null && musicLists.size() > 0) {
                MusicFragment.musicLists.clear();
                MusicFragment.musicLists.addAll(musicLists);
                adapter.clearDatas();
                adapter.appendDatas(musicLists);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null) {
            mPlayer.destroy();
        }
        if (musicLists != null) {
            musicLists.clear();
        }
        super.onDestroy();
    }
}
