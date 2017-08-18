package com.morladim.morganrss.base.util;

import android.util.Log;

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

    private static final String NO_IMAGE = "noImage";

    private static Boolean noImage;

    private AppUtils() {

    }

    private static final String TAG = "appUtils";

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
     * 是否无图模式
     *
     * @return 无图模式
     */
    public static boolean isNoImageMode() {
        if (noImage == null) {
            noImage = SharedUtils.loadBoolean(NO_IMAGE);
        }
        return noImage;
    }

    /**
     * 设置无图模式
     *
     * @param noImage 无图模式
     */
    public static synchronized void setNoImageMode(boolean noImage) {
        AppUtils.noImage = noImage;
        SharedUtils.saveBoolean(NO_IMAGE, noImage);
    }

}
