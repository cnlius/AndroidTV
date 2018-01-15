package com.ls.tv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;

import com.ls.tv.R;
import com.ls.tv.app.Global;
import com.ls.tv.model.Movie;
import com.ls.tv.ui.background.MyBackgroundManager;
import com.ls.tv.ui.presenter.CardPresenter;
import com.ls.tv.ui.presenter.GridItemPresenter;
import com.ls.tv.utils.LogUtils;

/**
 * MainFragment由左侧抽屉样式的标题区和右侧内容区组成
 * BrowseFragment类由Android SDK Leanback库提供
 * Created by liusong on 2018/1/9.
 */

public class MainFragment extends BrowseFragment {
    private ArrayObjectAdapter mRowsAdapter; //主界面内容容器adapter
    /* Grid row item settings */
    private static final int GRID_ITEM_WIDTH = 300;
    private static final int GRID_ITEM_HEIGHT = 200;
    private MyBackgroundManager myBackgroundManager; //处理内容区实际的背景更改:test

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.i(this, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        //主题配置
        setupUIElements();
        //装载数据
        loadRows();
        //事件监听
        setupEventListeners();

        //背景管理器初始化
        myBackgroundManager = new MyBackgroundManager(getActivity());
    }

    /**
     * 设置MainFragment的主界面相关信息
     * 1> 由左侧抽屉样式的标题导航栏和右侧内容区组成
     * 2> 设置内容区的标题，显示在右上角；
     * 3> 导航栏显示样式：按back显示，选中时隐藏；
     * 4> 导航栏背景色设置；
     * 5> 搜索按钮图标颜色设置；
     */
    private void setupUIElements() {
        //setBadgeDrawable展示在标题栏上的图片会覆盖标题
        //setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.videos_by_google_banner));
        //设置title
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        //显示左侧导航栏，HEADERS_DISABLED 不显示 HEADERS_HIDDEN 隐藏，到边缘按左键还能显示
        setHeadersState(HEADERS_ENABLED);
        //back键会显示
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color(设置快速导航（或 headers) 背景色)
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color(设置搜索的颜色)
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));

    }

    /**
     * 设置导航栏和内容区的内容
     * 1. 整个内容界面看做是一个rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());；
     * 2. ArrayObjectAdapter对象由实现了Presenter接口的对象构造；
     * 3. 实现了Presenter接口的类，决定了ArrayObjectAdapter的UI界面样式；
     * 4. rowsAdapter有可以填充左侧抽屉导航栏和右侧内容区域；
     * 5. rowsAdapter填充对象时一组ListRow;
     * 6. ListRow由HeaderItem(导航栏标题)和ArrayObjectAdapter(内容区)构成；
     */
    private void loadRows() {
        //主界面容器adapter：导航栏HeaderItem和rowsAdapter成对构成
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        //--第1组内容--------------------------------------------
        /* GridItemPresenter */
        //1->创建一个导航栏标题；
        HeaderItem gridItemPresenterHeader = new HeaderItem(0, "GridItemPresenter");

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        //2->创建每个导航栏标题对应的内容
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add("ITEM 1");
        gridRowAdapter.add("ITEM 2");
        gridRowAdapter.add("ITEM 3");
        mRowsAdapter.add(new ListRow(gridItemPresenterHeader, gridRowAdapter));
        //--第2组内容--------------------------------------------
        /* CardPresenter */
        HeaderItem cardPresenterHeader = new HeaderItem(1, "CardPresenter");
        CardPresenter cardPresenter = new CardPresenter();
        ArrayObjectAdapter cardRowAdapter = new ArrayObjectAdapter(cardPresenter);
        for (int i = 0; i < 10; i++) {
            Movie movie = new Movie();
            movie.setTitle("title" + i);
            movie.setStudio("studio" + i);
            setCardImageUrl(i, movie);
            cardRowAdapter.add(movie);
        }
        mRowsAdapter.add(new ListRow(cardPresenterHeader, cardRowAdapter));

        /* set */
        setAdapter(mRowsAdapter);
    }

    private void setupEventListeners() {
        //必须设置此监听，内容区的item才能被选中；
        setOnItemViewClickedListener(new ItemViewClickedListener());
        //内容区item被选中时监听
        setOnItemViewSelectedListener(new ItemViewSelectedListener());

    }

    /**
     * 内容区的item被选择时调用
     * 1> 更新内容区的背景图；
     */
    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            LogUtils.i(this, "onItemSelected");
            //测试案例加载本地图片
            if (item instanceof String) { // GridItemPresenter row
                myBackgroundManager.clearBackground();
            } else if (item instanceof Movie) { // CardPresenter row
                myBackgroundManager.updateBackground(((Movie) item).getCardImageUrl());
            }
        }
    }

    /**
     * 内容区的item被点击时回调
     */
    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            LogUtils.i(this, "onItemClicked");
            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE, movie);
                getActivity().startActivity(intent);
            }
        }
    }

    /**
     * 设置一波网络图片
     * @param i
     * @param movie
     */
    private void setCardImageUrl(int i, Movie movie) {
        switch (i){
            case 1:
                movie.setCardImageUrl(Global.NET_IMAGE_1);
                break;
            case 2:
                movie.setCardImageUrl(Global.NET_IMAGE_2);
                break;
            case 3:
                movie.setCardImageUrl(Global.NET_IMAGE_3);
                break;
            case 4:
                movie.setCardImageUrl(Global.NET_IMAGE_4);
                break;
            case 5:
                movie.setCardImageUrl(Global.NET_IMAGE_5);
                break;
            case 6:
                movie.setCardImageUrl(Global.NET_IMAGE_6);
                break;
            case 7:
                movie.setCardImageUrl(Global.NET_IMAGE_7);
                break;
            case 8:
                movie.setCardImageUrl(Global.NET_IMAGE_8);
                break;
            case 9:
                movie.setCardImageUrl(Global.NET_IMAGE_9);
                break;
            default:
                movie.setCardImageUrl(Global.NET_IMAGE_10);
                break;
        }
    }
}
