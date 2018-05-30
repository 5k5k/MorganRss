package com.morladim.morganrss.base.web;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * 在web進程運行的service，在閃屏頁啟動為了將web進程啟動。
 * 避免在程序運行中第一次啟動 {@link WebActivity}時要先啟動進程的延遲。
 *
 * @author morladim
 * @date 2018/5/30
 */
public class WebService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }
}
