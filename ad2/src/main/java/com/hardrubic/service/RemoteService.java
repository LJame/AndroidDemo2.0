package com.hardrubic.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hardrubic.aidl.Book;
import com.hardrubic.aidl.IBookManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 拥有被远程调用的服务
 */
public class RemoteService extends Service {

    private final static String TAG = "RemoteService";
    private List<Book> mBookList = new ArrayList<>();

    /**
     * 被绑定时，返回Binder接口对象
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("test", "RemoteService onBind");
        return stub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("test", "RemoteService onCreate");
        initData();
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

    /**
     * 处理远程请求
     */
    IBookManager.Stub stub = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }
    };

    private void initData() {
        Book book1 = new Book(1l, "书本1");
        Book book2 = new Book(2l, "书本2");
        mBookList.add(book1);
        mBookList.add(book2);
    }
}
