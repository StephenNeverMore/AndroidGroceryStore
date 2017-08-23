package com.stephen.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author zhushuang
 * @data 2017/8/23.
 */

public class CommonUtils {

    public static void startAtyAndClearTop(Context context, Class clazz) {
        if (context == null || clazz == null) {
            return;
        }
        Intent intent = new Intent(context, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    public static boolean isAccessibilitySettingsOn(Context mContext, String name) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + name;

        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            LogUtils.e("Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);

                StringBuilder sb = new StringBuilder();
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        LogUtils.e("accessibilityEnabled = " + accessibilityEnabled);
                        return true;
                    } else {
                        sb.append(":");
                        int index = accessibilityService.indexOf("/");
                        if (index != -1) {
                            sb.append(accessibilityService.substring(0, index));
                        } else {
                            sb.append(accessibilityService);
                        }
                        sb.append(":");
                    }
                }

                String logVal = sb.toString();
                if (!TextUtils.isEmpty(logVal)) {
                    LogUtils.e("accessibility: " + logVal);
                } else {
                    LogUtils.e("accessibility: NONE");
                }
            }
        } else {
            LogUtils.e(" ---- ACCESSIBILIY IS DISABLED ---");
        }

        return false;
    }


    /**
     * 判断悬浮窗权限是否开启
     */
    public static boolean isAlertWindowPermissionEnabled() {
        if (PhoneModelUtils.isMiui() || PhoneModelUtils.isMiuiV5()) {
            return !PhoneModelUtils.isWindowAlterCloseByMIUIV5();
        }
        if (PhoneModelUtils.isEMUI2() || PhoneModelUtils.isEMUI3()) {
            return !PhoneModelUtils.isWindowAlterCloseByHUAWEIForOneKeyPermission();
        }
        if (PhoneModelUtils.isOppoColorOSv3()) {
            Boolean b = PhoneModelUtils.isFloatCloseByOppoColor();
            return !b;
        }
        return true;
    }

    public static boolean isIntentExist(Context context, Intent intent) {
        if (intent == null) {
            return false;
        }
        PackageManager localPackageManager = context.getPackageManager();
        if (localPackageManager.resolveActivity(intent, 0) == null) {
            return false;
        }
        return true;
    }

    // 必须都使用此方法打开外部activity,避免外部activity不存在而造成崩溃，
    public static boolean startActivity(Context context, Intent intent) {
        if (context == null || intent == null) {
            return false;
        }
        boolean bResult = true;
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            bResult = false;
        }
        return bResult;
    }

    public static void setStatusBarColor(Activity activity, View statusBar, int color) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        final int statusBarHeight = getStatusBarHeight(activity);
        final Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {// 4.4 - 5.0
            if (statusBar != null) {
                statusBar.setBackgroundColor(color);
                final ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
                layoutParams.height = statusBarHeight;
                statusBar.setLayoutParams(layoutParams);
                statusBar.setVisibility(View.VISIBLE);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0 and above
            if (statusBar != null) {
                final ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
                layoutParams.height = statusBarHeight;
                statusBar.setLayoutParams(layoutParams);
                statusBar.setVisibility(View.VISIBLE);
            }
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static int getStatusBarHeight(final Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
