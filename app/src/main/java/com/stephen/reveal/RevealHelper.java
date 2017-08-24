package com.stephen.reveal;

import android.animation.ValueAnimator;

/**
 * @author zhushuang
 * @data 2017/8/23.
 */

public class RevealHelper {

    public static ValueAnimator getRevealAnimator(float startR, float endR, long duration) {
        RevealTypeEvaluator typeValue = new RevealTypeEvaluator();
        ValueAnimator animator = ValueAnimator.ofObject(typeValue, startR, endR);
        animator.setDuration(duration);
        return animator;
    }


}
