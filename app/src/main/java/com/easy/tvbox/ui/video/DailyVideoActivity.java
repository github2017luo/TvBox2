package com.easy.tvbox.ui.video;

import android.content.Intent;
import android.net.Uri;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseActivity;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.Daily;
import com.easy.tvbox.bean.DailyItem;
import com.easy.tvbox.bean.PlayProgress;
import com.easy.tvbox.databinding.DailyVideoBinding;
import com.easy.tvbox.event.MtMessage;
import com.easy.tvbox.utils.DimensUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.owen.focus.FocusBorder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DailyVideoActivity extends BaseActivity<DailyVideoBinding> implements DailyVideoView {

    @Inject
    DailyVideoPresenter presenter;
    List<DailyItem> dailyItems;
    PlayerView playerView;
    ExoPlayer player;
    Player.EventListener eventListener;
    int width;
    FocusBorder mFocusBorder;
    PlayerControlView controlView;
    LinearLayout scrollView;
    int currentPlayPosition = 0;
    DefaultControlDispatcher dispatcher;
    boolean isFirstLoad = true;
    Disposable disposable;

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
        if (isConnect && player != null) {
            player.retry();
        }
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
        mFocusBorder = new FocusBorder.Builder()
                .asColor()
                .borderColorRes(R.color.actionbar_color)
                .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 3f)
                .shadowColorRes(R.color.green_bright)
                .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 5f)
                .build(this);
        width = DimensUtils.dp2px(this, 150);
        Intent intent = getIntent();
        String dataJson = intent.getStringExtra("data");
        try {
            Daily daily = JSON.parseObject(dataJson, Daily.class);
            if (daily != null) {
                dailyItems = daily.getDailyItems();
                List<PlayProgress> playProgresses = DataManager.getInstance().queryPlayProgress();
                if (playProgresses != null && playProgresses.size() > 0) {
                    for (DailyItem dailyItem : dailyItems) {
                        for (PlayProgress playProgress : playProgresses) {
                            if (dailyItem.getVid().equals(playProgress.getId())) {
                                dailyItem.setProgress(playProgress.getProgressInt());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dailyItems == null || dailyItems.isEmpty()) {
            finish();
            return;
        }
        initExoPlayer();
        findPlayPosition(dailyItems, 0);
        addVideoView();
    }

    protected void onMoveFocusBorder(View focusedView, float scale) {
        if (null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale));
        }
    }

    private void addVideoView() {
        scrollView = findViewById(R.id.scrollView);
        for (int i = 0; i < dailyItems.size(); i++) {
            DailyItem dailyItem = dailyItems.get(i);
            dailyItem.setPosition(i);
            View item = LayoutInflater.from(this).inflate(R.layout.video_item, null);
            item.setTag(dailyItem);
            ProgressBar progressBar = item.findViewById(R.id.progressBar);
            progressBar.setMax(dailyItem.getDurationMForLong());
            progressBar.setProgress(dailyItem.getProgress());
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickVideo(v, false);
                }
            });
            item.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));
            ImageView videoPreImage = item.findViewById(R.id.videoPreImage);
            Glide.with(this).load(dailyItem.getFaceurl()).into(videoPreImage);
            TextView videoTitle = item.findViewById(R.id.videoTitle);
            videoTitle.setText(TextUtils.isEmpty(dailyItem.getTitle()) ? "无标题" : dailyItem.getTitle());
            scrollView.addView(item, width, width);
        }
    }

    private void clickVideo(View item, boolean force) {
        Object object = item.getTag();
        if (object instanceof DailyItem) {
            DailyItem dailyItem = (DailyItem) object;
            if (!force && dailyItem.getPosition() == currentPlayPosition) {
                return;
            }
            if (dispatcher != null) {
                dispatcher.dispatchSeekTo(player, dailyItem.getPosition(), dailyItem.getProgress());
            }
        }
    }

    public void startInterval() {
        Observable.interval(0, 2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable dis) {
                        disposable = dis;
                    }

                    @Override
                    public void onNext(Long number) {
                        if (player != null && dailyItems != null && scrollView != null) {
                            if (player.getCurrentPosition() > 100) {
                                View view = scrollView.getChildAt(player.getCurrentWindowIndex());
                                if (view != null) {
                                    ProgressBar progressBar = view.findViewById(R.id.progressBar);
                                    progressBar.setProgress((int) player.getCurrentPosition());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //取消订阅
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void seekTo(View item, DailyItem dailyItem, int windowIndex, long positionMs) {
        if (currentPlayPosition == windowIndex) {
            dailyItem.setProgress((int) positionMs);
            if (mFocusBorder.isVisible() && item != null) {
                ProgressBar progressBar = item.findViewById(R.id.progressBar);
                progressBar.setProgress(dailyItem.getProgress());
            }
        } else {
            DailyItem lastItem = dailyItems.get(currentPlayPosition);
            View lastView = scrollView.getChildAt(currentPlayPosition);
            lastItem.setProgress((int) player.getCurrentPosition());
            if (mFocusBorder.isVisible() && lastView != null) {
                ProgressBar progressBar = lastView.findViewById(R.id.progressBar);
                progressBar.setProgress(lastItem.getProgress());
            }
            currentPlayPosition = windowIndex;
            if (mFocusBorder.isVisible() && item != null) {
                onMoveFocusBorder(item, 1.1f);
            }
            positionMs = dailyItem.getProgress();
        }
        player.seekTo(windowIndex, positionMs);
        Log.d("SeekTo", "windowIndex:" + windowIndex + " positionMs:" + positionMs);
        Log.d("SeekTo", dailyItem.toString());
    }

    private void initExoPlayer() {
        playerView = mViewBinding.videoView;
        controlView = findViewById(R.id.exo_controller);
        View playBtn = controlView.findViewById(R.id.exo_play);
        playBtn.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));
        View pauseBtn = controlView.findViewById(R.id.exo_pause);
        pauseBtn.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));

        View rewBtn = controlView.findViewById(R.id.exo_rew);
        rewBtn.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));
        View ffwdBtn = controlView.findViewById(R.id.exo_ffwd);
        ffwdBtn.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));

        playerView.setControllerShowTimeoutMs(8000);
        dispatcher = new DefaultControlDispatcher() {
            @Override
            public boolean dispatchSeekTo(Player player, int windowIndex, long positionMs) {
                if (scrollView != null) {
                    View view = scrollView.getChildAt(windowIndex);
                    if (view != null) {
                        Object object = view.getTag();
                        if (object instanceof DailyItem) {
                            DailyItem dailyItem = (DailyItem) object;
                            seekTo(view, dailyItem, windowIndex, positionMs);
                        }
                    }
                } else {
                    player.seekTo(windowIndex, positionMs);
                }
                return true;
            }
        };
        playerView.setControlDispatcher(dispatcher);
        playerView.setControllerVisibilityListener(visibility -> {
            if (mFocusBorder != null) {
                mFocusBorder.setVisible(View.VISIBLE == visibility);
            }
        });
        eventListener = new Player.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
