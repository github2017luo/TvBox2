package com.easy.tvbox.ui.video;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.MusicInfo;
import com.easy.tvbox.databinding.MusicVideoBinding;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MusicVideoActivity extends BaseActivity<MusicVideoBinding> implements MusicVideoView {

    @Inject
    MusicVideoPresenter presenter;

    PlayerView playerView;
    ExoPlayer player;
    Player.EventListener eventListener;

    List<MusicInfo> musicInfos;

    @Override
    public int getLayoutId() {
        return R.layout.music_video;
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
            musicInfos = JSON.parseArray(dataJson, MusicInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (musicInfos == null || musicInfos.isEmpty()) {
            finish();
            return;
        }
        initExoPlayer();

        List<String> urls = new ArrayList<>();
        for (MusicInfo info : musicInfos) {
            urls.add(info.getPlayUrl());
        }
        startPayer(urls, false, 0);
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

    public void startPayer(List<String> urls, boolean isLoop, long positionMs) {
        if (urls != null && urls.size() > 0) {
            ConcatenatingMediaSource source = new ConcatenatingMediaSource();
            for (String url : urls) {
                Uri uri = Uri.parse(url);
                MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("exoplayer-codelab")).createMediaSource(uri);
                source.addMediaSource(mediaSource);
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
        releasePlayer();
    }
}
