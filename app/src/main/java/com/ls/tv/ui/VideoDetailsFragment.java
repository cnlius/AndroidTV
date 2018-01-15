package com.ls.tv.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ls.tv.http.GlideApp;
import com.ls.tv.model.Movie;
import com.ls.tv.ui.background.MyBackgroundManager;
import com.ls.tv.ui.presenter.CardPresenter;
import com.ls.tv.ui.presenter.CustomFullWidthDetailsOverviewRowPresenter;
import com.ls.tv.ui.presenter.DetailsDescriptionPresenter;
import com.ls.tv.utils.LogUtils;
import com.ls.tv.utils.Utils;

/**
 * Created by liusong on 2018/1/15.
 */

public class VideoDetailsFragment extends DetailsFragment {
    private CustomFullWidthDetailsOverviewRowPresenter mFwdorPresenter;
    private MyBackgroundManager myBackgroundManager;
    private Movie mSelectedMovie; //需要查看详情的movie
    private DetailsRowBuilderTask mDetailsRowBuilderTask;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private void receivedData() {
        mSelectedMovie = getActivity().getIntent().getParcelableExtra(DetailsActivity.MOVIE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.i(this,"onCreate");
        super.onCreate(savedInstanceState);
        receivedData();
        //详情页主控ui
        mFwdorPresenter = new CustomFullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());

        //详情页面的背景设置
        myBackgroundManager = new MyBackgroundManager(getActivity());
        myBackgroundManager.updateBackground(mSelectedMovie.getCardImageUrl());

        mDetailsRowBuilderTask = (DetailsRowBuilderTask) new DetailsRowBuilderTask().execute(mSelectedMovie);
    }

    private class DetailsRowBuilderTask extends AsyncTask<Movie, Integer, DetailsOverviewRow> {
        @Override
        protected DetailsOverviewRow doInBackground(Movie... params) {
            final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
            try {
                //glide加载图片的into(view)需要在抓线程中
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        int width = Utils.convertDpToPixel(getActivity(), DETAIL_THUMB_WIDTH);
//                        int height = Utils.convertDpToPixel(getActivity(), DETAIL_THUMB_HEIGHT);
//
//                        GlideApp.with(getActivity())
//                                .load(mSelectedMovie.getCardImageUrl())
//                                .override(width, height)
//                                .centerCrop()
//                                .into(new SimpleTarget<Drawable>() {
//                                    @Override
//                                    public void onResourceReady(@NonNull final Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                                        row.setImageDrawable(resource);
//                                    }
//                                });
//                    }
//                });

                int width = Utils.convertDpToPixel(getActivity(), DETAIL_THUMB_WIDTH);
                int height = Utils.convertDpToPixel(getActivity(), DETAIL_THUMB_HEIGHT);
                //glide加载图片的get()方法必须在子线程中
                Drawable drawable=GlideApp.with(getActivity())
                        .asDrawable()
                        .load(mSelectedMovie.getCardImageUrl())
                        .submit(width,height)
                        .get();
                row.setImageDrawable(drawable);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return row;
        }

        @Override
        protected void onPostExecute(DetailsOverviewRow row) {
            /* 1st row: DetailsOverviewRow */
            SparseArrayObjectAdapter sparseArrayObjectAdapter = new SparseArrayObjectAdapter();
            for (int i = 0; i<10; i++){
                sparseArrayObjectAdapter.set(i, new Action(i, "label1", "label2"));
            }
            row.setActionsAdapter(sparseArrayObjectAdapter);

            /* 2nd row: ListRow */
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
            for(int i = 0; i < 10; i++){
                Movie movie = new Movie();
                if(i%3 == 0) {
                    movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02580.jpg");
                } else if (i%3 == 1) {
                    movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02630.jpg");
                } else {
                    movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02529.jpg");
                }
                movie.setTitle("title" + i);
                movie.setStudio("studio" + i);
                listRowAdapter.add(movie);
            }
            HeaderItem headerItem = new HeaderItem(0, "Related Videos");

            ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();
            mFwdorPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_SMALL);
            LogUtils.i(this, "mFwdorPresenter.getInitialState: " +mFwdorPresenter.getInitialState());

            classPresenterSelector.addClassPresenter(DetailsOverviewRow.class, mFwdorPresenter);
            classPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());

            ArrayObjectAdapter adapter = new ArrayObjectAdapter(classPresenterSelector);
            /* 1st row */
            adapter.add(row);
            /* 2nd row */
            adapter.add(new ListRow(headerItem, listRowAdapter));
            /* 3rd row */
            //adapter.add(new ListRow(headerItem, listRowAdapter));
            setAdapter(adapter);

        }
    }



}
