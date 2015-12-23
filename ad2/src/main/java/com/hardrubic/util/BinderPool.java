package com.hardrubic.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.hardrubic.aidl.IBinderPool;
import com.hardrubic.remote.service.RemoteService;

import java.util.concurrent.CountDownLatch;

public class BinderPool {
    private Context mContext;
    private IBinderPool mBinderPool;
    private static BinderPool mInstance;
    private static CountDownLatch mCountDownLatch;

    public BinderPool(Context context) {
        this.mContext = context;
        connectRemoteService();
    }

    public static BinderPool getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BinderPool.class) {
                if (mInstance == null) {
                    mInstance = new BinderPool(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 关联远程service
     */
    private synchronized void connectRemoteService() {
        mCountDownLatch = new CountDownLatch(1);
        Intent intent = new Intent(mContext, RemoteService.class);
        mContext.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinderPool = IBinderPool.Stub.asInterface(service);
                try {
                    //断开连接时的处理
                    mBinderPool.asBinder().linkToDeath(mDeathRecipient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mCountDownLatch.countDown();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        if (mBinderPool != null) {
            try {
                binder = mBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return binder;
    }

    /**
     * Binder死亡时，接收通知并重连
     */
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.w("test", "binder died");
            mBinderPool.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mBinderPool = null;
            connectRemoteService();
        }
    };

    public static class BinderPoolImpl extends IBinderPool.Stub {

        public BinderPoolImpl() {
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode){
                case 1:
                    binder = new BinderPoolImpl();
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            return binder;
        }
    }
}
