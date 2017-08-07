package com.stephen.util;

import android.util.Log;

/**
 * @author zhushuang
 * @data 2017/7/19.
 */

public class LogUtils {
    private final static String TAG = "SEE_THIS";
    private static boolean isLoggable = true;

    public static void e(String message) {
        if (isLoggable) {
            Log.e(TAG, message);
        }
    }
}
