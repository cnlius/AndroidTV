package com.ls.tv.ui;

import android.content.Intent;
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
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ls.tv.R;
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
    private MyBackgroundManager myBackgroundManager;
    private Movie mSelectedMovie; //需要查看详情的movie
    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector classPresenterSelector;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private DetailsRowBuilderTask mDetailsRowBuilderTask;

    private void receivedData() {
        mSelectedMovie = getActivity().getIntent().getParcelableExtra(DetailsActivity.MOVIE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.i(this, "onCreate");
        super.onCreate(savedInstanceState);
        receivedData();

        //详情页面的背景设置
        myBackgroundManager = new MyBackgroundManager(getActivity());
        myBackgroundManager.updateBackground(mSelectedMovie.getCardImageUrl());

        //装填数据方式1：
        if (mSelectedMovie != null) {
            /**
             * 构建总容器：使用ClassPresenterSelector创建adapter
             * ClassPresenterSelector定义了这个对应关系：
             * DetailsOverviewRow=FullWidthDetailsOverviewRowPresenter(被选择影片的详情)+ListRow(相关联的影片)
             * ListRowPresenter。
             */
            classPresenterSelector = new ClassPresenterSelector();
            mAdapter = new ArrayObjectAdapter(classPresenterSelector);
            setupDetailsOverviewRow();
            setupDetailsOverviewRowPresenter();
            setupRelatedMovieListRow();
            setAdapter(mAdapter);
            setOnItemViewClickedListener(new ItemViewClickedListener());
        }

        //装填数据方式2：
//        mDetailsRowBuilderTask = (DetailsRowBuilderTask) new DetailsRowBuilderTask().execute(mSelectedMovie);
    }

    /**
     * 设置详情子内容DetailRow
     */
    private void setupDetailsOverviewRow() {
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
        row.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.default_background));
        int width = Utils.convertDpToPixel(getActivity(), DETAIL_THUMB_WIDTH);
        int height = Utils.convertDpToPixel(getActivity(), DETAIL_THUMB_HEIGHT);
        GlideApp.with(getActivity())
                .load(mSelectedMovie.getCardImageUrl())
                .centerCrop()
                .override(width, height)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        row.setImageDrawable(resource);
                    }
                });
        //-----------------------------------------
        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                actionAdapter.add(new Action(i, "简介"));
            } else if (i == 1) {
                actionAdapter.add(new Action(i, "演员"));
            }
        }
        row.setActionsAdapter(actionAdapter);
        //-----------------------------------------
        //另一种加载详情的子分类数据的方法：
//        SparseArrayObjectAdapter sparseArrayObjectAdapter = new SparseArrayObjectAdapter();
//        for (int i = 0; i < 2; i++) {
//            if (i == 0) {
//                sparseArrayObjectAdapter.set(i, new Action(i, "简介"));
//            } else if (i == 1) {
//                sparseArrayObjectAdapter.set(i, new Action(i, "演员"));
//            }
//        }
//        row.setActionsAdapter(sparseArrayObjectAdapter);
        //-----------------------------------------
        mAdapter.add(row);
    }

    /**
     * 设置详情页面的展示presenter
     */
    private void setupDetailsOverviewRowPresenter() {
        //详情页主控ui
        CustomFullWidthDetailsOverviewRowPresenter detailsPresenter = new CustomFullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsPresenter.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.selected_background));
        detailsPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_SMALL);

        // Hook up transition element???
//        FullWidthDetailsOverviewSharedElementHelper sharedElementHelper = new FullWidthDetailsOverviewSharedElementHelper();
//        sharedElementHelper.setSharedElementEnterTransition(getActivity(), DetailsActivity.SHARED_ELEMENT_NAME);
//        detailsPresenter.setListener(sharedElementHelper);
//        detailsPresenter.setParticipatingEntranceTransition(true);

        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        //添加详情DetailRow的Presenter
        classPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    /**
     * 设置关联视频
     */
    private void setupRelatedMovieListRow() {
        //相关联的视频列表
        HeaderItem headerItem = new HeaderItem(0, "Related Videos");
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (int i = 0; i < 10; i++) {
            Movie movie = new Movie();
            DataUtils.setCardImageUrl(i, movie);
            movie.setTitle("title" + i);
            movie.setStudio("studio" + i);
            listRowAdapter.add(movie);
        }
        mAdapter.add(new ListRow(headerItem, listRowAdapter));
        classPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Movie) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE, mSelectedMovie);
                getActivity().startActivity(intent);
            }
        }
    }

    /**
     * 装填数据方式2：
     * 异步执行任务的方式加载数据
     * 根据数据，来执行显示效果
     */
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
            CustomFullWidthDetailsOverviewRowPresenter detailsPresenter = new CustomFullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
            detailsPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_SMALL);
            classPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
            //相关联的影片列表
            classPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
            ArrayObjectAdapter adapter = new ArrayObjectAdapter(classPresenterSelector);

            //内容按顺序添加，则按顺序显示；

            /* 1st row: action view(详情内容的分块信息-点击展示不同的信息) */
            SparseArrayObjectAdapter sparseArrayObjectAdapter = new SparseArrayObjectAdapter();
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    sparseArrayObjectAdapter.set(i, new Action(i, "简介"));
                } else if (i == 1) {
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
                DataUtils.setCardImageUrl(i, movie);
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
