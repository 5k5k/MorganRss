package com.morladim.morganrss.base;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.morladim.morganrss.base.util.ImageLoader;
import com.morladim.morganrss.base.util.NetworkUtils;
import com.morladim.morganrss.base.util.SharedUtils;
import com.squareup.leakcanary.LeakCanary;

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
        ImageLoader.init(this);
        NetworkUtils.init(this);
        Stetho.initializeWithDefaults(this);
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
        context = this;
    }

}
