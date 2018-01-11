/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ls.tv.presenter;

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;

import com.ls.tv.R;
import com.ls.tv.http.GlideApp;
import com.ls.tv.model.Movie;
import com.ls.tv.utils.LogUtils;

/**
 * 使用ImageCardView填充内容区域每个item的内容
 * <p>
 * 1> presenter用来构建ArrayObjectAdapter
 * 2> 导航栏的标题组对应的内容区每个item的UI内容
 * <p>
 * ImageCardView
 * 1> 可以设置标题、图标、描述内容；
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class CardPresenter extends Presenter {

    private static final int CARD_WIDTH = 313;
    private static final int CARD_HEIGHT = 176;
    private Context context;

    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LogUtils.i(this, "onCreateViewHolder");
        context = parent.getContext();
        ImageCardView cardView = new ImageCardView(context);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setBackgroundColor(context.getResources().getColor(R.color.fastlane_background));
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        LogUtils.i(this, "onBindViewHolder");
        ViewHolder holder = (ViewHolder) viewHolder;
        Movie movie = (Movie) item;
        holder.mCardView.setTitleText(movie.getTitle());
        holder.mCardView.setContentText(movie.getStudio());
        holder.mCardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
        //设置cardImageView的图片
//        ((ViewHolder) viewHolder).mCardView.setMainImage(((ViewHolder) viewHolder).getDefaultCardImage());
        GlideApp.with(context)
                .load(movie.getCardImageUrl())
                .error(R.drawable.movie)
                .into(holder.mCardView.getMainImageView());
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        LogUtils.i(this, "onUnbindViewHolder");
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    static class ViewHolder extends Presenter.ViewHolder {
        private ImageCardView mCardView;

        public ViewHolder(View view) {
            super(view);
            mCardView = (ImageCardView) view;
        }
    }
}
