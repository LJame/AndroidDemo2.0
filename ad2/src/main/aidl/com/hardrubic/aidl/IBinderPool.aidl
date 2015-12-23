package com.hardrubic.aidl;

/**
* Binder连接池
*/
interface IBinderPool{
    IBinder queryBinder(int binderCode);
}