package com.morladim.morganrss.base.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 請求線程池
 * 使用Schedulers.io不會限制並發線程數量，可能會導致oom
 * 所以加入線程池
 * <br>创建时间：2017/7/14.
 *
 * @author morladim
 */
public class RequestPool {

    private volatile static ExecutorService instance;

    private RequestPool() {

    }

    public static ExecutorService getInstance() {
        if (instance == null) {
            synchronized (RequestPool.class) {
                if (instance == null) {
                    //設置線程數量為cpu核數量
                    int count = Runtime.getRuntime().availableProcessors();
                    instance = new ThreadPoolExecutor(count, count, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory());
                }
            }
        }
        return instance;
    }

    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "RequestPool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
