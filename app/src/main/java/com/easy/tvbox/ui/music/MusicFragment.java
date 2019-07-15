package com.easy.tvbox.ui.music;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
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
import com.owen.focus.FocusBorder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MusicFragment extends BaseFragment<MusicFragmentBinding> implements MusicFragmentView {

    @Inject
    MusicFragmentPresenter presenter;

    MusicFragmentAdapter adapter;
    public static List<MusicList> musicLists = new ArrayList<>();
    public static List<MusicList> mvLists = new ArrayList<>();
    int page = 0;
    Account account;
    int videoId = 1;//1 音乐，2 MV
    AliVcMediaPlayer mPlayer;
    int currentPosition = 0;//播放的位置
    MusicList currentPlayingMusic;
    boolean isPlaying;
    FocusBorder mFocusBorder;
    MusicData musicDatas;

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

    protected void onMoveFocusBorder(View focusedView, float scale, float roundRadius) {
        if (null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale, roundRadius));
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
            initPayer();
        }
        adapter = new MusicFragmentAdapter(getContext());
        mViewBinding.recyclerView.setSpacingWithMargins(10, 3);
        mViewBinding.recyclerView.setAdapter(adapter);
        mViewBinding.recyclerView.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                onMoveFocusBorder(itemView, 1.1f);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                if (videoId == 1) {
                    if (musicLists != null && musicLists.size() > 0 && position < musicLists.size()) {
                        if (mPlayer != null) {
                            if (mPlayer.isPlaying()) {
                                mPlayer.stop();
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
        mViewBinding.recyclerView.setOnLoadMoreListener(new TvRecyclerView.OnLoadMoreListener() {
            @Override
            public boolean onLoadMore() {
                mViewBinding.recyclerView.setLoadingMore(true); //正在加载数据
                presenter.queryMusic(++page, account.getShopNo(), videoId);
                if (musicDatas != null) {
                    return !musicDatas.isLast();
                }
                return false; //是否还有更多数据
            }
        });

        mViewBinding.tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteManager.goAlbumActivity(getContext());
            }
        });
        mViewBinding.tvAlbum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onMoveFocusBorder(v, 1.1f);
            }
        });
        mViewBinding.ivPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoId == 1) {
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
        mViewBinding.ivPlayer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onMoveFocusBorder(v, 1.1f);
            }
        });
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

    private void initPayer() {
        mPlayer = new AliVcMediaPlayer(getContext(), mViewBinding.surfaceView);

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
        if (mPlayer != null) {
            mPlayer.destroy();
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
}


