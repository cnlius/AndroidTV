package com.ls.tv.utils;

import android.util.Log;

/**
 * Created by liusong on 2018/1/9.
 */

public class LogUtils {

    public static void i(Object object, String content) {
        String tag = object.getClass().getSimpleName();
        Log.i(tag,content);
    }
}
