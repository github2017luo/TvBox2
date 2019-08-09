package com.easy.tvbox.ui.video;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.Constant;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.DailyList;
import com.easy.tvbox.bean.DailyPlay;
import com.easy.tvbox.bean.DailyRoll;
import com.easy.tvbox.bean.Respond;
import com.easy.tvbox.databinding.DailyVideoBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.utils.ToastUtils;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DailyVideoActivity extends BaseActivity<DailyVideoBinding> implements DailyVideoView {

    @Inject
    DailyVideoPresenter presenter;

    DailyList dailyData;
    DailyPlay dailyPlay;
    boolean isPlayRoll;//是否播放广告---还没到达知道时间轮播roll里的内容
    PlayerView playerView;
    ExoPlayer player;
    Player.EventListener eventListener;
    boolean hasNet;//是否有网络

    @Override
    public int getLayoutId() {
        return R.layout.daily_video;
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
            dailyData = JSON.parseObject(dataJson, DailyList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dailyData == null || dailyData.getDailyPlay() == null) {
            finish();
            return;
        }
        hasNet = NetworkUtils.isNetConnected(this);
        initExoPlayer();
        dailyPlay = dailyData.getDailyPlay();
        showBackground();
        handlePlayContent();
    }

    private void initExoPlayer() {
        playerView = mViewBinding.videoView;
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        player.setPlayWhenReady(true);
        eventListener = new Player.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                String stateString;
                // actually playing media
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    Log.d("", "onPlayerStateChanged: actually playing media");
                }
                switch (playbackState) {
                    case Player.STATE_IDLE:
                        stateString = "ExoPlayer.STATE_IDLE      -";
                        break;
                    case Player.STATE_BUFFERING:
                        stateString = "ExoPlayer.STATE_BUFFERING -";
                        break;
                    case Player.STATE_READY:
                        stateString = "ExoPlayer.STATE_READY     -";
                        break;
                    case Player.STATE_ENDED:
                        stateString = "ExoPlayer.STATE_ENDED     -";
                        break;
                    default:
                        stateString = "UNKNOWN_STATE             -";
                        break;
                }
                Log.d("DailyVideoActivity", "changed state to " + stateString + " playWhenReady: " + playWhenReady);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        };
        player.addListener(eventListener);
    }

    private void showBackground() {
        if (dailyPlay != null) {
            String type = dailyPlay.getType();
            if ("audio".equalsIgnoreCase(type)) {
                Glide.with(DailyVideoActivity.this)
                        .load(dailyPlay.getCover())
                        .error(R.drawable.error_icon)
                        .placeholder(R.drawable.error_icon)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(mViewBinding.ivBg);
                mViewBinding.ivBg.setVisibility(View.VISIBLE);
            } else {
                mViewBinding.ivBg.setVisibility(View.GONE);
            }
        }
    }

    public void startPayer(List<String> urls, boolean isLoop, long positionMs) {
        if (urls != null && urls.size() > 0) {
            ConcatenatingMediaSource source = new ConcatenatingMediaSource();
            for (String url : urls) {
                Uri uri = null;
                File downloadFile = presenter.getDownload(url);
                if (downloadFile != null) {
                    uri = Uri.fromFile(downloadFile);
                }
                if (uri != null) {
                    DataSpec dataSpec = new DataSpec(uri);
                    final FileDataSource fileDataSource = new FileDataSource();
                    try {
                        fileDataSource.open(dataSpec);
                    } catch (FileDataSource.FileDataSourceException e) {
                        e.printStackTrace();
                    }

                    DataSource.Factory factory = () -> fileDataSource;
                    MediaSource audioSource = new ExtractorMediaSource(fileDataSource.getUri(), factory,
                            new DefaultExtractorsFactory(), null, null);
                    source.addMediaSource(audioSource);
                } else if (hasNet) {
                    uri = Uri.parse(url);
                    if (uri != null) {
                        MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("exoplayer-codelab")).createMediaSource(uri);
                        source.addMediaSource(mediaSource);
                    }
                } else {
                    ToastUtils.showLong("网络异常");
                }
            }
            if (source.getSize() > 0) {
                if (isLoop) {
                    MediaSource loopingSource = new LoopingMediaSource(source);
                    player.prepare(loopingSource);
                } else {
                    player.prepare(source);
                    if (positionMs != 0) {
                        player.seekTo(positionMs);
                    }
                }
            }
        }
    }

    @Override
    public void playFormalVideo() {
        //倒计时结束后开始的，所以就是从头开始播放
        isPlayRoll = false;
        playDaily();
    }

    private void handlePlayContent() {
        if (dailyPlay != null) {
            if (Constant.isTestDownload) {
                isPlayRoll = true;
                playDaily();
                return;
            }
            long fixTime = dailyPlay.getFixed();
            long time = fixTime - getCurrentTime() / 1000;
            if (time > 0) { //
                isPlayRoll = true;
                presenter.downCount(time);
                playDaily();
            } else {
                isPlayRoll = false;
                findPlayPosition();
            }
        }
    }

    public long getCurrentTime() {
//        return CommonUtils.date2TimeStamp("2019-06-23 19:00:00");
        return System.currentTimeMillis();
    }

    /**
     * 查找播放的位置
     */
    private void findPlayPosition() {
        if (dailyPlay != null) {
            long currentTime = getCurrentTime() / 1000;
            List<DailyRoll> formals = dailyPlay.getFormal();
            if (formals != null && formals.size() > 0) {
                long seek = 0;
                List<String> urls = new ArrayList<>();
                for (int i = 0; i < formals.size(); i++) {
                    DailyRoll formal = formals.get(i);
                    long startTime = formal.getStart();
                    long endTime = formal.getEnd();
                    if (currentTime < endTime) {
                        urls.add(formal.getSource());
                        seek = (currentTime - startTime) * 1000;
                    }
                }
                startPayer(urls, false, seek);
            }
        }
    }

    public void playDaily() {
        if (dailyPlay != null) {
            if (Constant.isTestDownload) {
                List<DailyRoll> rolls = dailyPlay.getRoll();
                if (rolls != null && rolls.size() > 0) {
                    List<String> urls = new ArrayList<>();
                    urls.add(rolls.get(0).getSource());
                    startPayer(urls, true, 0);
                }
                return;
            }
            if (isPlayRoll) {
                List<DailyRoll> rolls = dailyPlay.getRoll();
                if (rolls != null && rolls.size() > 0) {
                    List<String> urls = new ArrayList<>();
                    for (DailyRoll roll : rolls) {
                        urls.add(roll.getSource());
                    }
                    startPayer(urls, true, 0);
                }
            } else {
                List<DailyRoll> formals = dailyPlay.getFormal();
                if (formals != null && formals.size() > 0) {
                    List<String> urls = new ArrayList<>();
                    for (DailyRoll roll : formals) {
                        urls.add(roll.getSource());
                    }
                    startPayer(urls, false, 0);
                }
            }
        }
    }

    private void releasePlayer() {
        if (player != null) {
            if (eventListener != null) {
                player.removeListener(eventListener);
            }
            player.release();
            player = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.cancel();
        }
        releasePlayer();
    }
}
