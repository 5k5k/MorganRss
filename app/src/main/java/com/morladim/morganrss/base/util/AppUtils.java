package com.morladim.morganrss.base.util;

import android.os.Looper;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * 存取应用相关信息的工具类
 * <br>创建时间：2017/8/16.
 *
 * @author morladim
 */
public class AppUtils {

    private AppUtils() {

    }

    static {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                Log.e(TAG, throwable.getMessage());
                throwable.printStackTrace();
            }
        });
    }

    /**
     * 獲取加載圖片模式
     *
     * @return 模式
     */
    public static @ImageLoadMode
    int getImageLoadMode() {
        if (imageLoadMode == null) {
            imageLoadMode = SharedPreferencesUtils.loadInt(NO_IMAGE, IMAGE_LOAD_ONLY_WIFI);
        }
        return imageLoadMode;
    }

    /**
     * 設置圖片加載模式
     *
     * @param mode 分為僅wifi下有圖、始終有圖、始終無圖
     */
    public static synchronized void setImageLoadMode(@ImageLoadMode int mode) {
        AppUtils.imageLoadMode = mode;
        SharedPreferencesUtils.saveInt(NO_IMAGE, mode);
    }

    /**
     * 根據當前的設定和網路狀況，返回是否應該加載圖片
     *
     * @return 是否加載
     */
    public static boolean loadImage() {
        //設置為始終不顯示圖片時提示
        if (AppUtils.getImageLoadMode() == AppUtils.IMAGE_NOT_LOAD) {
            return false;
        }
        //根據wifi環境判斷
        return NetworkUtils.isConnectedWifi() || AppUtils.getImageLoadMode() == AppUtils.IMAGE_LOAD_ALWAYS;
    }

    /**
     * 當前是否為主線程
     *
     * @return 是否主線程
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 圖片加載模式
     */
    private static final String NO_IMAGE = "imageLoadMode";
    static final int IMAGE_LOAD_ONLY_WIFI = 1;
    public static final int IMAGE_LOAD_ALWAYS = 1 << 1;
    public static final int IMAGE_NOT_LOAD = 1 << 2;

    @ImageLoadMode
    private static Integer imageLoadMode;

    @IntDef(value = {IMAGE_LOAD_ALWAYS, IMAGE_LOAD_ONLY_WIFI, IMAGE_NOT_LOAD})
    @Retention(RetentionPolicy.SOURCE)
    @interface ImageLoadMode {

    }

    private static final String TAG = "appUtils";

}
