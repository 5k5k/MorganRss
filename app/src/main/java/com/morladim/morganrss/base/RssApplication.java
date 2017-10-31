package com.morladim.morganrss.base;

import com.morladim.tools.BaseApplication;
import com.squareup.leakcanary.LeakCanary;

/**
 * <br>创建时间：2017/7/13.
 *
 * @author morladim
 */
public class RssApplication extends BaseApplication {

    @Override
    public void onCreate() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        super.onCreate();
    }
}
