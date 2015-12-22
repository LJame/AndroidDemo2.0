package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import com.hardrubic.aidl.Book;
import com.hardrubic.aidl.IBookManager;
import com.hardrubic.aidl.INewBookListener;
import com.hardrubic.service.RemoteService;
import com.hardrubic.util.LogUtils;
import com.hardrubic.util.ToastUtil;
import java.util.List;
import java.util.Random;

public class AidlActivity extends TitleActivity {

    private Context mContext;
    Intent binderIntent;
    IBookManager mRemoteBookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        mContext = this;

        findViewById(R.id.tv_remote_book_list).setOnClickListener(this);
        findViewById(R.id.tv_remote_add_book).setOnClickListener(this);
        findViewById(R.id.tv_register).setOnClickListener(this);
        findViewById(R.id.tv_unregister).setOnClickListener(this);

        //开启远程服务
        Intent intent = new Intent(this, RemoteService.class);
        startService(intent);
        //绑定是异步，bindService()会立即返回，它不会返回IBinder给客户端。要接收IBinder，客户端必须创建一个ServiceConnection的实例并传给bindService()．
        //ServiceConnection包含一个回调方法，系统调用这个方法来传递要返回的IBinder．
        binderIntent = new Intent(this, RemoteService.class);
        bindService(binderIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_remote_book_list:
                try {
                    List<Book> bookList = mRemoteBookManager.getBookList();
                    ToastUtil.longShow(mContext, "书籍数量：" + bookList.size());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_remote_add_book:
                Random random = new Random();
                long num = random.nextLong();
                Book book = new Book(num, "书本" + num);
                try {
                    mRemoteBookManager.addBook(book);
                    ToastUtil.longShow(mContext, "增加一本书");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_register:
                try {
                    mRemoteBookManager.registerListener(mListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                ToastUtil.longShow(mContext, "注册成功");
                break;
            case R.id.tv_unregister:
                try {
                    mRemoteBookManager.unregisterListener(mListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                ToastUtil.longShow(mContext, "解除注册成功");
                break;
        }
    }

    private INewBookListener mListener = new INewBookListener.Stub() {

        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            LogUtils.d("哇，有新书：" + book.getName());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //暂停服务
        Intent intent = new Intent(this, RemoteService.class);
        stopService(intent);

        //断开与远程Service的连接
        unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteBookManager = IBookManager.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

}
