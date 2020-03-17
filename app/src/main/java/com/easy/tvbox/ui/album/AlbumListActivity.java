package com.easy.tvbox.ui.album;

import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.easy.aliplayer.view.AliyunVodPlayerView;
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
    AliyunVodPlayerView mAliyunVodPlayerView = null;
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
                .borderColorRes(R.color.touming)
                .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 0.01f)
                .shadowColorRes(R.color.touming)
                .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 0.001f)
                .noShimmer()
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
                    if (mAliyunVodPlayerView != null) {
                        if (mAliyunVodPlayerView.isPlaying()) {
                            mAliyunVodPlayerView.pause();
                            currentPlayingMusic = null;
                            currentPosition = 0;
                            refreshView(true);
                        }
                    }
                    RouteManager.goMusicDetailActivity(AlbumListActivity.this, position);
                }
            }
        });

        mViewBinding.ivPlayer.setOnClickListener(v -> {
            if (mAliyunVodPlayerView != null) {
                if (mAliyunVodPlayerView.isPlaying()) {
                    mAliyunVodPlayerView.pause();
                    refreshView(true);
                } else {
                    if (currentPlayingMusic != null) {
                        mAliyunVodPlayerView.startPlay();
                        refreshView(false);
                    } else {
                        startPayer();
                    }
                }
            }
        });

        mViewBinding.ivPlayer.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));
    }

    private void startPayer() {
        if (currentPosition < 0 || currentPosition >= musicLists.size()) {
            return;
        }
        currentPlayingMusic = musicLists.get(currentPosition);
        if (currentPlayingMusic != null) {
            MusicInfo musicInfo = currentPlayingMusic.getMusicInfo();
            if (musicInfo != null) {
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.setDataSource(musicInfo.getPlayUrl());
                    mAliyunVodPlayerView.prepare();
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


    private void playerFirstFrameStart() {
        Log.d("VcPlayer", "playerFirstFrameStart");
        isPlaying = true;
        mViewBinding.ivPlayer.setImageResource(R.drawable.button_player_pause);
    }

    private void playerError(ErrorInfo errorInfo) {
        Log.d("VcPlayer", "playerError==>" + errorInfo.getMsg());
        isPlaying = false;
        mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
    }

    /**
     * 播放完成
     */
    private void playerCompletion() {
        Log.d("VcPlayer", "playerCompletion");
        currentPosition++;
        if (currentPosition >= musicLists.size()) {
            isPlaying = false;
            mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
        } else {
            startPayer();
        }
    }

    /**
     * 播放状态切换
     */
    private void playStateSwitch(int playerState) {
        if (playerState == IPlayer.started) {//暂停
            isPlaying = true;
            mViewBinding.ivPlayer.setImageResource(R.drawable.button_player_pause);
            Log.d("VcPlayer", "playStateSwitch==>播放状态切换 playerState=" + playerState);
        } else if (playerState == IPlayer.paused) {//开始
            Log.d("VcPlayer", "playStateSwitch==>播放状态切换 playerState=" + playerState);
            mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
            isPlaying = false;
        }
    }

    private void initPayer() {
        mAliyunVodPlayerView = mViewBinding.videoView;
        //保持屏幕敞亮
        mAliyunVodPlayerView.setKeepScreenOn(true);
        mAliyunVodPlayerView.setAutoPlay(true);
        mAliyunVodPlayerView.setOnCompletionListener(() -> playerCompletion());
        mAliyunVodPlayerView.setOnErrorListener(errorInfo -> playerError(errorInfo));
        mAliyunVodPlayerView.setOnFirstFrameStartListener(() -> playerFirstFrameStart());
        mAliyunVodPlayerView.setOnPlayStateBtnClickListener(playerState -> playStateSwitch(playerState));
        mAliyunVodPlayerView.enableNativeLog();
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
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
        }
        if (musicLists != null) {
            musicLists.clear();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onResume();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.setAutoPlay(false);
            mAliyunVodPlayerView.onStop();
        }
    }
}
