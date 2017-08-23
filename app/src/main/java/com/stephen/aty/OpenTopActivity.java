package com.stephen.aty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.stephen.R;
import com.stephen.util.CommonUtils;
import com.stephen.util.LogUtils;
import com.stephen.util.PhoneModelUtils;

public class OpenTopActivity extends Activity implements View.OnClickListener {

    private static final String ACCESSIBILITY_SERVICE_NAME = "com.stephen.service.TopAccessibilityService";
    private TextView openAccessTv;
    private TextView openFloatTv;
    private TextView tipsTv;

    public static void startOpenTopActivity(Context context) {
        if (context == null) {
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
        openFloatTv = (TextView) findViewById(R.id.tv_open_float);
        openFloatTv.setOnClickListener(this);
        tipsTv = (TextView) findViewById(R.id.tv_tips);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (openAccessTv != null) {
            final boolean accessibilityPermission = CommonUtils.isAccessibilitySettingsOn(this, ACCESSIBILITY_SERVICE_NAME);
            if (accessibilityPermission) {
                openAccessTv.setVisibility(View.GONE);
                tipsTv.setText(" Accessibility Service has been bonded! o(╯□╰)o");
            } else {
                openAccessTv.setVisibility(View.VISIBLE);
                tipsTv.setText("click the button to bind accessibility service.^(*￣(oo)￣)^");
            }
        }

        if (openFloatTv != null) {
            if (CommonUtils.isAlertWindowPermissionEnabled()) {
                openFloatTv.setVisibility(View.GONE);
            } else {
                openFloatTv.setVisibility(View.VISIBLE);
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
        } else if (id == R.id.tv_open_float) {
            PhoneModelUtils.openWindowSettingByOneKey(this, false);
        }
    }


}
