package com.stephen.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.stephen.bean.TopEvent;
import com.stephen.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * @author zhushuang
 * @data 2017/8/1.
 */

public class TopAccessibilityService extends AccessibilityService {


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event != null && event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            final CharSequence packageName = event.getPackageName();
            final String topAtyName = (String) event.getClassName();
            LogUtils.e(" TopAccessibilityService --> onAccessibilityEvent --> event--> " + event.getPackageName() + "   topAtyName = " + topAtyName);
            EventBus.getDefault().post(new TopEvent(packageName, topAtyName));
        }
    }

    @Override
    public void onInterrupt() {
        LogUtils.e(" TopAccessibilityService --> onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtils.e(" TopAccessibilityService ---> onServiceConnected --> connected successfully --- start show cover ");
        CoverService.startService(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(" TopAccessibility --> onDestroy ");
    }


}
