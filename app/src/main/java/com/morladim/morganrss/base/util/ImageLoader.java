package com.morladim.morganrss.base.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.morladim.morganrss.BuildConfig;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.StatsSnapshot;

/**
 * 图片加载封装类
 * <br>创建时间：2017/8/11.
 *
 * @author morladim
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ImageLoader {
    private static final String TAG = "ImageLoader";

    @SuppressLint("StaticFieldLeak")
    private static Picasso picasso;

    private ImageLoader() {

    }

    public static void init(Application context) {
        picasso = Picasso.with(context);
        picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
    }

    public static void loggingEnabled(boolean enable) {
        picasso.setLoggingEnabled(enable);
    }

//    public static void noDebug() {
//        picasso.setIndicatorsEnabled(false);
//        picasso.setLoggingEnabled(false);
//    }

    public static RequestCreator load(String url) {
        return picasso.load(url).config(Bitmap.Config.RGB_565);
        //.memoryPolicy(MemoryPolicy.NO_CACHE)
    }

    public static void resumeTag(Object tag) {
        picasso.resumeTag(tag);
    }

    public static void pauseTag(Object tag) {
        picasso.pauseTag(tag);
    }

    public static void getSnapshot() {
        StatsSnapshot picassoStats = picasso.getSnapshot();
        Log.d(TAG, picassoStats.toString());
    }

}
