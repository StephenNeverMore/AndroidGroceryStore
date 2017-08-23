package com.stephen.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.view.View;

import com.stephen.base.MainApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class PhoneModelUtils {

    private static final int FLAG_SHOW_FLOATING_WINDOW = 1 << 27;
    private static final int FLAG_SHOW_FLOATING_WINDOW_GT_V19 = 1 << 25;
    private static final int OP_SYSTEM_ALERT_WINDOW = 24;
    private static final String AddWindowManagerVer = "3.3.29";
    private final static String HW_SET_PACKAGE_NAME = "com.huawei.systemmanager";


    public static boolean isHasPackage(Context c, String packageName) {
        if (null == c || null == packageName)
            return false;

        boolean bHas = true;
        try {
            c.getPackageManager().getPackageInfo(packageName, PackageManager.GET_GIDS);
        } catch (/* NameNotFoundException */Exception e) {
            // 抛出找不到的异常，说明该程序已经被卸载
            bHas = false;
        }
        return bHas;
    }

    public static boolean hasMiuiRom() {
        return isHasPackage(MainApplication.getContext(), "com.miui.cloudservice");
    }

    public static boolean isMiui() {

        if (Build.DISPLAY != null && Build.DISPLAY.toUpperCase().contains("MIUI")) {
            return true;
        }

        if (Build.MODEL != null && Build.MODEL.contains("MI-ONE")) {
            return hasMiuiRom();
        }

        if (Build.DEVICE != null && Build.DEVICE.contains("mione")) {
            return hasMiuiRom();
        }

        try {
            if (Build.PRODUCT != null && Build.PRODUCT.contains("mione")) {
                return hasMiuiRom();
            }
        } catch (Throwable e) {
        }

        return false;
    }

    public static boolean isAsus() {

        if (Build.MODEL != null && Build.MODEL.contains("ASUS_")) {
            return true;
        }

        if (Build.DEVICE != null && Build.DEVICE.contains("ASUS_")) {
            return true;
        }

        return false;
    }

  /*  public static boolean isHuaWei() {

        if (Build.MODEL != null && Build.MODEL.contains("HUAWEI")) {
            return true;
        }

        if (Build.DEVICE != null && Build.DEVICE.contains("hw")) {
            return true;
        }

        if (Build.MANUFACTURER != null && Build.MANUFACTURER.equalsIgnoreCase("huawei")) {
            return true;
        }

        return false;
    }*/

    public static boolean invokeCheckOpMethod(Context context, int uid, String pkgName) {
        boolean isClosedByMiuiV6 = false;
        try {
            Class clz = Class.forName("android.content.Context");
            Field fd = clz.getDeclaredField("APP_OPS_SERVICE");
            fd.setAccessible(true);
            Object obj = fd.get(clz);
            String ops = "";
            if (obj instanceof String) {
                ops = (String) obj;
                Method method1 = clz.getMethod("getSystemService", String.class);
                Object appOpsManager = method1.invoke(context, ops);
                Class<?> cls = Class.forName("android.app.AppOpsManager");
                fd = cls.getDeclaredField("MODE_ALLOWED");
                fd.setAccessible(true);
                int allowMode = fd.getInt(cls);

                Method method = cls.getMethod("checkOp", int.class, int.class, String.class);
                int opMode = (Integer) method.invoke(appOpsManager, OP_SYSTEM_ALERT_WINDOW, uid, pkgName);
                isClosedByMiuiV6 = opMode != allowMode;
            }
        } catch (Exception e) {
        }
        return isClosedByMiuiV6;
    }

//    private static boolean checkOp(Context context, int uid, String pkgName) {
//        boolean isClosedByMiuiV6 = false;
//        try {
//            AppOpsManager aom = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//            Method method = AppOpsManager.class.getMethod("checkOp", int.class, int.class, String.class);
//            int opMode =  (Integer) method.invoke(aom, OP_SYSTEM_ALERT_WINDOW, uid, pkgName);
//            isClosedByMiuiV6 = opMode != AppOpsManager.MODE_ALLOWED;
//        } catch (Exception e) {
//            android.util.Log.d("ny", "checkOp() e = " + e.getMessage());
//        }
//        android.util.Log.d("ny", "checkOp() isClosedByMiuiV6 = " + isClosedByMiuiV6);
//        return isClosedByMiuiV6;
//    }

    /*
     * 判断是不是弹出 WINDOW 被干掉了
     *
     * @param info
     * @return
     */
    public static boolean isWindowAlterCloseByMIUIV5() {
        if (PhoneModelUtils.isMiui() || PhoneModelUtils.isMiuiV5()) {
            Context context = MainApplication.getContext();
            String pkgName = context.getPackageName();
            ApplicationInfo info;
            try {
                info = context.getPackageManager().getPackageInfo(pkgName, 0).applicationInfo;
                boolean isMiuiV6 = isSingleMiuiV6or7or8();
                if (isMiuiV6) {
                    return invokeCheckOpMethod(context, info.uid, pkgName);
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= 19) {
                        return (FLAG_SHOW_FLOATING_WINDOW_GT_V19 & info.flags) == 0;
                    } else {
                        return (FLAG_SHOW_FLOATING_WINDOW & info.flags) == 0;
                    }
                }
            } catch (NameNotFoundException e) {
            }
        }
        return false;
    }

    public static boolean isWindowAlterCloseByHUAWEI() {
        if (isEMUI2() || isEMUI3()) {

            Context context = MainApplication.getContext();
            String pkgName = context.getPackageName();
            ApplicationInfo info;
            try {
                info = context.getPackageManager().getPackageInfo(pkgName, 0).applicationInfo;
                return invokeCheckOpMethod(context, info.uid, pkgName);
            } catch (NameNotFoundException e) {
            }
        }
        return false;
    }

    /**
     * 一键修复的activity出现时，SpecialSystemISettingFragment还未加载，HAS_SHOW_EMUI_SPECIAL_PAGE为默认的false
     * 故去掉相关的判断。
     *
     * @return
     */
    public static boolean isWindowAlterCloseByHUAWEIForOneKeyPermission() {
        if (isEMUI2() || isEMUI3()) {
            Context context = MainApplication.getContext();
            String pkgName = context.getPackageName();
            ApplicationInfo info;
            try {
                info = context.getPackageManager().getPackageInfo(pkgName, 0).applicationInfo;
                return invokeCheckOpMethod(context, info.uid, pkgName);
            } catch (NameNotFoundException e) {
            }
        }
        return false;
    }

    public static boolean isFloatCloseByOppoColor() {
        if (isOppoColorOSv3()) {
            Context context = MainApplication.getContext();
            String pkgName = context.getPackageName();
            ApplicationInfo appInfo;
            try {
                appInfo = context.getPackageManager().getPackageInfo(pkgName, 0).applicationInfo;
                return invokeCheckOpMethod(context, appInfo.uid, pkgName);
            } catch (NameNotFoundException e) {
            }
        }
        return false;
    }

    private static boolean isIntentExist() {
        Intent intent = new Intent();
        Context context = MainApplication.getContext();
        intent.setClassName(HW_SET_PACKAGE_NAME, "com.huawei.systemmanager.SystemManagerMainActivity");
        if (!CommonUtils.isIntentExist(context, intent)) {
            intent.setClassName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
            if (!CommonUtils.isIntentExist(context, intent)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEMUI2() {

        String ver = SystemProperties.get("ro.build.version.emui", "unkonw");
        if (ver.equalsIgnoreCase("EmotionUI_2.3")) {
            return true;
        }
        ver = Build.DISPLAY;
        if (ver.contains("EMUI2.3") && "Huawei".equalsIgnoreCase(Build.BRAND)) {
            return true;
        }
        return false;
    }

    // 2016.02.03 判断华为改为版本3.x以上　该方法调用地方太多就不改名子了
    public static boolean isEMUI3() {
        String ver = SystemProperties.get("ro.build.version.emui", "unkonw");
        return ver.startsWith("EmotionUI_");
    }

    public static boolean isEMUI4x() {
        String ver = SystemProperties.get("ro.build.version.emui", "unkonw");
        return ver.startsWith("EmotionUI_4");
    }

    public static boolean isEMUI3_1() {
        String ver = SystemProperties.get("ro.build.version.emui", "unkonw");
        return ver.equalsIgnoreCase("EmotionUI_3.1");
    }

    public static boolean isOppoColorOSv3() {
        String ver = SystemProperties.get("ro.build.version.opporom", "unknown");
        return ver.equalsIgnoreCase("V3.0.0");
    }

    // v5 v6 v7
    public static boolean isMiuiV5() {
        String ver = SystemProperties.get("ro.miui.ui.version.name", "unkonw");
        if (ver.equalsIgnoreCase("V5") || ver.equalsIgnoreCase("V6") || ver.equalsIgnoreCase("V7") || ver.equalsIgnoreCase("V8")) {
            return isAddWindowManagerOnMIUIV5();
        }
        return false;
    }

    public static boolean isXiaoMi() {
        return "xiaomi".equalsIgnoreCase(Build.BRAND);
    }

    public static boolean isMINote() {
        return isXiaoMi() && "MI NOTE Pro".equalsIgnoreCase(Build.MODEL);
    }

    public static boolean isRedMiNote3() {
        return "Redmi Note 3".equalsIgnoreCase(Build.MODEL);
    }

    public static boolean isSingleMiuiV6or7or8() {
        String ver = SystemProperties.get("ro.miui.ui.version.name", "unkonw");
        if (ver.equalsIgnoreCase("V6") || ver.equalsIgnoreCase("V7") || ver.equalsIgnoreCase("V8")) {
            return isAddWindowManagerOnMIUIV5();
        }
        return false;
    }

    public static boolean isSingleMiuiV5() {
        String ver = SystemProperties.get("ro.miui.ui.version.name", "unkonw");
        if (ver.equalsIgnoreCase("V5")) {
            return isAddWindowManagerOnMIUIV5();
        }
        return false;
    }

    public static boolean isMiuiV8() {
        String ver = getSystemVersion();
        if (ver.equalsIgnoreCase("V6")
                || ver.equalsIgnoreCase("V7")
                || ver.equalsIgnoreCase("V8")) {
            return true;
        }
        return false;
    }

    private static String getSystemVersion() {
        String ver = SystemProperties.get("ro.miui.ui.version.name", "UNKNOWN");
        ver = ver == null ? "" : ver;
        return ver;
    }

    /*
     * 是否添加了浮动框管理
     *
     * @return
      */
    private static boolean isAddWindowManagerOnMIUIV5() {
        boolean hasAddWindowManager = true;
        try {
            String ver = SystemProperties.get("ro.build.version.incremental", "unkonw");
            if (TextUtils.isEmpty(ver)) {
                return false;
            }
            // 检测是否为稳定版MUIV5的VERSION   使用字母前缀标示
            // 修复Pattern timeout崩溃, 不使用Pattern，换一种方式判断是否有字母
            // boolean needCheckPrefix = Pattern.compile("(?i)[a-z]").matcher(ver).find();
            boolean needCheckPrefix = hasLetterInStr(ver);
            if (needCheckPrefix) {
                //对M2手机判断
                if (ver.startsWith("JLB")) {
                    int length = ver.length();
                    ver = ver.substring(3, length);
                    float nver = Float.valueOf(ver);
                    if (nver < 22.0f) {
                        hasAddWindowManager = false;
                    }
                }
            } else {
                //对(开发版)数字版本号判断
                String[] nVer = ver.split("\\.");
                String[] aVer = AddWindowManagerVer.split("\\.");
                int length = nVer.length > aVer.length ? aVer.length : nVer.length;
                for (int i = 0; i < length; i++) {
                    if (TextUtils.isEmpty(nVer[i]) || !TextUtils.isDigitsOnly(nVer[i])) {
                        continue;
                    }
                    if (TextUtils.isEmpty(aVer[i]) || !TextUtils.isDigitsOnly(aVer[i])) {
                        continue;
                    }
                    int n = Integer.parseInt(nVer[i]);
                    int a = Integer.parseInt(aVer[i]);
                    if (n == a) {
                        continue;
                    } else {
                        hasAddWindowManager = n > a;
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        return hasAddWindowManager;
    }

    /**
     * 判断字符串里是否含有字母
     *
     * @param str
     * @return true有字母, false没字母
     */
    private static boolean hasLetterInStr(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (Character.isLetter(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

//    /**
//     * 打开信任权限设置
//     * {act=miui.intent.action.APP_PERM_EDITOR
//     * cmp=com.miui.securitycenter/com.miui.permcenter.permissions.AppPermissionsEditorActivity (has extras)}
//     * <p>
//     * miui.intent.action.APP_PERM_EDITOR cmp=com.android.settings/com.miui.securitycenter.permission.AppPermissionsEditor
//     */
//    public static boolean openAutoRunSetting(final Context context, View view, boolean isNewTask) {
//        if (view == null) return false;
//        final int result;
//        if (PhoneModelUtils.isSingleMiuiV6or7or8() || PhoneModelUtils.isSingleMiuiV5()) {
//            result = PackageUtil.showInstalledAppPermission(context, context.getPackageName(), isNewTask);
//        } else {
//            if (isEMUI4x()) {
//                return handleHWAutoRun(context, view);
//            }
//            result = PackageUtil.showInstalledAppPermissionHW(context, context.getPackageName(), isNewTask);
//        }
//
//        if (result == PackageUtil.RESULT_FAIL) return false;
//
//        view.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int type = MIUIGuideActivity.TYPE_TRUST_ERROR;
//                if (result == PackageUtil.RESULT_EXTRA) {
//                    type = MIUIGuideActivity.TYPE_TRUST_ERROR;
//                } else {
//                    if (PhoneModelUtils.isSingleMiuiV6or7or8() || PhoneModelUtils.isSingleMiuiV5()) {
//                        boolean isV6 = PhoneModelUtils.isSingleMiuiV6or7or8();
//                        if (isV6) {
//                            if (result == PackageUtil.RESULT_BETTER) {
//                                type = MIUIGuideActivity.TYPE_TRUST_MIUI_V6_BETTER;
//                            } else {
//                                type = MIUIGuideActivity.TYPE_TRUST_MIUI_V6;
//                            }
//                        } else {
//                            type = MIUIGuideActivity.TYPE_TRUST_MIUI_V5;
//                        }
//                    }
//
//                    if (PhoneModelUtils.isEMUI3()) {
//                        // 暂无EMUI2的机器无法测试  所以只对EMUI3来处理
//                        if (result == PackageUtil.RESULT_BETTER)
//                            type = MIUIGuideActivity.TYPE_WINDOW_EMUI_E3_BETTER;
//                        else {
//                            type = MIUIGuideActivity.TYPE_WINDOW_EMUI_E3;
//                        }
//                    }
//
//                }
//
//                MIUIGuideActivity.toStart(context, type);
//            }
//        }, 500);
//        return true;
//    }
//
//    private static boolean handleHWAutoRun(final Context context, View view) {
//        if (openHUAWEIAutoRunSetting4x(context)) {
//            view.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    MIUIGuideActivity.toStart(context, MIUIGuideActivity.TYPE_WINDOW_EMUI_E3_BETTER);
//                }
//            }, 500);
//        }
//        return true;
//    }
//
//    private final static String HW_SET_PACKAGE_NAME = "com.huawei.systemmanager";
//
//    public static boolean openHUAWEIAutoRunSetting4x(Context context) {
//        Intent intent = new Intent();
//        intent.setClassName(HW_SET_PACKAGE_NAME, "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
//        if (!KCommons.isIntentExist(context, intent)) {
//            return false;
//        }
//
//        KCommons.startActivity(context, intent);
//        return true;
//    }


    /**
     * 打开悬浮窗设置
     */
    public static void openWindowSettingByOneKey(final Context context, boolean isNewTask) {
        if (PhoneModelUtils.isOppoColorOSv3()) {
            PackageUtil.showColorOSFloatPermission(context);
            return;
        } else if (PhoneModelUtils.isEMUI2() || PhoneModelUtils.isEMUI3()) {
            PackageUtil.showEMUIInstalledAppDetails(context, context.getPackageName(), isNewTask);
        } else if (PhoneModelUtils.isMiui() || PhoneModelUtils.isMiuiV5()) {
            PackageUtil.showInstalledAppDetails(context, context.getPackageName(), isNewTask);
        } else {
            PackageUtil.showInstalledAppDetails(context, context.getPackageName(), isNewTask);
            return;
        }
    }

    public static void openEMUIWindowSetting(final Context context, View view, boolean isNewTask) {
        if (view == null) return;
        PackageUtil.showEMUIInstalledAppDetails(context, context.getPackageName(), isNewTask);
//        view.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int nType = MIUIGuideActivity.TYPE_WINDOW_EMUI_E2;
//                if (isEMUI4x()) {
//                    nType = MIUIGuideActivity.TYPE_WINDOW_EMUI_E3_BETTER;
//                } else if (isEMUI2()) {
//                    nType = MIUIGuideActivity.TYPE_WINDOW_EMUI_E2;
//                }
//                MIUIGuideActivity.toStart(context, nType);
//            }
//        }, 500);
    }

    public static boolean isWindowAlterCloseByQiku() {
        if ("QiKU".equals(Build.BRAND)) {
            Context context = MainApplication.getContext();
            String pkgName = context.getPackageName();
            ApplicationInfo info;
            try {
                info = context.getPackageManager().getPackageInfo(pkgName, 0).applicationInfo;
                return invokeCheckOpMethod(context, info.uid, pkgName);
            } catch (NameNotFoundException e) {
            }
        }
        return false;
    }

    public static boolean isLG() {
        return "lge".equalsIgnoreCase(Build.BRAND);
    }

    public static boolean isGoogle() {
        return "google".equalsIgnoreCase(Build.BRAND);
    }

    public static boolean isHuawei() {
        return PhoneModelUtils.isEMUI2() || PhoneModelUtils.isEMUI3();
    }

//    public static boolean isDealEventForDialog() {
//        return ((isHuawei() || "samsung".equalsIgnoreCase(Build.BRAND)) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                || KSamSungUtil.isS5ModelOnly());
//    }

    public static boolean isHtc() {
        if (Build.MANUFACTURER.equalsIgnoreCase("htc")) {
            return true;
        }
        return false;
    }

    /**
     * 判断CM的ROM，基本是准的。
     *
     * @return
     */
    public static boolean isCyanogenMod() {
        String display = Build.DISPLAY;
        if (!TextUtils.isEmpty(display) && display.startsWith("cm_pisces")) {
            return true;
        }
        return false;
    }
}
