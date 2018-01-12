package com.ls.tv.ui.background;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.util.DisplayMetrics;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import com.ls.tv.R;
import com.ls.tv.http.GlideApp;


/**
 * 处理内容区实际的背景更改
 */
public class MyBackgroundManager {
    private final int DEFAULT_BACKGROUND_RES_ID = R.drawable.default_background;
    private static Drawable mDefaultBackground;
    private final DisplayMetrics displayMetrics;

    private Activity mActivity;
    private BackgroundManager mBackgroundManager;

    public MyBackgroundManager(Activity activity) {
        mActivity = activity;
        //默认背景图
        mDefaultBackground = activity.getDrawable(DEFAULT_BACKGROUND_RES_ID);
        //BackgroundManager单例实例
        mBackgroundManager = BackgroundManager.getInstance(activity);
        //-------------------------------------------------------
        //在更新背景之前必须附加到Window
        mBackgroundManager.attach(activity.getWindow());
        //-------------------------------------------------------
        //获取到Activity的实际屏幕信息
        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    /**
     * 改变背景
     *
     * @param path
     */
    public void updateBackground(Object path) {
        GlideApp.with(mActivity)
                .load(path)
                .override(displayMetrics.widthPixels, displayMetrics.heightPixels)
                .centerCrop()
                .placeholder(mDefaultBackground)
                .error(mDefaultBackground)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });

    }

    /**
     * 还原默认图像背景
     */
    public void clearBackground() {
        mBackgroundManager.setDrawable(mDefaultBackground);
    }

}