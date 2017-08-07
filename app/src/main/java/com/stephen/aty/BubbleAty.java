package com.stephen.aty;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.stephen.R;
import com.stephen.util.LogUtils;
import com.stephen.views.BubbleView;

public class BubbleAty extends Activity implements BubbleView.ExplosionListener {

    private BubbleView bubbleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
        bubbleView = (BubbleView) findViewById(R.id.bubble);
        bubbleView.setExplosionListener(this);
    }

    @Override
    public void startDrag() {
        LogUtils.e(" --- start drag ---");
    }

    @Override
    public void releaseDragIn() {
        LogUtils.e(" -- release recover state -- ");
    }

    @Override
    public void releaseDragOut(float x, float y) {
        LogUtils.e(" -- release. dismiss -- ");
        Toast.makeText(this, "-- 爆炸动画 --", Toast.LENGTH_SHORT).show();
    }
}
