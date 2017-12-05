package com.stephen.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.stephen.R;
import com.stephen.aty.AutoScrollActivity;
import com.stephen.aty.BubbleAty;
import com.stephen.aty.CropActivity;
import com.stephen.aty.HeartActivity;
import com.stephen.aty.OpenTopActivity;
import com.stephen.aty.RevealActivity;
import com.stephen.aty.WaveAty;
import com.stephen.util.CommonUtils;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View statusBar = findViewById(R.id.status_bar);
        CommonUtils.setStatusBarColor(this, statusBar, R.color.tool_bar_color);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initToolBar();
    }

    private void initToolBar() {
        if (toolbar == null) {
            return;
        }
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.android_store);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.toolbar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void startNewAty(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }

    public void toHeartAty(View view) {
        startNewAty(this, HeartActivity.class);
    }

    public void toOpenTop(View view) {
        OpenTopActivity.startOpenTopActivity(this);
    }

    public void toWaveAty(View view) {
        startNewAty(this, WaveAty.class);
    }

    public void toBubbleAty(View view) {
        startNewAty(this, BubbleAty.class);
    }

    public void toRevealAty(View view) {
        startNewAty(this, RevealActivity.class);
    }

    public void toCropAty(View view) {
        startNewAty(this, CropActivity.class);
    }

    public void toAutoScrollAty(View view) {
        startNewAty(this, AutoScrollActivity.class);
    }
}
