package com.hardrubic.util.network;

import com.hardrubic.util.LogUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 同步功能线程池工具
 */
public class SyncExecutorServiceUtil {

    private static ExecutorService mFixedThreadPool;

    public static ExecutorService getFixedThreadPool() {
        if (mFixedThreadPool == null || mFixedThreadPool.isShutdown()) {
            mFixedThreadPool = Executors.newFixedThreadPool(3);
        }
        return mFixedThreadPool;
    }

    public static void submitThread(Runnable runnable) {

        try {
            ExecutorService executorService = getFixedThreadPool();
            executorService.submit(runnable);
        } catch (Exception e) {
            LogUtils.w("线程池已关闭");
        }
    }

    public static void shutdown() {
        if (!mFixedThreadPool.isShutdown()) {
            mFixedThreadPool.shutdown();
        }
    }
}
