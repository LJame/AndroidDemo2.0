/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\program\\javaProject\\asProject\\AndroidDemo2.0\\ad2\\src\\main\\aidl\\com\\hardrubic\\aidl\\IBookManager.aidl
 */
package com.hardrubic.aidl.example;

/**
 * example：
 * SDK为aidl自动生成的Binder类
 *
 * 注意：
 * 1、客户端发起远程请求时，当前线程会被挂起直到服务端返回数据，所以耗时操作不能放在UI线程
 * 2、服务端的Binder方法运行在线程池中，所以Binder方法不管是否耗时都应该采用同步方式实现
 */
public interface IBookManagerExample extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     * 内部类Stub，是个Binder类。
     * 当客户端和服务端在同一个进程中，方法调用不会走跨进程的transact过程；
     * 当不在同一个进程时，方法调用走transact过程，这个逻辑由Proxy完成。
     */
    public static abstract class Stub extends android.os.Binder implements IBookManagerExample {
        /**
         * Binder唯一标识
         */
        private static final String DESCRIPTOR = "com.hardrubic.aidl.IBookManagerExample";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.hardrubic.aidl.IBookManagerExample interface,
         * generating a proxy if needed.
         * 将服务端的Binder对象转换成客户端所需的aidl接口类型的对象，区分进程。
         */
        public static IBookManagerExample asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IBookManagerExample))) {
                return ((IBookManagerExample) iin);
            }
            return new Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        /**
         * 运行于服务端的Binder线程池，处理系统封装好的跨进程请求
         * @param code
         * @param data
         * @param reply
         * @param flags
         * @return
         * @throws android.os.RemoteException
         */
        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_getBookList: {
                    data.enforceInterface(DESCRIPTOR);
                    java.util.List<com.hardrubic.aidl.Book> _result = this.getBookList();
                    reply.writeNoException();
                    reply.writeTypedList(_result);
                    return true;
                }
                case TRANSACTION_addBook: {
                    data.enforceInterface(DESCRIPTOR);
                    com.hardrubic.aidl.Book _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = com.hardrubic.aidl.Book.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    this.addBook(_arg0);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IBookManagerExample {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public java.util.List<com.hardrubic.aidl.Book> getBookList() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.util.List<com.hardrubic.aidl.Book> _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getBookList, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.createTypedArrayList(com.hardrubic.aidl.Book.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void addBook(com.hardrubic.aidl.Book book) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((book != null)) {
                        _data.writeInt(1);
                        book.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        /**
         * 两个整型ID标识自定义的方法，用于transact过程中标识客户端请求的目标方法
         */
        static final int TRANSACTION_getBookList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_addBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    }

    public java.util.List<com.hardrubic.aidl.Book> getBookList() throws android.os.RemoteException;

    public void addBook(com.hardrubic.aidl.Book book) throws android.os.RemoteException;
}
