package com.stephen.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.stephen.R;
import com.stephen.evaluator.BezierEvaluator;
import com.stephen.util.DimenUtils;


/**
 * @author zhushuang
 * @data 2017/7/20.
 */

public class HeartView extends View {

    private static int[] sColors = new int[]{
            0xFFFF5050, 0xFF1AFF7B, 0xFFFFA11A, 0xFF1AA8FF, 0xFFC7FF1A, 0xFF88E5FF
    };
    private int mColorIndex;
    private ViewGroup mParent;
    private boolean isRunning = false;

    public HeartView(Context context) {
        this(context, null);
    }

    public HeartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    isRunning = true;
                    handler.removeCallbacks(heartRunnable);
                    handler.post(heartRunnable);
                } else {
                    isRunning = false;
                    handler.removeCallbacks(heartRunnable);
                    mColorIndex = 0;
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mParent = (ViewGroup) getParent();
    }

    private Drawable getHeartDrawable(Context context) {
        final VectorDrawableCompat drawableCompat = VectorDrawableCompat.create(context.getResources(), R.drawable.icon_like, null);
        drawableCompat.setTint(sColors[mColorIndex++ % sColors.length]);
        return drawableCompat.mutate();
    }

    private FrameLayout.LayoutParams getHeartLayoutParams() {
        Rect rect = new Rect();
        getHitRect(rect);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.width = DimenUtils.dp2px(100);
        params.height = DimenUtils.dp2px(100);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        return params;
    }

    private void addHeart() {
        if (mParent != null) {
            final Context context = getContext();
            ImageView heartView = new ImageView(context);
            heartView.setImageDrawable(getHeartDrawable(context));
            mParent.addView(heartView, getHeartLayoutParams());
//            startAnim(heartView);
            startBezierAnim(heartView);
        }
    }

    /**
     * 通过贝塞尔曲线 + TypeEvaluator实现动画
     * @param heartView
     */
    private void startBezierAnim(final ImageView heartView) {
        final ValueAnimator bezierAnimator = getBezierAnimator(heartView);
        bezierAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mParent != null) {
                    mParent.removeView(heartView);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (mParent != null) {
                    mParent.removeView(heartView);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        bezierAnimator.start();
    }

    /**
     * 通过普通的ObjectAnimator 实现动画
     * @param heartView
     */
    private void startAnim(final ImageView heartView) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(getAlphaAnimator(heartView),
                getHeartXAnimator(heartView),
                getHeartYAnimator(heartView),
                getScaleXAnimator(heartView),
                getScaleYAnimator(heartView));
        set.setDuration(1500);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mParent != null) {
                    mParent.removeView(heartView);
                }
            }
        });
        set.start();
    }

    private ValueAnimator getBezierAnimator(final ImageView heartView) {
        //tow controller points
        PointF pointF2 = new PointF(getWidth() / 4, getHeight() / 4);
        PointF pointF1 = new PointF(getWidth() * 3 / 4, getHeight() * 3 / 4);
        // start point and end point
        PointF pointF0 = new PointF((getWidth() - heartView.getWidth()) / 2, getHeight() - heartView.getHeight());
        PointF pointF3 = new PointF((getWidth() - heartView.getWidth()) / 2, 0);
        BezierEvaluator evaluator = new BezierEvaluator(pointF1, pointF2);
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, pointF0, pointF3);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                heartView.setX(pointF.x);
                heartView.setY(pointF.y);
                heartView.setAlpha(1 - animation.getAnimatedFraction() + 0.1f);
            }
        });
        animator.setTarget(heartView);
        animator.setDuration(3000);
        return animator;
    }


    private Animator getScaleXAnimator(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.SCALE_X, 1, 2);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    private Animator getScaleYAnimator(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1, 2);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    private Animator getAlphaAnimator(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0.0F, 1.0F, 0.0F);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

    private Animator getHeartXAnimator(View view) {
        final double random = Math.random();
        int offset = (int) (200 * Math.asin(random));
        if (random >= 0.5) {
            offset = -1 * offset;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, view.getTranslationX(), offset);
        animator.setInterpolator(new AnticipateOvershootInterpolator());
        return animator;
    }

    private Animator getHeartYAnimator(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0, -DimenUtils.dp2px(500));
        animator.setInterpolator(new AccelerateInterpolator());
        return animator;
    }

    private Runnable heartRunnable = new Runnable() {
        @Override
        public void run() {
            addHeart();
            if (isRunning) {
                postDelayed(this, 500);
            }
        }
    };

    private Handler handler = new Handler();

}
