package com.morladim.morganrss.base;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.morladim.morganrss.BuildConfig;
import com.morladim.morganrss.base.util.ImageLoader;
import com.morladim.morganrss.base.util.NetworkUtils;
import com.morladim.morganrss.base.util.SharedUtils;
import com.morladim.tools.MorladimDebugTree;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;


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

        if (BuildConfig.DEBUG) {
          Timber.plant(new MorladimDebugTree());
        } else {
        }
    }

}
