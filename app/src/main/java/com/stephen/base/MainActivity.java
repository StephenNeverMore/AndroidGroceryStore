package com.stephen.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.stephen.R;
import com.stephen.aty.HeartActivity;
import com.stephen.aty.BubbleAty;
import com.stephen.aty.OpenTopActivity;
import com.stephen.aty.WaveAty;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


}
