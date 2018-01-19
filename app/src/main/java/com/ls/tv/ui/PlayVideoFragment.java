package com.ls.tv.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.VideoSupportFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ls.tv.http.GlideApp;
import com.ls.tv.model.Movie;
import com.ls.tv.ui.presenter.CardPresenter;
import com.ls.tv.ui.presenter.DetailsDescriptionPresenter;
import com.ls.tv.utils.DataUtils;
import com.ls.tv.utils.LogUtils;
import com.ls.tv.utils.Utils;

import java.util.ArrayList;

/**
 * 播放控制页面:UI更新部分
 * Handles video playback with media controls.
 * Created by liusong on 2018/1/17.
 */

public class PlayVideoFragment extends VideoSupportFragment {
    private Movie mSelectedMovie;
    private ArrayObjectAdapter mRowsAdapter; // 总容器

    private PlaybackControlsRow mPlaybackControlsRow;
    private ArrayObjectAdapter mPrimaryActionsAdapter; // 主要按钮UI
    private ArrayObjectAdapter mSecondaryActionsAdapter; //次要按钮UI

    //控制动作
    private PlaybackControlsRow.PlayPauseAction mPlayPauseAction; //暂停
    private PlaybackControlsRow.RepeatAction mRepeatAction; // 重复
    private PlaybackControlsRow.ThumbsUpAction mThumbsUpAction; //满意，赞
    private PlaybackControlsRow.ThumbsDownAction mThumbsDownAction; //反对，取消赞
    private PlaybackControlsRow.ShuffleAction mShuffleAction; //搁置
    private PlaybackControlsRow.SkipNextAction mSkipNextAction; //跳过下一个
    private PlaybackControlsRow.SkipPreviousAction mSkipPreviousAction; //跳过前一个
    private PlaybackControlsRow.FastForwardAction mFastForwardAction; //快进
    private PlaybackControlsRow.RewindAction mRewindAction; //回退
    private PlaybackControlsRow.HighQualityAction mHighQualityAction; //高质量
    private PlaybackControlsRow.ClosedCaptioningAction mClosedCaptioningAction; //隐藏字幕
    private PlaybackControlsRow.MoreActions mMoreActions; //更多

    //other
    private int mCurrentPlaybackState = PlaybackState.STATE_NONE;
    // 缓冲时间？？？
    private static final int SIMULATED_BUFFERED_TIME = 10 * 1000;
    private static final int DEFAULT_UPDATE_PERIOD = 1000;
    private static final int UPDATE_PERIOD = 16;
    private Runnable mRunnable;
    private Handler mHandler;
    private MediaController mMediaController;
    private ArrayList<Movie> mItems = new ArrayList<Movie>();
    private int mCurrentItem; // index of current item
    private static final boolean SHOW_IMAGE = true;

    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 240;


