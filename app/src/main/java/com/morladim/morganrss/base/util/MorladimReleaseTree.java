package com.morladim.morganrss.base.util;

import android.util.Log;

/**
 * 可以打印所在类及行，tag改为固定
 * <br>创建时间：2017/10/26.
 *
 * @author morladim
 */
public class MorladimReleaseTree extends MorladimDebugTree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }
        super.log(priority, tag, message, t);
    }

}
