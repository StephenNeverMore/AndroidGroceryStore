package com.stephen.fake_aidl;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;

/**
 * @author zhushuang
 * @data 2017/7/31.
 */

public interface IBookManager extends IInterface {

    static final String DESCRIPTOR = "com.stephen.fake_aidl.IBookManager";
    static final int TRANSACTION_getBookList = IBinder.FIRST_CALL_TRANSACTION + 0;
    static final int TRANSACTION_addBook = IBinder.FIRST_CALL_TRANSACTION + 1;

    public List<Book> getBookList() throws RemoteException;
    public List<String> addBook(Book book) throws RemoteException;
}