    private void receivedData() {
        mSelectedMovie = getActivity().getIntent().getParcelableExtra(PlayVideoActivity.MOVIE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMediaController = getActivity().getMediaController();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.i(this, "onCreate");
        super.onCreate(savedInstanceState);
        receivedData();
        //背景类型：高亮透明背景
        setBackgroundType(VideoSupportFragment.BG_LIGHT);

        //播放时自动隐藏控制界面
        setFadingEnabled(true);
//        setControlsOverlayAutoHideEnabled(true);

        initData();
        setUpRows();
    }

    private void initData() {
        mHandler = new Handler();
    }

    /**
     * 设置内容行
     */
    private void setUpRows() {
        ClassPresenterSelector ps = new ClassPresenterSelector();
        //控制界面UI
        PlaybackControlsRowPresenter playbackControlsRowPresenter = new PlaybackControlsRowPresenter(new DetailsDescriptionPresenter());

        //添加控制row
        ps.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        //添加ListRow
        ps.addClassPresenter(ListRow.class, new ListRowPresenter());

        //构建总容器
        mRowsAdapter = new ArrayObjectAdapter(ps);

        //第一行：添加控制行；
        addPlaybackControlsRow();
        //第二行：推荐视频列表
        addOtherRows();

        /* 控制按钮的click */
        playbackControlsRowPresenter.setOnActionClickedListener(playbackControlsListener);

        setAdapter(mRowsAdapter);
    }

    /**
     * 控制行
     */
    private void addPlaybackControlsRow() {
        //总控制容器UI
        mPlaybackControlsRow = new PlaybackControlsRow(mSelectedMovie);
        //添加到总容器
        mRowsAdapter.add(mPlaybackControlsRow);

        //控制控件容器构建
        ControlButtonPresenterSelector presenterSelector = new ControlButtonPresenterSelector();
        //主要按钮UI
        mPrimaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        //次要按钮UI
        mSecondaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);

        //将两个控制行ui添加到总控
        mPlaybackControlsRow.setPrimaryActionsAdapter(mPrimaryActionsAdapter);
        mPlaybackControlsRow.setSecondaryActionsAdapter(mSecondaryActionsAdapter);

        //初始化各种控制动作
        Activity activity = getActivity();
        mPlayPauseAction = new PlaybackControlsRow.PlayPauseAction(activity);
        mRepeatAction = new PlaybackControlsRow.RepeatAction(activity);
        mThumbsUpAction = new PlaybackControlsRow.ThumbsUpAction(activity);
        mThumbsDownAction = new PlaybackControlsRow.ThumbsDownAction(activity);
        mShuffleAction = new PlaybackControlsRow.ShuffleAction(activity);
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(activity);
        mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(activity);
        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(activity);
        mRewindAction = new PlaybackControlsRow.RewindAction(activity);
        mHighQualityAction = new PlaybackControlsRow.HighQualityAction(activity);
        mClosedCaptioningAction = new PlaybackControlsRow.ClosedCaptioningAction(activity);
        mMoreActions = new PlaybackControlsRow.MoreActions(activity);

        /* PrimaryAction setting(控制动作装填到主要控制UI) */
        mPrimaryActionsAdapter.add(mSkipPreviousAction);
        mPrimaryActionsAdapter.add(mRewindAction);
        mPrimaryActionsAdapter.add(mPlayPauseAction);
        mPrimaryActionsAdapter.add(mFastForwardAction);
        mPrimaryActionsAdapter.add(mSkipNextAction);

        /* SecondaryAction setting(控制动作装填到次要控制UI) */
        mSecondaryActionsAdapter.add(mThumbsUpAction);
        mSecondaryActionsAdapter.add(mThumbsDownAction);
        mSecondaryActionsAdapter.add(mRepeatAction);
        mSecondaryActionsAdapter.add(mShuffleAction);
        mSecondaryActionsAdapter.add(mHighQualityAction);
        mSecondaryActionsAdapter.add(mClosedCaptioningAction);
        mSecondaryActionsAdapter.add(mMoreActions);
    }

    /**
     * 其他内容列表
     */
    private void addOtherRows() {
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (int i = 0; i < 6; i++) {
            Movie movie = new Movie();
            DataUtils.setCardImageUrl(i, movie);
            movie.setTitle("title" + i);
            movie.setStudio("studio" + i);
            movie.setDescription("description" + i);
            listRowAdapter.add(movie);
        }

        HeaderItem header = new HeaderItem(0, "OtherRows");
        mRowsAdapter.add(new ListRow(header, listRowAdapter));
    }

    private void togglePlayback(boolean playPause) {
        /* Video control part */
        ((PlayVideoActivity) getActivity()).playPause(playPause);

        /* UI control part */
        playbackStateChanged();
    }


