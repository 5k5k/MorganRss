package com.morladim.morganrss.base;

import android.app.Application;

import com.morladim.morganrss.base.util.SharedUtils;

/**
 * <br>创建时间：2017/7/13.
 *
 * @author morladim
 */
public class RssApplication extends Application {

    private static RssApplication context;

    public static RssApplication getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedUtils.init(this);
        context = this;
    }

}
