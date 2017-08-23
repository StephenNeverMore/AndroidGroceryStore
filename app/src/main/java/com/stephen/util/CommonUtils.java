package com.stephen.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;

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
}
