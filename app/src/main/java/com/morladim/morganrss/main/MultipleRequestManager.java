package com.morladim.morganrss.main;

import android.util.Log;

import com.morladim.morganrss.R;
import com.morladim.morganrss.base.RssApplication;
import com.morladim.morganrss.base.database.entity.Channel;
import com.morladim.morganrss.base.network.NewsProvider;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * 多请求管理类
 * <br>创建时间：2017/8/15.
 *
 * @author morladim
 */
@SuppressWarnings("WeakerAccess")
public class MultipleRequestManager {

    private static final String TAG = "MultipleRequestManager";

    private static volatile MultipleRequestManager multipleRequestManager;

    private static final Object obj = new Object();

    //总请求数量
    private int count;
    //成功请求数量
    private int successCount;
    //请求失败数量
    private int errorCount;

    private MultipleRequestManager() {

    }

    public static MultipleRequestManager getInstance() {
        if (multipleRequestManager == null) {
            synchronized (MultipleRequestManager.class) {
                if (multipleRequestManager == null) {
                    multipleRequestManager = new MultipleRequestManager();
                }
            }
        }
        return multipleRequestManager;
    }

    /**
     * 生成默认频道
     *
     * @param generateChannelsListener 回调
     */
    public synchronized void generateChannels(final GenerateChannelsListener generateChannelsListener) {
        String[] urls = RssApplication.getContext().getResources().getStringArray(R.array.default_urls);
        generateChannelsByArray(urls, generateChannelsListener);
    }

    /**
     * 生成指定频道列表
     *
     * @param urls                     地址数组
     * @param generateChannelsListener 回调
     */
    public synchronized void generateChannelsByArray(String[] urls, final GenerateChannelsListener generateChannelsListener) {
        count = urls.length;
        successCount = 0;
        errorCount = 0;
        Log.d(TAG, "generateChannels开始，共有" + count + "项。");
        for (String url : urls) {
            NewsProvider.getChannel(url, new Consumer<Channel>() {
                @Override
                public void accept(@NonNull Channel channel) throws Exception {
                    synchronized (obj) {
                        count--;
                        successCount++;
                        Log.d(TAG, "generateChannels剩余" + count + "项。");
                        checkDone(generateChannelsListener);
                    }
                    if (generateChannelsListener != null) {
                        generateChannelsListener.oneChannelDone(channel);
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    synchronized (obj) {
                        count--;
                        Log.d(TAG, "generateChannels剩余" + count + "项，当前项错误！");
                        errorCount++;
                        checkDone(generateChannelsListener);
                    }
                    if (generateChannelsListener != null) {
                        generateChannelsListener.oneChannelError();
                    }
                }
            });
        }
    }

    private void checkDone(GenerateChannelsListener generateChannelsListener) {
        if (count == 0 && generateChannelsListener != null) {
            Log.d(TAG, "generateChannels结束，成功" + successCount + "项，失败" + errorCount + "项。");
            generateChannelsListener.allDone(successCount, errorCount);
        }
    }

    interface GenerateChannelsListener {

        void allDone(int success, int error);

        void oneChannelDone(Channel channel);

        void oneChannelError();
    }
}
