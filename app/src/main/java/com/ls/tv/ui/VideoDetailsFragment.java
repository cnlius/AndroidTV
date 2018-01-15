package com.ls.tv.ui;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.ls.tv.http.GlideApp;
import com.ls.tv.model.Movie;
import com.ls.tv.ui.background.MyBackgroundManager;
import com.ls.tv.ui.presenter.CardPresenter;
import com.ls.tv.ui.presenter.CustomFullWidthDetailsOverviewRowPresenter;
import com.ls.tv.ui.presenter.DetailsDescriptionPresenter;
import com.ls.tv.utils.DataUtils;
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
        LogUtils.i(this, "onCreate");
        super.onCreate(savedInstanceState);
        receivedData();
        //详情页主控ui
        mFwdorPresenter = new CustomFullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());

        //详情页面的背景设置
        myBackgroundManager = new MyBackgroundManager(getActivity());
        myBackgroundManager.updateBackground(mSelectedMovie.getCardImageUrl());
        //装填数据任务
        mDetailsRowBuilderTask = (DetailsRowBuilderTask) new DetailsRowBuilderTask().execute(mSelectedMovie);
    }

    private class DetailsRowBuilderTask extends AsyncTask<Movie, Integer, DetailsOverviewRow> {
        @Override
        protected DetailsOverviewRow doInBackground(Movie... params) {
            //构建详情行
            final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
            try {
                int width = Utils.convertDpToPixel(getActivity(), DETAIL_THUMB_WIDTH);
                int height = Utils.convertDpToPixel(getActivity(), DETAIL_THUMB_HEIGHT);
                //glide加载图片的get()方法必须在子线程中
                Drawable drawable = GlideApp.with(getActivity())
                        .asDrawable()
                        .load(mSelectedMovie.getCardImageUrl())
                        .submit(width, height)
                        .get();
                //设置行内图片信息
                row.setImageDrawable(drawable);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return row;
        }

        @Override
        protected void onPostExecute(DetailsOverviewRow row) {
            /**
             * 构建总容器：使用ClassPresenterSelector创建adapter
             * ClassPresenterSelector定义了这个对应关系：
             * DetailsOverviewRow=FullWidthDetailsOverviewRowPresenter(被选择影片的详情)+ListRow(相关联的影片)
             * ListRowPresenter。
             */
            ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();
            //被选择影片的详情
            mFwdorPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_SMALL);
            classPresenterSelector.addClassPresenter(DetailsOverviewRow.class, mFwdorPresenter);
            //相关联的影片列表
            classPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
            ArrayObjectAdapter adapter = new ArrayObjectAdapter(classPresenterSelector);

            /* 1st row: action view(详情内容的分块信息-点击展示不同的信息) */
            SparseArrayObjectAdapter sparseArrayObjectAdapter = new SparseArrayObjectAdapter();
            for (int i = 0; i < 2; i++) {
                if(i==0) {
                    sparseArrayObjectAdapter.set(i, new Action(i, "简介"));
                }else if(i==1){
                    sparseArrayObjectAdapter.set(i, new Action(i, "演员"));
                }
            }
            row.setActionsAdapter(sparseArrayObjectAdapter);
            adapter.add(row);

            /* 2nd row: ListRow */
            //相关联的视频列表
            HeaderItem headerItem = new HeaderItem(0, "Related Videos");
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
            for (int i = 0; i < 10; i++) {
                Movie movie = new Movie();
                DataUtils.setCardImageUrl(i,movie);
                movie.setTitle("title" + i);
                movie.setStudio("studio" + i);
                listRowAdapter.add(movie);
            }
            adapter.add(new ListRow(headerItem, listRowAdapter));

            /* 3rd row */
            //adapter.add(new ListRow(headerItem, listRowAdapter));
            setAdapter(adapter);
        }
    }

}
