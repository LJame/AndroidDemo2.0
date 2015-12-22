package com.hardrubic.aidl;
import com.hardrubic.aidl.Book;

interface INewBookListener{
    void onNewBookArrived(in Book book);
}