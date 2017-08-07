package com.stephen.base;

import android.app.Application;
import android.content.Context;

/**
 * @author zhushuang
 * @data 2017/8/1.
 */

public class MainApplication extends Application {

    private static Context sContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }
}
