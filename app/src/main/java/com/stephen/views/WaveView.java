package com.stephen.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.stephen.util.LogUtils;


/**
 * @author zhushuang
 * @data 2017/8/4.
 */

public class WaveView extends View {
    private static final int DEFAULT_WAVE_COUNT = 2;
    private static final int DEFAULT_WAVE_HEIGHT = 100;
    private static final int DEFAULT_WATER_HEIGHT = 50;
    private int count = DEFAULT_WAVE_COUNT;
    private float waveWidth;
    private int waveHeight = DEFAULT_WAVE_HEIGHT;
    private float waterHeightProgress = DEFAULT_WATER_HEIGHT;
    private float width;
    private float height;
    private Paint mPaint;
    private Path wavePath;
    private float offset = 0;
    private ValueAnimator animator;
    private boolean isWaving = false;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        wavePath = new Path();
        animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (float) animator.getAnimatedValue();
                invalidate();
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1000);//change this value to set the speed of wave
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        wavePath.reset();
        if (width == 0 || height == 0) {
            width = getWidth();
            height = getHeight();
        }
        float waterHeight = height * (1- waterHeightProgress / 100);
        LogUtils.e(" -- waterHeight = " + waterHeight);
        waveWidth = width / count;// wave's width
        final float upControlY = waterHeight - waveHeight;
        final float downControlY = waterHeight + waveHeight;
        offset *= width;
        wavePath.moveTo(-width + offset, waterHeight);
        for (int i = count - 1; i >= 0; i--) {
            wavePath.quadTo(-i * waveWidth - waveWidth * 3 / 4 + offset, upControlY, -i * waveWidth - waveWidth / 2 + offset, waterHeight);
            wavePath.quadTo(-i * waveWidth - waveWidth / 4 + offset, downControlY, -i * waveWidth + offset, waterHeight);
        }
        for (int i = 0; i < count; i++) {
            wavePath.quadTo(i * waveWidth + waveWidth / 4 + offset, upControlY, i * waveWidth + waveWidth / 2 + offset, waterHeight);
            wavePath.quadTo(i * waveWidth + waveWidth * 3 / 4 + offset, downControlY, i * waveWidth + waveWidth + offset, waterHeight);
        }
        wavePath.lineTo(width, height);
        wavePath.lineTo(-width, height);
        wavePath.close();
        canvas.drawPath(wavePath, mPaint);
    }

    public void startWave() {
        if (animator != null) {
            animator.start();
            isWaving = true;
        }
    }

    public void stopWave() {
        if (animator != null) {
            animator.cancel();
            isWaving = false;
        }
    }

    public boolean isWaving() {
        return isWaving;
    }

    public void setWaveHeight(int height) {
        this.waveHeight = height;
        invalidate();
    }

    public void setWaveCount(int count) {
        if (count <= 0) {
            return;
        }
        this.count = count;
        invalidate();
    }

    public void setWaterHeightProgress(float waterHeightProgress) {
        this.waterHeightProgress = waterHeightProgress;
    }
}
