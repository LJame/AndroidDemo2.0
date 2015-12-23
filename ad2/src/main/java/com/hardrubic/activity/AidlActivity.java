package com.hardrubic.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;

import com.hardrubic.aidl.Book;
import com.hardrubic.aidl.IBookManager;
import com.hardrubic.aidl.INewBookListener;
import com.hardrubic.aidl.BookManagerImpl;
import com.hardrubic.util.BinderPool;
import com.hardrubic.util.LogUtils;
import com.hardrubic.util.ToastUtil;

import java.util.List;
import java.util.Random;

import ad2.hardrubic.com.androiddemo20.R;

public class AidlActivity extends TitleActivity {

    private Context mContext;
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

        //通过BinderPool获取远程的Binder对象接口
        new Thread(new Runnable() {
            @Override
            public void run() {
                BinderPool binderPool = BinderPool.getInstance(mContext);  //可以放在Application中进行初始化
                IBinder bookManagerBinder = binderPool.queryBinder(1);
                mRemoteBookManager = BookManagerImpl.asInterface(bookManagerBinder);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if(mRemoteBookManager == null){
            return;
        }
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
}
