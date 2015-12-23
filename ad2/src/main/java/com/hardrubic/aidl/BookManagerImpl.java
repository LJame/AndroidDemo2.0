package com.hardrubic.aidl;

import android.os.RemoteCallbackList;
import android.os.RemoteException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * AIDL BookManager具体处理请求的实现
 */
public class BookManagerImpl extends IBookManager.Stub {

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<INewBookListener> mListenerList = new RemoteCallbackList<>();

    public BookManagerImpl() {
        Book book1 = new Book(1l, "书本1");
        Book book2 = new Book(2l, "书本2");
        mBookList.add(book1);
        mBookList.add(book2);
    }

    @Override
    public List<Book> getBookList() throws RemoteException {
        return mBookList;
    }

    @Override
    public void addBook(Book book) throws RemoteException {
        mBookList.add(book);
        int size = mListenerList.beginBroadcast();
        for (int i = 0; i < size; i++) {
            INewBookListener listener = mListenerList.getBroadcastItem(i);
            if(null != listener){
                listener.onNewBookArrived(book);
            }
        }
        mListenerList.finishBroadcast();
    }

    @Override
    public void registerListener(INewBookListener listener) throws RemoteException {
        mListenerList.register(listener);
    }

    @Override
    public void unregisterListener(INewBookListener listener) throws RemoteException {
        mListenerList.unregister(listener);
    }
}
