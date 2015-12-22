package com.hardrubic.aidl;
import com.hardrubic.aidl.Book;
import com.hardrubic.aidl.INewBookListener;

/**
 * AIDL接口
 */
interface IBookManager {
   List<Book> getBookList();
   void addBook(in Book book);
   void registerListener(INewBookListener listener);
   void unregisterListener(INewBookListener listener);
}
