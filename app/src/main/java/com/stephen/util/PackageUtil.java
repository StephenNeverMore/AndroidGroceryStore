package com.stephen.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

/**
 * @author zhushuang
 * @data 2017/8/23.
 */

public class PackageUtil {

    public static boolean showColorOSFloatPermission(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.permission.floatwindow.FloatWindowListActivity");
        if (!CommonUtils.isIntentExist(context, intent)) {
            intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
            if(!CommonUtils.isIntentExist(context, intent)){
                return false;
            }
        }
        return CommonUtils.startActivity(context, intent);
    }

    public static void showEMUIInstalledAppDetails(Context context, String packageName, boolean isNewTask) {
        Intent intent = new Intent();
        if (PhoneModelUtils.isEMUI3_1() || PhoneModelUtils.isEMUI4x()) {
            intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");
            if (!CommonUtils.isIntentExist(context, intent)) {
                return;
            }
        } else {
            intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.SystemManagerMainActivity");
            if (!CommonUtils.isIntentExist(context, intent)) {
                intent.setClassName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
                if (!CommonUtils.isIntentExist(context, intent)) {
                    return;
                }
            }
        }
        CommonUtils.startActivity(context, intent);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void showInstalledAppDetails(Context context, String packageName, boolean isNewTask) {
        if (packageName == null)
            return;
        final int apiLevel = Build.VERSION.SDK_INT;
        Intent intent = new Intent();
        if (isNewTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        if (PhoneModelUtils.isSingleMiuiV6or7or8()) {
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", packageName);
        } else {
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
        }

        // 小米8.16后权限activity改名子了
        if (!CommonUtils.startActivity(context, intent)) {
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", packageName);
            CommonUtils.startActivity(context, intent);
        }
    }
}
