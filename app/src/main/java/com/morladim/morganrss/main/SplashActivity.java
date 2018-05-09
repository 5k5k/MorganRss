package com.morladim.morganrss.main;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.stetho.Stetho;
import com.morladim.morganrss.BuildConfig;
import com.morladim.morganrss.base.RssApplication;
import com.morladim.morganrss.base.util.ImageLoader;
import com.morladim.morganrss.base.util.MorladimDebugTree;
import com.morladim.morganrss.base.util.NetworkUtils;
import com.morladim.morganrss.base.util.SharedPreferencesUtils;
import com.squareup.leakcanary.LeakCanary;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


/**
 * <br>创建时间：2018/5/9.
 *
 * @author morladim
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加載數據
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                initApplicationData(RssApplication.getContext());
                e.onNext(true);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean b) throws Exception {
                        startMainActivity();
                    }
                });
    }


    /**
     * 初始化應用工具及數據等
     *
     * @param application 應用
     */
    private void initApplicationData(Application application) {
        SharedPreferencesUtils.init(application);
        ImageLoader.init(application);
        NetworkUtils.init(application);
        //chrome://inspect
        Stetho.initializeWithDefaults(application);

        if (BuildConfig.DEBUG) {
            Timber.plant(new MorladimDebugTree());
        }

        if (LeakCanary.isInAnalyzerProcess(application)) {
            return;
        }
        LeakCanary.install(application);
    }

    /**
     * 啟動主頁面
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
