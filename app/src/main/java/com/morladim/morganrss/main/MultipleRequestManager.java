package com.morladim.morganrss.main;

import android.util.Log;

import com.morladim.morganrss.base.database.entity.Channel;
import com.morladim.morganrss.base.network.NewsProvider;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * 多请求管理类
 * <br>创建时间：2017/8/15.
 *
 * @author morladim
 */
@SuppressWarnings("WeakerAccess")
public class MultipleRequestManager {

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

    public void loadChannels(List<Channel> channelList, final GenerateChannelsListener generateChannelsListener) {
        if (working) {
            return;
        }
        working = true;
        count = channelList.size();
        successCount = 0;
        errorCount = 0;
        Log.d(TAG, "loadChannels开始，共有" + count + "项。");

        NewsProvider.getChannels(channelList, new Consumer<Channel>() {
            @Override
            public void accept(@NonNull Channel channel) {
                synchronized (OBJ) {
                    count--;
                    successCount++;
                    Timber.d(TAG, "generateChannels剩余" + count + "项。");
                    checkDone(generateChannelsListener);
                }
                if (generateChannelsListener != null) {
                    generateChannelsListener.oneChannelDone(channel);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                synchronized (OBJ) {
                    throwable.printStackTrace();
                    count--;
                    Timber.d(TAG, "generateChannels剩余" + count + "项，当前项错误！");
                    errorCount++;
                    checkDone(generateChannelsListener);
                }
                if (generateChannelsListener != null) {
                    generateChannelsListener.oneChannelError();
                }
            }
        });
    }

    private void checkDone(GenerateChannelsListener generateChannelsListener) {
        if (count == 0 && generateChannelsListener != null) {
            Timber.d(TAG, "generateChannels结束，成功" + successCount + "项，失败" + errorCount + "项。");
            generateChannelsListener.allDone(successCount, errorCount);
            working = false;
        }
    }

    private static final String TAG = "MultipleRequestManager";

    private static volatile MultipleRequestManager multipleRequestManager;

    /**
     * 鎖
     */
    private static final Object OBJ = new Object();

    /**
     * 总请求数量
     */
    private int count;
    /**
     * 成功请求数量D
     */
    private int successCount;
    /**
     * 请求失败数量
     */
    private int errorCount;

    private volatile boolean working = false;

    interface GenerateChannelsListener {

        /**
         * 全部完成回調
         *
         * @param success 成功獲取數量
         * @param error   失敗數量
         */
        void allDone(int success, int error);

        /**
         * 一個頻道完成回調
         *
         * @param channel 頻道
         */
        void oneChannelDone(Channel channel);

        /**
         * 一個頻道失敗回調
         */
        void oneChannelError();
    }
}
