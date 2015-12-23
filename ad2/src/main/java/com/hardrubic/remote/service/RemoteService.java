package com.hardrubic.remote.service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hardrubic.util.BinderPool;

/**
 * 拥有被远程调用的服务
 */
public class RemoteService extends Service {

    private final static String TAG = "RemoteService";
    private Binder mBinderPool = new BinderPool.BinderPoolImpl();

    /**
     * 被绑定时，返回Binder接口对象
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("test", "RemoteService onBind");
        return mBinderPool;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("test", "RemoteService onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("test", "RemoteService onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("test", "RemoteService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }
}