    public void playbackStateChanged() {
        if (mCurrentPlaybackState != PlaybackState.STATE_PLAYING) {
            mCurrentPlaybackState = PlaybackState.STATE_PLAYING;
            startProgressAutomation();
            setFadingEnabled(true);
            mPlayPauseAction.setIndex(PlaybackControlsRow.PlayPauseAction.PAUSE);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlaybackControlsRow.PlayPauseAction.PAUSE));
            notifyChanged(mPlayPauseAction);
        } else if (mCurrentPlaybackState != PlaybackState.STATE_PAUSED) {
            mCurrentPlaybackState = PlaybackState.STATE_PAUSED;
            stopProgressAutomation();
            //setFadingEnabled(false); // if set to false, PlaybackcontrolsRow will always be on the screen
            mPlayPauseAction.setIndex(PlaybackControlsRow.PlayPauseAction.PLAY);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlaybackControlsRow.PlayPauseAction.PLAY));
            notifyChanged(mPlayPauseAction);
        }

        int currentTime = ((PlayVideoActivity) getActivity()).getPosition();
        mPlaybackControlsRow.setCurrentTime(currentTime);
        mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);

    }

    private void notifyChanged(Action action) {
        ArrayObjectAdapter adapter = mPrimaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
        adapter = mSecondaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
    }

    //更新视频的当前时间状态
    private void startProgressAutomation() {
        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    int updatePeriod = getUpdatePeriod();
                    int currentTime = mPlaybackControlsRow.getCurrentTime() + updatePeriod;
                    int totalTime = mPlaybackControlsRow.getTotalTime();
                    mPlaybackControlsRow.setCurrentTime(currentTime);
                    mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);

                    if (totalTime > 0 && totalTime <= currentTime) {
                        stopProgressAutomation();
                        //next(true);
                    } else {
                        mHandler.postDelayed(this, updatePeriod);
                    }
                }
            };
            mHandler.postDelayed(mRunnable, getUpdatePeriod());
        }
    }

    private int getUpdatePeriod() {
        if (getView() == null || mPlaybackControlsRow.getTotalTime() <= 0 || getView().getWidth() == 0) {
            return DEFAULT_UPDATE_PERIOD;
        }
        return Math.max(UPDATE_PERIOD, mPlaybackControlsRow.getTotalTime() / getView().getWidth());
    }

    private void stopProgressAutomation() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
            mRunnable = null;
        }
    }

    /**
     * 快进
     */
    private void fastForward() {
        /* Video control part */
        ((PlayVideoActivity) getActivity()).fastForward();

        /* UI part */
        int currentTime = ((PlayVideoActivity) getActivity()).getPosition();
        mPlaybackControlsRow.setCurrentTime(currentTime);
        mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);
    }

    /**
     * 回退
     */
    private void rewind() {
        /* Video control part */
        ((PlayVideoActivity) getActivity()).rewind();

        /* UI part */
        int currentTime = ((PlayVideoActivity) getActivity()).getPosition();
        mPlaybackControlsRow.setCurrentTime(currentTime);
        mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);
    }

    //更新视频内容的DetailsDescription信息
    private void updatePlaybackRow(int index) {
        if (mPlaybackControlsRow.getItem() != null) {
            Movie item = (Movie) mPlaybackControlsRow.getItem();
            item.setTitle(mItems.get(mCurrentItem).getTitle());
            item.setStudio(mItems.get(mCurrentItem).getStudio());

            mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
            /* total time is necessary to show video playing time progress bar */
            int duration = (int) Utils.getDuration(mItems.get(mCurrentItem).getVideoUrl());
            mPlaybackControlsRow.setTotalTime(duration);
            mPlaybackControlsRow.setCurrentTime(0);
            mPlaybackControlsRow.setBufferedProgress(0);
        }
        if (SHOW_IMAGE) {
            int width = Utils.convertDpToPixel(getActivity(), CARD_WIDTH);
            int height = Utils.convertDpToPixel(getActivity(), CARD_HEIGHT);
            GlideApp.with(getActivity())
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

    /**
     * 播放控制按钮的click事件
     */
    private OnActionClickedListener playbackControlsListener = new OnActionClickedListener() {
        @Override
        public void onActionClicked(Action action) {
            if (action.getId() == mPlayPauseAction.getId()) {
                    /* PlayPause action */
                if (mPlayPauseAction.getIndex() == PlaybackControlsRow.PlayPauseAction.PLAY) {
                    mMediaController.getTransportControls().play();
                } else if (mPlayPauseAction.getIndex() == PlaybackControlsRow.PlayPauseAction.PAUSE) {
                    mMediaController.getTransportControls().pause();
                }
            } else if (action.getId() == mSkipNextAction.getId()) {
                    /* SkipNext action */
                mMediaController.getTransportControls().skipToNext();
            } else if (action.getId() == mSkipPreviousAction.getId()) {
                    /* SkipPrevious action */
                mMediaController.getTransportControls().skipToPrevious();
            } else if (action.getId() == mFastForwardAction.getId()) {
                    /* FastForward action  */
                mMediaController.getTransportControls().fastForward();
            } else if (action.getId() == mRewindAction.getId()) {
                    /* Rewind action */
                mMediaController.getTransportControls().rewind();
            }
            if (action instanceof PlaybackControlsRow.MultiAction) {
                    /* Following action is subclass of MultiAction
                     * - PlayPauseAction
                     * - FastForwardAction
                     * - RewindAction
                     * - ThumbsAction
                     * - RepeatAction
                     * - ShuffleAction
                     * - HighQualityAction
                     * - ClosedCaptioningAction
                     */
                    /* Change icon */
                if (action instanceof PlaybackControlsRow.ThumbsUpAction ||
                        action instanceof PlaybackControlsRow.ThumbsDownAction ||
                        action instanceof PlaybackControlsRow.RepeatAction ||
                        action instanceof PlaybackControlsRow.ShuffleAction ||
                        action instanceof PlaybackControlsRow.HighQualityAction ||
                        action instanceof PlaybackControlsRow.ClosedCaptioningAction) {
                    ((PlaybackControlsRow.MultiAction) action).nextIndex();
                }
                    /* Note: notifyChanged must be called after action.nextIndex has been called */
                notifyChanged(action);
            }
        }
    };


}
