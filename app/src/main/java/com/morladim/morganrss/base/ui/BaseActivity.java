package com.morladim.morganrss.base.ui;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

/**
 * <br>创建时间：2017/10/17.
 *
 * @author morladim
 */
@SuppressWarnings("unused")
public class BaseActivity extends AppCompatActivity {

    /**
     * 上次触摸时间
     */
    private long lastClickTime;

    private boolean interceptDoubleClick = true;

    /**
     * 是否拦截双击事件
     *
     * @param interceptDoubleClick 拦截
     */
    protected void setInterceptDoubleClick(boolean interceptDoubleClick) {
        this.interceptDoubleClick = interceptDoubleClick;
    }

    /**
     * 是否处理触摸事件
     *
     * @param ev 事件
     * @return 处理则不下发
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastClick() && interceptDoubleClick) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * activity中连续点击时间间隔，单位毫秒
     */
    public static final int DISPATCH_DURATION = 200;

    /**
     * 是否连续点击
     *
     * @return 连点与否
     */
    private boolean isFastClick() {
        long time = SystemClock.elapsedRealtime();
        long timeD = time - lastClickTime;
        if (timeD > 0 && timeD < DISPATCH_DURATION) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }
}