//                if (scrollView != null) {
//                    View view = scrollView.getChildAt(player.getCurrentWindowIndex());
//                    Object object = view.getTag();
//                    if (object instanceof DailyItem) {
//                        DailyItem dailyItem = (DailyItem) object;
//                        dailyItem.setProgress(timeline.getPeriodPosition());
//                    }
//                }
                Log.d("EventListener", "onTimelineChanged:");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.d("EventListener", "onTracksChanged:");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.d("EventListener", "onLoadingChanged:");
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                String stateString;
                // actually playing media
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    Log.d("EventListener", "onPlayerStateChanged: actually playing media");
                }
                switch (playbackState) {
                    case Player.STATE_IDLE:
                        stateString = "ExoPlayer.STATE_IDLE      -";
                        break;
                    case Player.STATE_BUFFERING:
                        stateString = "ExoPlayer.STATE_BUFFERING -";
                        if (isFirstLoad && dailyItems != null && dailyItems.size() > 0) {
                            isFirstLoad = false;
                            startInterval();
                            DailyItem dailyItem = dailyItems.get(0);
                            dispatcher.dispatchSeekTo(player, dailyItem.getPosition(), dailyItem.getProgress());
                        }
                        break;
                    case Player.STATE_READY:
                        stateString = "ExoPlayer.STATE_READY     -";
                        break;
                    case Player.STATE_ENDED:
                        stateString = "ExoPlayer.STATE_ENDED     -";
                        finish();
                        break;
                    default:
                        stateString = "UNKNOWN_STATE             -";
                        break;
                }
                Log.d("EventListener", "changed state to " + stateString + " playWhenReady: " + playWhenReady);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.d("EventListener", "onRepeatModeChanged:");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                Log.d("EventListener", "onShuffleModeEnabledChanged:");
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.d("EventListener", "onPlayerError:");
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.d("EventListener", "onPositionDiscontinuity:");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.d("EventListener", "onPlaybackParametersChanged:");
            }

            @Override
            public void onSeekProcessed() {
                Log.d("EventListener", "onSeekProcessed:");
            }
        };
        player = ExoPlayerFactory.newSimpleInstance(this);
        player.addListener(eventListener);
        playerView.setPlayer(player);
        player.setPlayWhenReady(true);
    }

    public void startPayer(List<String> urls, long positionMs) {
        if (urls != null && urls.size() > 0) {
            ConcatenatingMediaSource source = new ConcatenatingMediaSource();
            for (String url : urls) {
                Uri uri = Uri.parse(url);
                if (uri != null) {
                    MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("exoplayer-codelab")).createMediaSource(uri);
                    source.addMediaSource(mediaSource);
                }
            }
            if (source.getSize() > 0) {
                player.prepare(source);
                if (positionMs != 0) {
                    player.seekTo(positionMs);
                }
            }
        }
    }

    /**
     * 查找播放的位置
     */
    private void findPlayPosition(List<DailyItem> items, long position) {
        List<String> urls = new ArrayList<>();
        for (DailyItem dailyItem : items) {
            urls.add(dailyItem.getVideourl());
        }
        startPayer(urls, position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoPlayEvent(MtMessage message) {
        if (message != null && dailyItems != null && dailyItems.size() > 0) {
            List<DailyItem> newItems = new ArrayList<>();
            boolean add = false;
            for (DailyItem dailyItem : dailyItems) {
                if (dailyItem.getVid().equalsIgnoreCase(message.getVid())) {
                    add = true;
                }
                if (add) {
                    newItems.add(dailyItem);
                }
            }
            if (newItems.size() > 0) {
                findPlayPosition(newItems, message.getTimer());
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
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (dailyItems != null && player != null) {
            dailyItems.get(player.getCurrentWindowIndex()).setProgress((int) player.getCurrentPosition());
            List<PlayProgress> playProgresses = new ArrayList<>();
            for (DailyItem dailyItem : dailyItems) {
                PlayProgress playProgress = new PlayProgress();
                playProgress.setId(dailyItem.getVid());
                playProgress.setProgress(dailyItem.getProgress() + "");
                playProgresses.add(playProgress);
            }
            DataManager.getInstance().updateProgress(playProgresses);
        }
        super.onDestroy();
        releasePlayer();
    }
}
