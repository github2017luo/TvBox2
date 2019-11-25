package com.easy.tvbox.ui.music;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.easy.aliplayer.view.AliyunVodPlayerView;
import com.easy.tvbox.R;
import com.easy.tvbox.base.App;
import com.easy.tvbox.base.BaseFragment;
import com.easy.tvbox.base.BasePresenter;
import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.FocusBorderHelper;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.bean.Account;
import com.easy.tvbox.bean.MusicData;
import com.easy.tvbox.bean.MusicInfo;
import com.easy.tvbox.bean.MusicList;
import com.easy.tvbox.databinding.MusicFragmentBinding;
import com.easy.tvbox.http.NetworkUtils;
import com.easy.tvbox.tvview.tvRecycleView.SimpleOnItemListener;
import com.easy.tvbox.tvview.tvRecycleView.TvRecyclerView;
import com.easy.tvbox.utils.DimensUtils;
import com.owen.focus.FocusBorder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.easy.tvbox.base.Constant.OPEN_PLAYER;

public class MusicFragment extends BaseFragment<MusicFragmentBinding> implements MusicFragmentView {

    @Inject
    MusicFragmentPresenter presenter;

    MusicFragmentAdapter adapter;
    public static List<MusicList> musicLists = new ArrayList<>();
    public static List<MusicList> mvLists = new ArrayList<>();
    int page = 0;
    Account account;
    int videoId = 1;//1 音乐，2 MV
    AliyunVodPlayerView mAliyunVodPlayerView = null;
    int currentPosition = 0;//播放的位置
    MusicList currentPlayingMusic;
    boolean isPlaying;
    FocusBorder mFocusBorder;
    MusicData musicDatas;
    float scale = 1.03f;

    public static MusicFragment getInstance(int type) {
        MusicFragment musicFragment = new MusicFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        musicFragment.setArguments(bundle);
        return musicFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof FocusBorderHelper) {
            mFocusBorder = ((FocusBorderHelper) getActivity()).getFocusBorder();
        }
    }

    protected void onMoveFocusBorder(View focusedView, float scale) {
        if (null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            videoId = args.getInt("type");
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.music_fragment;
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
    public void initView(View view) {
        account = DataManager.getInstance().queryAccount();
        if (videoId == 1) {
            if (OPEN_PLAYER) {
                initAliyunPlayerView();
            }
        }

        int[] size = DimensUtils.getWidthHeight(App.getApp());
        int bigWidth = size[0] - DimensUtils.dp2px(App.getApp(), 290 + 10);
        int smallWidth = size[0] - DimensUtils.dp2px(App.getApp(), 290 + 10 + 40);
        scale = bigWidth * 1.0f / smallWidth;


        adapter = new MusicFragmentAdapter(getContext());
        mViewBinding.recyclerView.setSpacingWithMargins(10, 3);
        mViewBinding.recyclerView.setAdapter(adapter);
        mViewBinding.recyclerView.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                onMoveFocusBorder(itemView, scale);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                if (videoId == 1) {
                    if (musicLists != null && musicLists.size() > 0 && position < musicLists.size()) {
                        if (mAliyunVodPlayerView != null) {
                            if (mAliyunVodPlayerView.isPlaying()) {
                                mAliyunVodPlayerView.pause();
                                currentPlayingMusic = null;
                                currentPosition = 0;
                                refreshView(true);
                            }
                        }
                        RouteManager.goMusicDetailActivity(getContext(), position);
                    }
                } else {
                    if (mvLists != null && mvLists.size() > 0 && position < mvLists.size()) {
                        List<MusicInfo> musicInfos = new ArrayList<>();
                        MusicInfo musicInfo = mvLists.get(position).getMusicInfo();
                        if (musicInfo != null) {
                            musicInfos.add(musicInfo);
                        }
                        RouteManager.goMusicVideoActivity(getContext(), JSON.toJSONString(musicInfos));
                    }
                }
            }
        });
        mViewBinding.recyclerView.setOnLoadMoreListener(() -> {
            mViewBinding.recyclerView.setLoadingMore(true); //正在加载数据
            presenter.queryMusic(++page, account.getShopNo(), videoId);
            if (musicDatas != null) {
                return !musicDatas.isLast();
            }
            return false; //是否还有更多数据
        });

        mViewBinding.tvAlbum.setOnClickListener(v -> {
            if (mAliyunVodPlayerView != null) {
                if (mAliyunVodPlayerView.isPlaying()) {
                    mAliyunVodPlayerView.pause();
                    currentPlayingMusic = null;
                    currentPosition = 0;
                    refreshView(true);
                }
            }
            RouteManager.goAlbumActivity(getContext());
        });
        mViewBinding.tvAlbum.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.05f));
        mViewBinding.ivPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoId == 1) {
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
                } else {
                    if (mvLists != null && mvLists.size() > 0) {
                        List<MusicInfo> musicInfos = new ArrayList<>();
                        for (MusicList mvList : mvLists) {
                            MusicInfo musicInfo = mvList.getMusicInfo();
                            if (musicInfo != null) {
                                musicInfos.add(musicInfo);
                            }
                        }
                        RouteManager.goMusicVideoActivity(getContext(), JSON.toJSONString(musicInfos));
                    }
                }
            }
        });
        mViewBinding.ivPlayer.setOnFocusChangeListener((v, hasFocus) -> onMoveFocusBorder(v, 1.1f));
        networkChange(NetworkUtils.isNetConnected(getContext()));
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

    private void initAliyunPlayerView() {
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

    private void playerFirstFrameStart() {
        Log.d("VcPlayer", "playerFirstFrameStart");
        isPlaying = true;
        mViewBinding.ivPlayer.setImageResource(R.drawable.button_player_pause);
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

    private void playerError(ErrorInfo errorInfo) {
        Log.d("VcPlayer", "playerError==>" + errorInfo.getMsg());
        isPlaying = false;
        mViewBinding.ivPlayer.setImageResource(R.drawable.button_stop_pause);
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

    public void networkChange(boolean isConnect) {
        if (isConnect) {
            presenter.queryMusic(page, account.getShopNo(), videoId);
        }
    }

    @Override
    public void queryMusicCallback(MusicData data, int videoId) {
        if (data != null) {
            musicDatas = data;
            List<MusicList> musicDataContent = data.getContent();
            if (musicDataContent != null && musicDataContent.size() > 0) {
                if (videoId == 1) {
                    musicLists.addAll(musicDataContent);
                } else {
                    mvLists.addAll(musicDataContent);
                }
                adapter.appendDatas(musicDataContent);
                mViewBinding.recyclerView.setLoadingMore(false); //加载数据完毕
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
        }
        if (videoId == 1) {
            musicLists.clear();
        } else {
            mvLists.clear();
        }
        super.onDestroy();
    }

    public void choose(boolean isCurrent) {
        if (isCurrent) {

        } else {
            if (videoId == 1 && isPlaying) {
                mViewBinding.ivPlayer.performClick();
            }
        }
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


