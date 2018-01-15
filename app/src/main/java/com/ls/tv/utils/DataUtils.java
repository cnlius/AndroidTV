package com.ls.tv.utils;

import com.ls.tv.app.Global;
import com.ls.tv.model.Movie;

/**
 * Created by liusong on 2018/1/15.
 */

public class DataUtils {
    /**
     * 设置一波网络图片
     * @param i
     * @param movie
     */
    public static void setCardImageUrl(int i, Movie movie) {
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
