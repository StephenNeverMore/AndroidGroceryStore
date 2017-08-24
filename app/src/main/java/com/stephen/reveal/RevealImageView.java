package com.stephen.reveal;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

/**
 * @author zhushuang
 * @data 2017/8/23.
 */

public class RevealImageView extends ImageView {
    private float radius;
    private Path mRevealPath;
    private int centerX;
    private int centerY;
    private boolean animate = false;

    public RevealImageView(Context context) {
        this(context, null);
    }

    public RevealImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startReveal();
            }
        });
        mRevealPath = new Path();
    }

    private void startReveal() {
        final int width = getWidth() / 2;
        final int height = getHeight() / 2;
        final int startR = (int) Math.sqrt(width * width + height * height);
        final ValueAnimator revealAnimator = RevealHelper.getRevealAnimator(startR, 0, 1000);
        revealAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                radius = (float) valueAnimator.getAnimatedValue();
                animate = true;
                invalidate();
            }
        });
        revealAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animate = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                animate = false;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        revealAnimator.setInterpolator(new AccelerateInterpolator());
        revealAnimator.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (animate) {
            if (centerX == 0 || centerY == 0) {
                centerX = getWidth() / 2;
                centerY = getHeight() / 2;
            }
            mRevealPath.reset();
            mRevealPath.addCircle(centerX, centerY, radius, Path.Direction.CW);
            canvas.clipPath(mRevealPath);
        }
        super.onDraw(canvas);
    }
}
