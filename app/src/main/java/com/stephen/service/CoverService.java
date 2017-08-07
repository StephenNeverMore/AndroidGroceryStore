package com.stephen.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.stephen.R;
import com.stephen.base.MainApplication;
import com.stephen.bean.TopEvent;
import com.stephen.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CoverService extends Service {


    private WindowManager windowManager;
    private View rootView;
    private TextView pkgTv;
    private TextView atyTv;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(" -- CoverService --> onCreate ");

        startCover();
        EventBus.getDefault().register(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("  -- CoverService --> onDestroy ");
        EventBus.getDefault().unregister(this);
    }

    private void startCover() {
        windowManager = (WindowManager) MainApplication.getContext().getSystemService(WINDOW_SERVICE);
        rootView = LayoutInflater.from(MainApplication.getContext()).inflate(R.layout.layout_top_view, null);
        pkgTv = (TextView) rootView.findViewById(R.id.tv_top_pkg);
        atyTv = (TextView) rootView.findViewById(R.id.tv_top_aty);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        if (Build.VERSION.SDK_INT > 24) {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
//        params.systemUiVisibility = (Build.VERSION.SDK_INT >= 19 ? 0x1606 : 0x1604);
        params.format = PixelFormat.TRANSLUCENT;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        windowManager.addView(rootView, params);
    }


    public static WindowManager.LayoutParams getCoverLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT >= 19) { // 19以上透明通知栏和虚拟键
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.CENTER_VERTICAL;
            params.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            ;
        } else {
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                    | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            ;
        }
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        return params;
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, CoverService.class);
        context.startService(intent);
    }

    @Subscribe
    public void onEvent(TopEvent event){
        if (event == null){
            return;
        }
        final CharSequence packageName = event.pkg;
        final CharSequence className = event.name;
        if (pkgTv != null){
            pkgTv.setText(packageName);
        }
        if (atyTv != null){
            atyTv.setText(className);
        }
    }

}
