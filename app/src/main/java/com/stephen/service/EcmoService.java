package com.stephen.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.stephen.R;
import com.stephen.util.LogUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author zhushuang
 * @data 2017/8/1.
 */

public class EcmoService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startEcmoServiceNotification(this);
        stopSelf();
    }

    public static void startEcmoServiceNotification(Service context) {
        if (null == context) {
            return;
        }
        try {
            Drawable drawable = context.getResources().getDrawable(R.drawable.cmlauncher_smart_screen_logo);
            if (null == drawable) {
                LogUtils.e(" EcmoService --> startEcmoServiceNotification -- drawable is null");
                return;
            }
            Notification note = tryCreateNotification(context, R.drawable.cmlauncher_smart_screen_logo);
            if (null == note) {
                LogUtils.e(" EcmoService --> startEcmoServiceNotification -- note is null");
                return;
            }
            context.startForeground(1220, note);
        } catch (Exception e) {
            LogUtils.e(" EcmoService --> startEcmoServiceNotification -- Exception");
        }
    }

    public static Notification tryCreateNotification(Context context, int iconID) {
        try {
            Class<?> notifyBuilder = Class.forName("android.app.Notification$Builder");
            Constructor<?> ctor = notifyBuilder.getDeclaredConstructor(Context.class);
            ctor.setAccessible(true);
            Object obj = ctor.newInstance(context);

            Method methodSetSmallIcon = notifyBuilder.getDeclaredMethod("setSmallIcon", new Class[] { int.class });
            Method methodSetTicker = notifyBuilder.getDeclaredMethod("setTicker", new Class[] { CharSequence.class });
            Method methodSetAutoCancel = notifyBuilder.getDeclaredMethod("setAutoCancel", new Class[] { boolean.class });
            Method methodGetNotification = notifyBuilder.getDeclaredMethod("getNotification", new Class[] {});
            Method methodSetPriority = notifyBuilder.getDeclaredMethod("setPriority", new Class[] { int.class });
            Method methodSetWhen = notifyBuilder.getDeclaredMethod("setWhen", new Class[] { long.class });

            // Set Icon
            methodSetSmallIcon.invoke(obj, iconID);
            // Set Ticker Message
            //methodSetTicker.invoke(obj, "");
            // Dismiss Notification
            methodSetAutoCancel.invoke(obj, true);
            // Set Priority (PRIORITY_MIN)
            methodSetPriority.invoke(obj, -2);
            // Set When
            methodSetWhen.invoke(obj, 0);

            Notification notification = (Notification) methodGetNotification.invoke(obj);
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            return notification;
        } catch (Exception e) {
            //MyCrashHandler.getInstance().caughtNotificationResourceException(e);
        }

        return null;
    }
}
