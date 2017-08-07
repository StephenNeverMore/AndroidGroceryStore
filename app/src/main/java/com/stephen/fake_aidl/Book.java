package com.stephen.fake_aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zhushuang
 * @data 2017/7/31.
 */

public class Book implements Parcelable {

    private String name;
    private String price;
    private int id;


    protected Book(Parcel in) {
        name = in.readString();
        price = in.readString();
        id = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(price);
        dest.writeInt(id);
    }
}
