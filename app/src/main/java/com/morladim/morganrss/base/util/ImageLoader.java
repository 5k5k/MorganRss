package com.morladim.morganrss.base.util;

import android.graphics.Bitmap;
import android.util.Log;

import com.morladim.morganrss.BuildConfig;
import com.morladim.morganrss.base.RssApplication;
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

    private Picasso picasso;

    private volatile static ImageLoader instance;

    public static ImageLoader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    private ImageLoader() {
        picasso = Picasso.with(RssApplication.getContext());
        picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
    }

    public void loggingEnabled(boolean enable) {
        picasso.setLoggingEnabled(enable);
    }

//    public static void noDebug() {
//        picasso.setIndicatorsEnabled(false);
//        picasso.setLoggingEnabled(false);
//    }

    public RequestCreator load(String url) {
        return picasso.load(url).config(Bitmap.Config.RGB_565);
        //.memoryPolicy(MemoryPolicy.NO_CACHE)
    }

    public void resumeTag(Object tag) {
        picasso.resumeTag(tag);
    }

    public void pauseTag(Object tag) {
        picasso.pauseTag(tag);
    }

    public void getSnapshot() {
        StatsSnapshot picassoStats = picasso.getSnapshot();
        Log.d(TAG, picassoStats.toString());
    }

}
