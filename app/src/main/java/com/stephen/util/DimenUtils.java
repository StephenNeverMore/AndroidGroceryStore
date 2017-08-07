package com.stephen.util;

import android.graphics.PointF;
import android.util.TypedValue;

import com.stephen.base.MainApplication;

/**
 * @author zhushuang
 * @data 2017/7/20.
 */

public class DimenUtils {

    public static int dp2px(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, MainApplication.getContext().getResources().getDisplayMetrics());
    }

    public static int getDistance(PointF startPointF, PointF endPointF) {
        return 0;
    }
}
