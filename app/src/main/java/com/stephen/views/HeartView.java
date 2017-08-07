package com.stephen.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
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
        final VectorDrawableCompat drawableCompat = VectorDrawableCompat.create(context.getResources(), R.drawable.cmlocker_wallpaper_icon_like, null);
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
            startAnim(heartView);
        }
    }

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
            if (isRunning){
                postDelayed(this, 500);
            }
        }
    };

    private Handler handler = new Handler();

}
