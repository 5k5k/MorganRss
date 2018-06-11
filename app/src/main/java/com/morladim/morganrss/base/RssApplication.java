package com.morladim.morganrss.base;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.morladim.morganrss.BuildConfig;
import com.morladim.morganrss.base.util.MorladimDebugTree;
import com.morladim.morganrss.base.util.MorladimReleaseTree;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

/**
 * <br>创建时间：2017/7/13.
 *
 * @author morladim
 */
public class RssApplication extends Application {

    private static RssApplication context;

    public static Application getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        context = this;
        LeakCanary.install(this);
        Stetho.initializeWithDefaults(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new MorladimDebugTree());
        } else {
            Timber.plant(new MorladimReleaseTree());
        }
    }
}
