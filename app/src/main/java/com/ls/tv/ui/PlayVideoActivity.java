package com.ls.tv.ui;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v4.app.FragmentActivity;
import android.widget.VideoView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ls.tv.R;
import com.ls.tv.enums.LeanbackPlaybackState;
import com.ls.tv.http.GlideApp;
import com.ls.tv.model.Movie;
import com.ls.tv.utils.Utils;

import java.util.ArrayList;

/**
 * 播放页：控制播放
 * 布局：
 * 1> VideoView是我们将播放视频内容的视图;
 * 2> PlaybackVideoFragment将显示用于控制视频的UI;
 * Created by liusong on 2018/1/17.
 */

public class PlayVideoActivity extends FragmentActivity {

    public static final String MOVIE = "Movie";

    private Movie mSelectedMovie;
    private VideoView mVideoView;
    //枚举标识播放状态: 初始化为空闲状态
    private LeanbackPlaybackState mPlaybackState = LeanbackPlaybackState.IDLE;

    private int mPosition = 0; //播放的时间位置
    private long mDuration = -1; //视频的播放时长
    private long mStartTimeMillis; //视屏开始的具体时间
    private int mCurrentItem; // index of current item

    private ArrayList<Movie> mItems = new ArrayList<Movie>();
    private int mCurrentPlaybackState = PlaybackState.STATE_NONE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback_overlay);
        receivedData();
        loadControlUI();

        loadViews();
    }

    private void receivedData() {
        mSelectedMovie = getIntent().getParcelableExtra(PlayVideoActivity.MOVIE);
    }

    /**
     * 视频控制界面
     */
    private void loadControlUI() {
        PlayVideoFragment playbackVideoFragment = new PlayVideoFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container_controls, playbackVideoFragment).commitAllowingStateLoss();
    }

    private void loadViews() {
        mVideoView = findViewById(R.id.videoView);
        mVideoView.setFocusable(false);
        mVideoView.setFocusableInTouchMode(false);

        setVideoPath(mSelectedMovie.getVideoUrl());
    }

    /**
     * 设置播放地址
     *
     * @param videoUrl
     */
    public void setVideoPath(String videoUrl) {
        setPosition(0);
        mVideoView.setVideoPath(videoUrl);
        mStartTimeMillis = 0; //???
        mDuration = Utils.getDuration(videoUrl);
    }

    //处理播放/暂停视频。
    public void playPause(boolean doPlay) {
        if (mPlaybackState == LeanbackPlaybackState.IDLE) {
            /* Callbacks for mVideoView */
            setupCallbacks();
        }

        if (doPlay && mPlaybackState != LeanbackPlaybackState.PLAYING) {
            mPlaybackState = LeanbackPlaybackState.PLAYING;
            if (mPosition > 0) {
                mVideoView.seekTo(mPosition);
            }
            mVideoView.start();
            mStartTimeMillis = System.currentTimeMillis();
        } else {
            mPlaybackState = LeanbackPlaybackState.PAUSED;
            int timeElapsedSinceStart = (int) (System.currentTimeMillis() - mStartTimeMillis);
            setPosition(mPosition + timeElapsedSinceStart);

            mVideoView.pause();
        }
    }

    private void setupCallbacks() {
        //错误
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mVideoView.stopPlayback();
                mPlaybackState = LeanbackPlaybackState.IDLE;
                return false;
            }
        });

        //准备播放
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
                    mVideoView.start();
                }
            }
        });

        //播放完成
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlaybackState = LeanbackPlaybackState.IDLE;
            }
        });
    }

    //只需快进/快进当前位置10秒钟
    public void fastForward() {
        if (mDuration != -1) {
            // Fast forward 10 seconds.
            setPosition(mVideoView.getCurrentPosition() + (10 * 1000));
            mVideoView.seekTo(mPosition);
        }
    }

    public void rewind() {
        // rewind 10 seconds
        setPosition(mVideoView.getCurrentPosition() - (10 * 1000));
        mVideoView.seekTo(mPosition);
    }

    /**
     * 设置播放的时间位置
     * position<=mDuration
     *
     * @param position
     */
    public void setPosition(int position) {
        if (position > mDuration) {
            mPosition = (int) mDuration;
        } else if (position < 0) {
            mPosition = 0;
            mStartTimeMillis = System.currentTimeMillis();
        } else {
            mPosition = position;
        }
        mStartTimeMillis = System.currentTimeMillis();
    }

    public int getPosition() {
        return mPosition;
    }

    //
    private void next(boolean autoPlay) {
        /* Video control part */
        if (++mCurrentItem >= mItems.size()) { // Current Item is set to next here mCurrentItem设置为上一个/下一个
            mCurrentItem = 0;
        }

        if (autoPlay) {
            mCurrentPlaybackState = PlaybackState.STATE_PAUSED;
        }

        Movie movie = mItems.get(mCurrentItem);
        if (movie != null) {
            setVideoPath(movie.getVideoUrl());
            mPlaybackState = LeanbackPlaybackState.PAUSED;
            playPause(autoPlay);
        }

        /* UI part */
//        playbackStateChanged();
        updatePlaybackRow(mCurrentItem);
    }

    private void prev(boolean autoPlay) {
        /* Video control part */
        if (--mCurrentItem < 0) { // Current Item is set to previous here
            mCurrentItem = mItems.size() - 1;
        }
        if (autoPlay) {
            mCurrentPlaybackState = PlaybackState.STATE_PAUSED;
        }

        Movie movie = mItems.get(mCurrentItem);
        if (movie != null) {
            setVideoPath(movie.getVideoUrl());
            mPlaybackState = LeanbackPlaybackState.PAUSED;
            playPause(autoPlay);
        }

        /* UI part */
//        playbackStateChanged();
        updatePlaybackRow(mCurrentItem);
    }

    private PlaybackControlsRow mPlaybackControlsRow;
    private static final boolean SHOW_IMAGE = true;
    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 240;

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    private ArrayObjectAdapter mRowsAdapter;

    public void updatePlaybackRow(int index) {
        if (mPlaybackControlsRow.getItem() != null) {
            Movie item = (Movie) mPlaybackControlsRow.getItem();
            item.setTitle(mItems.get(index).getTitle());
            item.setStudio(mItems.get(index).getStudio());

            mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
            /* total time is necessary to show video playing time progress bar */
            int duration = (int) Utils.getDuration(mItems.get(index).getVideoUrl());
            setDuration(duration);
            mPlaybackControlsRow.setTotalTime(duration);
            mPlaybackControlsRow.setCurrentTime(0);
            mPlaybackControlsRow.setBufferedProgress(0);
        } else {
        }
        if (SHOW_IMAGE) {
            int width = Utils.convertDpToPixel(this, CARD_WIDTH);
            int height = Utils.convertDpToPixel(this, CARD_HEIGHT);
            GlideApp.with(this)
                    .load(mItems.get(mCurrentItem).getCardImageUrl())
                    .centerCrop()
                    .override(width, height)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            mPlaybackControlsRow.setImageDrawable(resource);
                            mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
//                            mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
                        }
                    });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayback();
        mVideoView.suspend(); //将VideoView所占用的资源释放掉
        mVideoView.setVideoURI(null);
    }

    private void stopPlayback() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

}
