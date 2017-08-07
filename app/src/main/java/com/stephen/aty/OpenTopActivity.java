package com.stephen.aty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.stephen.R;
import com.stephen.util.LogUtils;

public class OpenTopActivity extends Activity implements View.OnClickListener {

    private static final String ACCESSIBILITY_SERVICE_NAME = "com.stephen.service.TopAccessibilityService";
    private TextView openAccessTv;
    private TextView tipsTv;

    public static void startOpenTopActivity(Context context){
        if (context == null){
            return;
        }
        Intent intent = new Intent(context, OpenTopActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_top);
        openAccessTv = (TextView) findViewById(R.id.tv_open_accessibility);
        openAccessTv.setOnClickListener(this);
        tipsTv = (TextView) findViewById(R.id.tv_tips);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (openAccessTv != null) {
            final boolean accessibilityPermission = isAccessibilitySettingsOn(this, ACCESSIBILITY_SERVICE_NAME);
            if (accessibilityPermission) {
                openAccessTv.setVisibility(View.GONE);
                tipsTv.setText(" Accessibility Service has been bonded! o(╯□╰)o");
            } else {
                openAccessTv.setVisibility(View.VISIBLE);
                tipsTv.setText("click the button to bind accessibility service.^(*￣(oo)￣)^");
            }
        }
    }


    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.tv_open_accessibility) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            try {
                startActivity(intent);
            } catch (Exception e) {
                LogUtils.e(" -- go to open accessibility setting page failed ! -- ");
                e.printStackTrace();
            }
        }
    }


    private static boolean isAccessibilitySettingsOn(Context mContext, String name) {
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
}
